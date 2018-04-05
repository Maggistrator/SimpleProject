package code;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import it.marteEngine.entity.Entity;

/**
 * по-сути, копия player, но с автоуправлением
 * этого можно добиться грамотным наследованием, 
 * но в целях наглядности использовано дублирование
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
	
	//Константы направления для автоматики
	private final String LEFT = "left";
	private final String RIGHT = "right";
	private final String UP = "up";
	private final String DOWN = "down";
	
	//переключатель обновления кулдауна
	private boolean isCooldown = false;
	
	//Базовые статы
	//здоровье 
	public float health = 60;	
	//коэффициент защиты, ослабляет итоговый урон врага
	public float defence = 0.1f;
	//коэффициет атаки, усиливает ваш урон
	public float atack = 0.2f;	
	//урон, теоретически должен зависеть от оружия
	public float damage = 10;
	
	//переменная счетчика кулдауна
	private int cooldown = 0;
	
	public static String ENEMY = "enemy";
	
	public final String STRATEGY_PATROL = "patrol";
	private Strategy strategy = null;
	
	private float newx = x;
	private float newy = y;
	
	Player target;
	
	//персонаж жив (или нет)
	public boolean isAlive = true;

	public Enemy(float x, float y) throws SlickException {
		super(x, y);
		//Заявляем размеры сущности
		width = 20;
		height = 40;
		//Объявляем спрайтлисты
		SpriteSheet sheet_hit = new SpriteSheet("textures/hit.png",width,height);
		SpriteSheet sheet_left = new SpriteSheet("textures/left.png",width,height);
		SpriteSheet sheet_right = new SpriteSheet("textures/right.png",width,height);
		SpriteSheet sheet_up = new SpriteSheet("textures/up.png",width,height);
		SpriteSheet sheet_down = new SpriteSheet("textures/down.png",width,height);
		SpriteSheet sheet_wounded = new SpriteSheet("textures/wounded.png",width,height);
		setGraphic(new Image("D:/Data/player.png"));
		//Создаем анимацию через массив изображений
		Image[] arr_calm = {
				new Image("textures/calm.png"), 
				new Image("textures/scratching_ass1.png"),
				new Image("textures/scratching_ass2.png"),
				new Image("textures/scratching_ass3.png")};
		//Регистрация анимаций
		addAnimation(ANIM_CALM, new Animation(arr_calm, 40));
		addAnimation(ANIM_HIT, new Animation(sheet_hit, 40));
		addAnimation(ANIM_LEFT, new Animation(sheet_left, 40));
		addAnimation(ANIM_RIGHT, new Animation(sheet_right, 40));
		addAnimation(ANIM_UP, new Animation(sheet_up, 40));
		addAnimation(ANIM_DOWN, new Animation(sheet_down, 40));
		addAnimation(ANIM_WOUNDED, new Animation(sheet_wounded, 40));
		setAnim(ANIM_CALM);
		//в данном случае, управление автоматическое, клавиш не задаётся
		//Задаём тип сущности, чтобы различать их при обнаружении столкновений
		addType(ENEMY);
		setStrategy(new PatrolStrategy(this));
		//Задаём область для обнаружения столновений
		setHitBox(0, 0, width, height);
		}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
		if(isAlive) {
		//если есть путевая точка - следуем к ней

		if(checkPlayer()==null) {
		if(newx<x) move(LEFT); else move(RIGHT);
		if(newy<y) move(UP); else move(DOWN);
		}
		
		strategy.act();
		
		//если протагонист рядом - атакуем его
		if(cooldown<=0) {
			target = checkPlayer();
			if(target!=null) hit(target);
			cooldown = 50;
			isCooldown = true;
		}
		//вызов стратегии бота
		
		//это условие управляет обновлением кулдауна
		if(isCooldown) {
			if (cooldown > 0) cooldown--;
			else isCooldown = false;
		}}
	}
	
	protected Player checkPlayer() {
		Entity player = null;
		if(collide(PLAYER, x+2, y)!=null) player = collide(PLAYER, x+2, y);
		if(collide(PLAYER, x-2, y)!=null) player = collide(PLAYER, x-2, y);
		//мы не ниндзя, чтобы бить назад
		//if(collide(PLAYER, x, y+2)!=null) player = collide(PLAYER, x, y+2);
		if(collide(PLAYER, x, y-2)!=null) player = collide(PLAYER, x, y-2);			
		if(player!=null)return (Player) player;
		else return null;
	} 
	
	private void hit(Player player) {
		setAnim(ANIM_HIT);
		//вычислим урон с учётом модификатора атаки
		float real_damage = this.damage + (this.damage * this.atack);
		//вычислим урон ослабленный модификатором защиты противника
		float reduced_damage = real_damage - (real_damage * player.defence); 
		//вычтем из жизни противника
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
		//переопределение стандартного метода MarteEngine, 
		//чтобы анимации по-умлчнию не зацикливались, это важно
		animation.setLooping(false);
		super.addAnimation(animName, animation);
	}
}
