import java.awt.image.BufferedImage;
import java.util.Map;

public class Enemy extends Player {

	protected int health;
	protected float speed;
	protected int damage;
	protected float vx;
	protected float vy;

	Enemy(BufferedImage _texture, int _x, int _y, int _w, int _h, int _health, float _speed, int _damage) {
		super(_texture, _x, _y, _w, _h, _damage, _speed, _damage);
		health = _health;
		speed = _speed;
		damage = _damage;
		x = 50;
		y = 50;
	}

	public void updatePhysics() {

		int xx = (int) (x + vx);
		int yy = (int) (y + vy);

		Player target = nearestPLayer();

		if (target != null) {
			if (target.x < x) {
				x--;
			} else if (target.x > x) {
				x++;
			}

			if (target.y < y) {
				y--;
			} else if (target.y > y) {
				y++;
			}
		}

//		if (Game.mapStr.charAt((xx / 32) + (yy / 32) * 32) == '1') {
//
//		} else {
			x += vx;
			y += vy;
		//}

		vx = 0;
		vy = 0;
	}

	public Player nearestPLayer() {
		Map<Integer, Player> players = Game.players;
		for (int i = 0; i < Game.players.size(); i++) {
			if (players.get(i) instanceof Player) {
				Player p = players.get(i);
				if (Math.abs(x - p.x) < 20) {
					System.out.println("Player in range");
					return p;
				}
			}
		}
		return null;
	}

	public void onThink(Engine engine) {
		// updatePhysics();
		Player target = nearestPLayer();

		if (target != null) {
			if (target.x < x) {
				x--;
			} else if (target.x > x) {
				x++;
			}

			if (target.y < y) {
				y--;
			} else if (target.y > y) {
				y++;
			}
		}
	}
}
