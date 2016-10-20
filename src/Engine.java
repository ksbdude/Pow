import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class Engine extends Canvas implements KeyListener{
	AudioManager audio = new AudioManager();
	public static abstract class RenderTex{

		BufferedImage texture;
		int x;
		int y;
		int w;
		int h;
		int crop;
		boolean flipped = false;

		RenderTex( BufferedImage _texture, int _x, int _y, int _w, int _h ){
			texture = _texture;
			x = _x;
			y = _y;
			w = _w;
			h = _h;
			crop = 0;
		}
	}

	Map<String,BufferedImage> textures;
	ArrayList<RenderTex> renderTexQueue;
	int[][] map;
	public static final int MAPX = 31;
	public static final int MAPY = 19;
	BufferedImage mapTex;
	BufferStrategy bs;
	int screenW;
	int screenH;

	public static final int SQSIZE = 42;

	Engine(){

		screenW = 1280;
		screenH = 720;

		JFrame frame = new JFrame("Network game");
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize( screenW, screenH );
        frame.setVisible(true);
        frame.setResizable(false);
        requestFocus();
        addKeyListener( this );

        createBufferStrategy(3);
        bs = this.getBufferStrategy();

		textures = new HashMap<String,BufferedImage>();
		renderTexQueue = new ArrayList<RenderTex>();
		map = null;
		mapTex = null;
		audio.play("level3.wav");
	}

	public void render( Graphics2D g ){

		g.setBackground( new Color( 20, 20, 50 ) );
		g.clearRect( 0, 0, screenW, screenH );

		if( map != null ){

			int xMin = 0;
			int xMax = Engine.MAPX;
			int yMin = 0;
			int yMax = Engine.MAPY;

			int y = 0;
			for( int i=yMin; i<yMax; i++ ){

				int x = 0;
				for( int j=xMin; j<xMax; j++ ){

					int crop = map[i][j];
					if( crop == 0 ){
						x += SQSIZE;
						continue;
					}
					int cy = crop/2;
					int cx = crop-cy*2;

					cx *= 64;
					cy *= 64;

					g.drawImage( mapTex, x, y, x+SQSIZE, y+SQSIZE, cx, cy, cx+64, cy+64, null );
					x += SQSIZE;
				}

				y += SQSIZE;
			}
		}

		for( int i=0; i<renderTexQueue.size(); i++ ){

			RenderTex obj = renderTexQueue.get(i);

			int cy = obj.crop/2;
			int cx = obj.crop-cy*2;

			cx *= 128;
			cy *= 128;

			if( obj.flipped ){
				g.drawImage( obj.texture, obj.x+obj.w, obj.y, obj.x, obj.y+obj.h, cx, cy, cx+128, cy+128,null );
			}else{
				g.drawImage( obj.texture, obj.x, obj.y, obj.x+obj.w, obj.y+obj.h, cx, cy, cx+128, cy+128,null );
			}
		}
	}

	public abstract void onUpdate();

	public void start() {

		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60;
		double delta = 0;
		requestFocus();

		while (true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				onUpdate();
				delta--;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
			}
			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
			render(g);
			g.dispose();
			bs.show();

			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {

			}
		}
	}

	public void loadTexture( String string ){

		try{

			BufferedImage tex = ImageIO.read( new File( string ) );
			textures.put( string, tex );
		}catch( Exception e ){
		}
	}

	public BufferedImage getTexture(String string) {
		return textures.get( string );
	}
}
