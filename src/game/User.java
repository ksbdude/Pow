package game;

public class User extends Player{

	User( Engine engine, int _x, int _y, int _w, int _h, int _health, int _speed, int _damage ){
		super(null, _x, _y, _w, _h, _health, _speed, _damage);
	}
	
	private int timer = 0;
	private boolean attacking = false;

	public void onThink(Engine engine) {
		
		Game game = (Game)engine;
		
		if( game.getMe() == this ){
			if( game.keys[Game.KEY_A] ){
				vx = -speed;
				flipped = true;
			}else if( game.keys[Game.KEY_D] ){
				vx = speed;
				flipped = false;
			}else if( onGround ){
				vx = 0.0f;
			}
			
			if( game.keys[Game.KEY_W] && onGround ){
				vy = -6;
			}
			
			timer++;
			if( game.keys[ Game.KEY_SPACE] && !attacking ){
				
				if( timer > 30 ){
					
					timer = 0;
					attacking = true;
					crop = 3;
					Game.attStr = "_a";
				}
			}
			if( attacking ){
				
				if( timer > 3 ){
					attacking = false;
					timer = 0;
					crop = 0;
					Game.attStr = "";
				}
			}
		}
		
		
		if( crop == 3 ){
			return;
		}
		if( vx == 0 ){
			crop = 0;
		}else{
			if( onGround ){
				crop++;
			}else{
				crop = 1;
			}
			if( crop > 2 ){
				crop = 0;
			}
		}
	}

}
