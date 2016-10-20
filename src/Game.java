import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Game extends Engine {

	public static class Pow extends Engine.RenderTex {

		int cx;
		int cy;

		Pow(Engine engine, int _x, int _y) {
			super(engine.getTexture("pow.png"), _x - 32, _y - 32, 32, 32);

			cx = _x - 32;
			cy = _y - 32;
		}

		public boolean expand() {

			w += 5;
			h += 5;

			x = cx - w / 2;
			y = cy - h / 2;
			if (w > 200) {
				return false;
			}
			return true;
		}
	}

	enum SceneType {
		GAME_LOAD, GAME_INIT, GAME_PLAY,
	}

	static Map<Integer, Player> players;
	SceneType currScene;
	NetworkManager net;
	User me;
	ArrayList<Pow> pows = new ArrayList<Pow>();

	Game() {

		players = new HashMap<Integer, Player>();
		currScene = SceneType.GAME_LOAD;
		net = new NetworkManager();

		net.sendObjects("_i");

		me = null;
	}

	public static String mapStr;

	private void loadAllImages(Engine engine) {

		engine.loadTexture("tiles.png");
		engine.loadTexture("pow.png");
		engine.loadTexture("ded.png");

		mapTex = engine.getTexture("tiles.png");

		mapStr = "";
		mapStr += "1111111111111111111111111111111";
		mapStr += "1001100000001000000100000000001";
		mapStr += "1000000000000000000100000000001";
		mapStr += "1000002000000000000000000000001";
		mapStr += "1000002220000200000002000000001";
		mapStr += "1220021112222222000022200000001";
		mapStr += "1120211111111111200011111100221";
		mapStr += "1120211111100000001111111110111";
		mapStr += "1120211111100000000000011110111";
		mapStr += "1120211001100200020001000000111";
		mapStr += "1120000000000002222011111111111";
		mapStr += "1122222220222221100000111111111";
		mapStr += "1111111110000000000001111111111";
		mapStr += "1111111111111111111111111111111";
		mapStr += "1111111111111111111111111111111";
		mapStr += "1111111111111111111111111111111";
		mapStr += "1111111111111111111111111111111";
		mapStr += "1111111111111111111111111111111";
		mapStr += "1111111111111111111111111111111";

		map = new int[Engine.MAPY][];
		for (int i = 0; i < Engine.MAPY; i++) {
			map[i] = new int[Engine.MAPX];
			for (int j = 0; j < Engine.MAPX; j++) {

				map[i][j] = mapStr.charAt(i * Engine.MAPX + j) - 48;
			}
		}
	}

	public static void main(String[] args) {

		Game game = new Game();

		game.start();
	}

	int counter = 0;

	public void onUpdate() {
		switch (currScene) {
		case GAME_LOAD:

			loadAllImages(this);
			currScene = SceneType.GAME_INIT;

			break;
		case GAME_INIT:

			currScene = SceneType.GAME_PLAY;

			break;
		case GAME_PLAY:

			parseNetwork();

			Iterator it = players.entrySet().iterator();
			while (it.hasNext()) {

				Map.Entry pair = (Map.Entry) it.next();
				Player player = (Player) pair.getValue();
				if (player.isAlive) {
					player.onThink(this);
				} else {
					player.texture = this.getTexture("ded.png");
					player.crop = 0;
				}

				player.updatePhysics();
			}

			if ((counter++ % 4) == 0) {
				sendToNetwork();
			}

			for (int i = pows.size(); i-- > 0;) {

				Pow pow = pows.get(i);
				if (!pow.expand()) {

					renderTexQueue.remove(pow);
					pows.remove(i);
				}
			}

			break;
		}
	}

	boolean[] keys = new boolean[5];
	public static final int KEY_W = 0;
	public static final int KEY_A = 1;
	public static final int KEY_S = 2;
	public static final int KEY_D = 3;
	public static final int KEY_SPACE = 4;

	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {
		case 87: // W
			keys[KEY_W] = true;
			break;
		case 65: // A
			keys[KEY_A] = true;
			break;
		case 83: // S
			keys[KEY_S] = true;
			break;
		case 68: // D
			keys[KEY_D] = true;
			break;
		case 32: // Space
			keys[KEY_SPACE] = true;
			break;
		}
	}

	public void keyReleased(KeyEvent e) {

		switch (e.getKeyCode()) {
		case 87: // W
			keys[KEY_W] = false;
			break;
		case 65: // A
			keys[KEY_A] = false;
			break;
		case 83: // S
			keys[KEY_S] = false;
			break;
		case 68: // D
			keys[KEY_D] = false;
			break;
		case 32: // Space
			keys[KEY_SPACE] = false;
			break;
		}
	}

	public void registerPlayer(int key, Player player) {

		players.put(key, player);
		player.id = key;
		renderTexQueue.add(player);

		player.initTexture(this);
	}

	public void keyTyped(KeyEvent e) {

	}

	public void tickPhysics(Player player) {

	}

	public User getMe() {
		return me;
	}

	private int meId = -1;

	public void parseNetwork() {

		String raw = net.recieveObjects();
		if (raw == null || raw.length() == 0) {
			return;
		}
		System.out.println("[" + raw + "]");
		String[] netcodes = raw.split("_");

		for (int i = 0; i < netcodes.length; i++) {

			String netcode = netcodes[i];
			if (netcode.length() == 0) {
				continue;
			}
			switch (netcode.charAt(0)) {
			case 'c': // Confirm client attack
			{
				int cId = Integer.parseInt(netcode.substring(1, netcode.length()));
				if (players.containsKey(cId)) {

					Player target = players.get(cId);
					if (!target.isAlive) {
						break;
					}

					Pow pow = new Pow(this, target.x + target.w / 2, target.y + target.h / 2);
					pows.add(pow);
					this.renderTexQueue.add(pow);
					audio.play("PUNCH.wav");
				}
				break;
			}
			case 'd': // player died
			{
				int cId = Integer.parseInt(netcode.substring(1, netcode.length()));
				if (players.containsKey(cId)) {

					Player target = players.get(cId);

					target.isAlive = false;
				}
				break;
			}
			case 'a': // Attack client ID
			{
				int cId = Integer.parseInt(netcode.substring(1, netcode.length()));
				if (players.containsKey(cId) && me != null && meId != cId) {

					Player attacker = players.get(cId);

					if (Math.abs(attacker.x - me.x) < 60 && Math.abs(attacker.y - me.y) < 80) {

						if (attacker.x < me.x) {
							me.vx = 7;
						} else {
							me.vx = -7;
						}
						me.vy = -7;

						me.health -= attacker.damage;
						conStr = "_c";

						if (me.health <= 0) {

							conStr = "_c_d";
						}
					}
				}
			}
				break;
			case 'i': // Parse client ID
			{
				meId = Integer.parseInt(netcode.substring(1, netcode.length()));
				me = new User(this, 100, 100, 32, 32, 100, 2, 50);
				registerPlayer(meId, me);

				System.out.println("Client ID #" + meId);
			}
				break;
			case 'p': // Parse client position
			{
				int lastJ = 1;
				int mode = 0;
				int cId = -1;
				int x = 0;
				int y = 0;
				float vx = 0;
				for (int j = 1; j < netcode.length(); j++) {

					if (netcode.charAt(j) == ',') {

						switch (mode) {
						case 0:
							cId = Integer.parseInt(netcode.substring(lastJ, j));
							lastJ = j + 1;
							mode++;
							break;
						case 1:
							x = Integer.parseInt(netcode.substring(lastJ, j));
							lastJ = j + 1;
							mode++;
							break;
						case 2:
							y = Integer.parseInt(netcode.substring(lastJ, j));
							lastJ = j + 1;
							mode++;
							break;
						case 3:
							vx = Float.parseFloat(netcode.substring(lastJ, j));
							lastJ = j + 1;
							break;
						}
					}
				}

				float vy = Float.parseFloat(netcode.substring(lastJ, netcode.length()));

				if (cId == -1) {
					break;
				}

				Player player = players.get(cId);
				if (player == null) {
					player = new User(this, 200, 200, 32, 32, 100, 3, 5);
					registerPlayer(cId, player);
				}

				player.x = (x + player.x) / 2;
				player.y = (y + player.y) / 2;
				player.vx = vx;
				player.vy = vy;
			}
				break;
			}
		}
	}

	public static String attStr = "";
	public static String conStr = "";

	int lastSend = 0;

	public void sendToNetwork() {

		String raw = "";

		// Send my position
		if (me != null) {

			if (me.vx == 0 && me.vy == 0) {
				lastSend--;
			} else {
				lastSend = 1;
			}
			if (lastSend >= 0) {
				raw += "_p" + Integer.toString(me.x) + "," + Integer.toString(me.y) + "," + Float.toString(me.vx) + ","
						+ Float.toString(me.vy);
			}
		}

		raw += attStr;
		raw += conStr;

		if (raw.length() != 0) {
			net.sendObjects(raw);

			if (conStr.length() != 0) {
				conStr = "";
			}
		}
	}
}
