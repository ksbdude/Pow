package game;

import java.awt.image.BufferedImage;

public abstract class Player extends Engine.RenderTex{
	
	protected int health;
	protected float speed;
	protected int damage;
	protected float vx;
	protected float vy;
	protected boolean onGround;
	public boolean isAlive = true;
	public int id;
	
	Player( BufferedImage _texture, int _x, int _y, int _w, int _h, int _health, float _speed, int _damage ){
		super(_texture,_x,_y,_w,_h);
		health = _health;
		speed = _speed;
		damage = _damage;
		onGround = false;
	}
	
	public void initTexture( Engine engine ){
		
		String texName = "team0"+Integer.toString(id % 4)+".png";
		if( engine.getTexture(texName) == null ){
			engine.loadTexture( texName );
		}
		texture = engine.getTexture( texName );

	}
	
	public int getHealth(){
		return health;
	}
	
	public void updatePhysics() {
		vy += 0.2f;
		
		int sqx = (x+w/2)/Engine.SQSIZE;
		int sqy = (y+h/2)/Engine.SQSIZE;

		{
			int index = sqy*Engine.MAPX+sqx+1;
			if( index < Engine.MAPX*Engine.MAPY && index >= 0 ){
				if( Game.mapStr.charAt( index ) != '0' && x+w+vx > (sqx+1)*Engine.SQSIZE ){
					vx = 0;
					x = (sqx+1)*Engine.SQSIZE-w;
				}
			}
		}
		{
			int index = sqy*Engine.MAPX+sqx-1;
			if( index < Engine.MAPX*Engine.MAPY && index >= 0 ){
				if( Game.mapStr.charAt( index ) != '0' && x+vx < sqx*Engine.SQSIZE+5 ){
					vx = 0;
					x = sqx*Engine.SQSIZE+5;
				}
			}
		}
		{
			onGround = false;
			int index = (sqy+1)*Engine.MAPX+sqx;
			if( index < Engine.MAPX*Engine.MAPY && index >= 0 ){
				if( Game.mapStr.charAt( index ) != '0' && y+h+vy > (sqy+1)*Engine.SQSIZE ){
					vy = 0;
					y = (sqy+1)*Engine.SQSIZE-h;
					onGround = true;
				}
			}
		}
		{
			int index = (sqy-1)*Engine.MAPX+sqx;
			if( index < Engine.MAPX*Engine.MAPY && index >= 0 ){
				if( Game.mapStr.charAt( index ) != '0' && y+vy < sqy*Engine.SQSIZE+5 ){
					vy = 0;
					y = sqy*Engine.SQSIZE+5;
				}
			}
		}
		
		x += vx;
		y += vy;
	}
	
	public abstract void onThink( Engine engine );
}
