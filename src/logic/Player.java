package logic;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import it.marteEngine.entity.Entity;

public class Player extends Entity{
	//������, �� ������������ ��������� - �����
	//��������� ��������
	public static final String ANIM_HIT = "anim_hit";
	public static final String ANIM_LEFT = "anim_left";
	public static final String ANIM_RIGHT = "anim_right";
	public static final String ANIM_UP = "anim_up";
	public static final String ANIM_DOWN = "anim_down";
	public static final String ANIM_CALM = "anim_calm";
	public static final String ANIM_DEATH = "anim_death";
	public static final String ANIM_WOUNDED = "anim_wouded";
	
	//��������� ������������ ��� �����
	private final String LEFT = "left";
	private final String RIGHT = "right";
	private final String UP = "up";
	private final String DOWN = "down";
	private final String ATTACK = "attack";
		
	//������� �����
	//�������� 
	public float health = 100;	
	//����������� ������, ��������� �������� ���� �����
	public float defence = 0.3f;
	//���������� �����, ��������� ��� ����
	public float atack = 0.1f;	
	//����, ������������ ������ �������� �� ������
	public float damage = 10;
	
	//������������� ���������� ��������
	private boolean isCooldown = false;
	//������� ��������
	private int cooldown = 0;
	
	public static String ENEMY = "enemy";
	
	//�������� ��� (��� ���)
	public boolean isAlive = true;

	//����������� ������������� ��������
	Rectangle rect;

	public Player(float x, float y) throws SlickException {
		super(x, y);
		//�������� ������� ��������
		width = 40;
		height = 58;
		//��������� �����������
		SpriteSheet sheet_hit = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_left = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_right = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_up = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_down = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_wounded = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_death = new SpriteSheet("textures/char.png",width,height);
		setGraphic(new Image("textures/char.png"));
		//������� �������� ����� ������ �����������
		Image[] arr_calm = {
				new Image("textures/char.png"), 
				new Image("textures/char.png"),
				new Image("textures/char.png"),
				new Image("textures/char.png")};
		//����������� ��������
		addAnimation(ANIM_CALM, new Animation(arr_calm, 40));
		addAnimation(ANIM_HIT, new Animation(sheet_hit, 40));
		addAnimation(ANIM_LEFT, new Animation(sheet_left, 40));
		addAnimation(ANIM_RIGHT, new Animation(sheet_right, 40));
		addAnimation(ANIM_UP, new Animation(sheet_up, 40));
		addAnimation(ANIM_DOWN, new Animation(sheet_down, 40));
		addAnimation(ANIM_WOUNDED, new Animation(sheet_wounded, 40));
		addAnimation(ANIM_DEATH, new Animation(sheet_death, 40));
		addAnimation(ANIM_DEATH, new Animation(sheet_death, 40));
		setAnim(ANIM_CALM);
		//������� ������� ������� ��� ����������
		define("right", Input.KEY_D);
		define("left", Input.KEY_A);
		define("up", Input.KEY_W);
		define("down", Input.KEY_S);
		define("attack", Input.KEY_ENTER);
		//����� ��� ��������, ����� ��������� �� ��� ����������� ������������
		addType(PLAYER);
		//����� ������� ��� ����������� �����������
		setHitBox(0, 0, width, height);
		//���� ��������
		rect = new Rectangle(0, 0, width, height);
		}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
		if(check(LEFT)) {
			System.out.println("keke");
		}
		//���� �������� ���, ��� ������ �����������
		if(isAlive) {
		//�����������
		if(check(LEFT)&&collide(ENEMY, x-2, y)==null) {
			setAnim(ANIM_LEFT); 
			x--;
		}
		else if(check(RIGHT)&&collide(ENEMY, x+2, y)==null){
			setAnim(ANIM_RIGHT); 
			x++;
		}
		else if(check(UP)&&collide(ENEMY, x, y-2)==null) {
			setAnim(ANIM_UP); 
			y--;
		}		
		else if(check(DOWN)&&collide(ENEMY, x, y+height+2)==null) {
			setAnim(ANIM_DOWN); 
			y++;
		}
		else setAnim(ANIM_CALM);
		//���� �������� �������..
		if(check(ATTACK)) {
			//..� ������� ��� ��� ���������..
			if (cooldown <= 0) {
				//..�� ���� ����� � ������� ������������..
				Enemy enemy = checkEnemy();
				//..� ���� ������� �������, ������� ���
				if (enemy != null) {
					hit(enemy);
				}
				//���� ������, ������������� �������
				isCooldown = true;
				cooldown = 20;
			}
		}
		//��� ������� ��������� ����������� ��������
		if(isCooldown) {
			if (cooldown > 0) cooldown--;
			else isCooldown = false;
		}
		}
		

		rect.setX(x);
		rect.setY(y);
	}
	
	/**����� ����������� ������ ������*/
	//TODO: ���������� � checkTarget, ��� �������������� � �����
	private Enemy checkEnemy() {
		Entity enemy = null;
		if(collide(ENEMY, x+4, y)!=null) enemy = collide(ENEMY, x+4, y);
		if(collide(ENEMY, x-4, y)!=null) enemy = collide(ENEMY, x-4, y);
		//�� �� ������, ����� ���� �����
		//if(collide(ENEMY, x, y+2)!=null) enemy = collide(ENEMY, x, y+2);
		if(collide(ENEMY, x, y-4)!=null) enemy = collide(ENEMY, x, y-4);			
		if(enemy!=null)return (Enemy) enemy;
		else return null;
	} 
	
	private void hit(Enemy enemy) {
		setAnim(ANIM_HIT);
		//�������� ���� � ������ ������������ �����
		float real_damage = this.damage + (this.damage * this.atack);
		//�������� ���� ����������� ������������� ������ ����������
		float reduced_damage = real_damage - (real_damage * enemy.defence); 
		//������ �� ����� ����������
		enemy.health -= reduced_damage;
		if(enemy.health>0) enemy.setAnim(ANIM_WOUNDED);
		else {
			enemy.setAnim(ANIM_DEATH);
			enemy.isAlive = false;
		}
	}
	
	@Override
	public void addAnimation(String animName, Animation animation) {
		//��������������� ������������ ������ MarteEngine, 
		//����� �������� ��-������� �� �������������, ��� �����
		animation.setLooping(false);
		super.addAnimation(animName, animation);
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
		g.drawString(""+(int)health, x, y+height);
		g.draw(rect);
	}
}
