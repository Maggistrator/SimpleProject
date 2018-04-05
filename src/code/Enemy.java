package code;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import it.marteEngine.entity.Entity;

/**
 * ��-����, ����� player, �� � ���������������
 * ����� ����� �������� ��������� �������������, 
 * �� � ����� ����������� ������������ ������������
 * */
public class Enemy extends Entity{
 
	public static final String ANIM_HIT = "anim_hit";
	public static final String ANIM_LEFT = "anim_left";
	public static final String ANIM_RIGHT = "anim_right";
	public static final String ANIM_UP = "anim_up";
	public static final String ANIM_DOWN = "anim_down";
	public static final String ANIM_CALM = "anim_calm";
	public static final String ANIM_DEATH = "anim_death";
	public static final String ANIM_WOUNDED = "anim_wouded";
	
	//��������� ����������� ��� ����������
	private final String LEFT = "left";
	private final String RIGHT = "right";
	private final String UP = "up";
	private final String DOWN = "down";
	
	//������������� ���������� ��������
	private boolean isCooldown = false;
	
	//������� �����
	//�������� 
	public float health = 60;	
	//����������� ������, ��������� �������� ���� �����
	public float defence = 0.1f;
	//���������� �����, ��������� ��� ����
	public float atack = 0.2f;	
	//����, ������������ ������ �������� �� ������
	public float damage = 10;
	
	//���������� �������� ��������
	private int cooldown = 0;
	
	public static String ENEMY = "enemy";
	
	public final String STRATEGY_PATROL = "patrol";
	private Strategy strategy = null;
	
	private float newx = x;
	private float newy = y;
	
	Player target;
	
	//�������� ��� (��� ���)
	public boolean isAlive = true;

	public Enemy(float x, float y) throws SlickException {
		super(x, y);
		//�������� ������� ��������
		width = 20;
		height = 40;
		//��������� �����������
		SpriteSheet sheet_hit = new SpriteSheet("textures/hit.png",width,height);
		SpriteSheet sheet_left = new SpriteSheet("textures/left.png",width,height);
		SpriteSheet sheet_right = new SpriteSheet("textures/right.png",width,height);
		SpriteSheet sheet_up = new SpriteSheet("textures/up.png",width,height);
		SpriteSheet sheet_down = new SpriteSheet("textures/down.png",width,height);
		SpriteSheet sheet_wounded = new SpriteSheet("textures/wounded.png",width,height);
		setGraphic(new Image("D:/Data/player.png"));
		//������� �������� ����� ������ �����������
		Image[] arr_calm = {
				new Image("textures/calm.png"), 
				new Image("textures/scratching_ass1.png"),
				new Image("textures/scratching_ass2.png"),
				new Image("textures/scratching_ass3.png")};
		//����������� ��������
		addAnimation(ANIM_CALM, new Animation(arr_calm, 40));
		addAnimation(ANIM_HIT, new Animation(sheet_hit, 40));
		addAnimation(ANIM_LEFT, new Animation(sheet_left, 40));
		addAnimation(ANIM_RIGHT, new Animation(sheet_right, 40));
		addAnimation(ANIM_UP, new Animation(sheet_up, 40));
		addAnimation(ANIM_DOWN, new Animation(sheet_down, 40));
		addAnimation(ANIM_WOUNDED, new Animation(sheet_wounded, 40));
		setAnim(ANIM_CALM);
		//� ������ ������, ���������� ��������������, ������ �� �������
		//����� ��� ��������, ����� ��������� �� ��� ����������� ������������
		addType(ENEMY);
		setStrategy(new PatrolStrategy(this));
		//����� ������� ��� ����������� �����������
		setHitBox(0, 0, width, height);
		}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
		if(isAlive) {
		//���� ���� ������� ����� - ������� � ���

		if(checkPlayer()==null) {
		if(newx<x) move(LEFT); else move(RIGHT);
		if(newy<y) move(UP); else move(DOWN);
		}
		
		strategy.act();
		
		//���� ����������� ����� - ������� ���
		if(cooldown<=0) {
			target = checkPlayer();
			if(target!=null) hit(target);
			cooldown = 50;
			isCooldown = true;
		}
		//����� ��������� ����
		
		//��� ������� ��������� ����������� ��������
		if(isCooldown) {
			if (cooldown > 0) cooldown--;
			else isCooldown = false;
		}}
	}
	
	protected Player checkPlayer() {
		Entity player = null;
		if(collide(PLAYER, x+2, y)!=null) player = collide(PLAYER, x+2, y);
		if(collide(PLAYER, x-2, y)!=null) player = collide(PLAYER, x-2, y);
		//�� �� ������, ����� ���� �����
		//if(collide(PLAYER, x, y+2)!=null) player = collide(PLAYER, x, y+2);
		if(collide(PLAYER, x, y-2)!=null) player = collide(PLAYER, x, y-2);			
		if(player!=null)return (Player) player;
		else return null;
	} 
	
	private void hit(Player player) {
		setAnim(ANIM_HIT);
		//�������� ���� � ������ ������������ �����
		float real_damage = this.damage + (this.damage * this.atack);
		//�������� ���� ����������� ������������� ������ ����������
		float reduced_damage = real_damage - (real_damage * player.defence); 
		//������ �� ����� ����������
		player.health -= reduced_damage;
		if(player.health>0) player.setAnim(ANIM_WOUNDED);
		else {
			setAnim(ANIM_DEATH);
			isAlive = false;
		}
	}
	
	public void move(float x, float y) {
		newx = x; newy = y;
	}
	
	private void move(String dir) {
		switch(dir) {
		case LEFT:
			if(collide(SOLID, x-2, y)==null) {
				setAnim(ANIM_LEFT); 
				x--;
			}
			break;
		case RIGHT:
			if(collide(SOLID, x+width+2, y)==null){
				setAnim(ANIM_RIGHT); 
				x++;
			}
			break;
		case UP:
			if(collide(SOLID, x, y-2)==null) {
				setAnim(ANIM_UP); 
				y--;
			}		
			break;
		case DOWN:
			if(collide(SOLID, x, y+height+2)==null) {
				setAnim(ANIM_DOWN); 
				y++;
			}
			break;
		}
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	@Override
	public void addAnimation(String animName, Animation animation) {
		//��������������� ������������ ������ MarteEngine, 
		//����� �������� ��-������� �� �������������, ��� �����
		animation.setLooping(false);
		super.addAnimation(animName, animation);
	}
}
