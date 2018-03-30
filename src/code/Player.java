package code;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import it.marteEngine.entity.Entity;

public class Player extends Entity{
	public static final String ANIM_HIT = "anim_hit";
	public static final String ANIM_LEFT = "anim_left";
	public static final String ANIM_RIGHT = "anim_right";
	public static final String ANIM_UP = "anim_up";
	public static final String ANIM_DOWN = "anim_down";
	public static final String ANIM_CALM = "anim_calm";
	public static final String ANIM_DEATH = "anim_death";
	public static final String ANIM_WOUNDED = "anim_wouded";
	
	private final String LEFT = "left";
	private final String RIGHT = "right";
	private final String UP = "up";
	private final String DOWN = "down";
	private final String ATTACK = "attack";		
	
	public float health = 100;
	public float defence = 0.1f;
	public float atack = 0.1f;
	public float damage = 10;
	
	public static String ENEMY = "enemy";
	
	Input input;
	public boolean isAlive = true;

	public Player(float x, float y) throws SlickException {
		super(x, y);
		width = 20;
		height = 40;
		SpriteSheet sheet_hit = new SpriteSheet("textures/hit.png",width,height);
		SpriteSheet sheet_left = new SpriteSheet("textures/left.png",width,height);
		SpriteSheet sheet_right = new SpriteSheet("textures/right.png",width,height);
		SpriteSheet sheet_up = new SpriteSheet("textures/up.png",width,height);
		SpriteSheet sheet_down = new SpriteSheet("textures/down.png",width,height);
		SpriteSheet sheet_wounded = new SpriteSheet("textures/wounded.png",width,height);
		setGraphic(new Image("D:/Data/player.png"));
		Image[] arr_calm = {
				new Image("textures/calm.png"), 
				new Image("textures/scratching_ass1.png"),
				new Image("textures/scratching_ass2.png"),
				new Image("textures/scratching_ass3.png")};
		addAnimation(ANIM_CALM, new Animation(arr_calm, 40));
		addAnimation(ANIM_HIT, new Animation(sheet_hit, 40));
		addAnimation(ANIM_LEFT, new Animation(sheet_left, 40));
		addAnimation(ANIM_RIGHT, new Animation(sheet_right, 40));
		addAnimation(ANIM_UP, new Animation(sheet_up, 40));
		addAnimation(ANIM_DOWN, new Animation(sheet_down, 40));
		addAnimation(ANIM_WOUNDED, new Animation(sheet_wounded, 40));
		setAnim(ANIM_CALM);//ƒольше, но использовать константы - добро
		define("RIGHT", Input.KEY_D);
		define("LEFT", Input.KEY_A);
		define("UP", Input.KEY_W);
		define("DOWN", Input.KEY_S);
		define("ATTACK", Input.MOUSE_LEFT_BUTTON);
		addType(PLAYER);
		setHitBox(0, 0, width, height);
		}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
		if(input==null)input = container.getInput();
		if(check(LEFT)&&collide(SOLID, x-2, y)==null) {
			setAnim(ANIM_LEFT); 
			x--;
		}
		else if(check(RIGHT)&&collide(SOLID, x+width+2, y)==null){
			setAnim(ANIM_RIGHT); 
			x++;
		}
		else if(check(UP)&&collide(SOLID, x, y-2)==null) {
			setAnim(ANIM_UP); 
			y--;
		}		
		else if(check(DOWN)&&collide(SOLID, x, y+height+2)==null) {
			setAnim(ANIM_DOWN); 
			y++;
		}
		else if(check(ATTACK)&&checkEnemy()!=null) hit(checkEnemy());
		else setAnim(ANIM_CALM);
	}
	
	private Enemy checkEnemy() {
		Entity enemy = null;
		if(collide(ENEMY, x+2, y)!=null) enemy = collide(ENEMY, x+2, y);
		if(collide(ENEMY, x-2, y)!=null) enemy = collide(ENEMY, x-2, y);
		if(collide(ENEMY, x, y+2)!=null) enemy = collide(ENEMY, x, y+2);
		if(collide(ENEMY, x, y-2)!=null) enemy = collide(ENEMY, x, y-2);			
		if(enemy!=null)return (Enemy) enemy;
		else return null;
	} 
	
	private void hit(Enemy enemy) {
		setAnim(ANIM_HIT);
		float real_damage = this.damage + (this.damage * this.atack);
		float reduced_damage = real_damage - (real_damage * enemy.defence); 
		enemy.health -= reduced_damage;
		if(enemy.health>0) enemy.setAnim(ANIM_WOUNDED);
		else setAnim(ANIM_DEATH);
	}

}
