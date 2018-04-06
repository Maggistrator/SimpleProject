package logic;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import ai.strategies.PatrolStrategy;
import core.Strategy;
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
	public boolean isCooldown = false;
	
	//Базовые статы
	//здоровье 
	public float health = 60;	
	//коэффициент защиты, ослабляет итоговый урон врага
	public float defence = 0.1f;
	//коэффициет атаки, усиливает ваш урон
	public float atack = 0.2f;	
	//урон, теоретически должен зависеть от оружия
	public float damage = 4;
	
	//переменная счетчика кулдауна
	public int cooldown = 0;
	
	public static String ENEMY = "enemy";
	
	public final String STRATEGY_PATROL = "patrol";
	private Strategy strategy = null;
	
	private float newx = x;
	private float newy = y;
	
	public Player target;
	//графическое представление хитбокса
	Rectangle rect;
	
	//персонаж жив (или нет)
	public boolean isAlive = true;

	public Enemy(float x, float y) throws SlickException {
		super(x, y);
		//Заявляем размеры сущности
		width = 48;
		height = 58;
		//Объявляем спрайтлисты
		SpriteSheet sheet_hit = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_left = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_right = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_up = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_down = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_wounded = new SpriteSheet("textures/char.png",width,height);
		SpriteSheet sheet_death = new SpriteSheet("textures/char.png",width,height);
		setGraphic(new Image("textures/char.png"));
		//Создаем анимацию через массив изображений
		Image[] arr_calm = {
				new Image("textures/char.png"), 
				new Image("textures/char.png"),
				new Image("textures/char.png"),
				new Image("textures/char.png")};
		//Регистрация анимаций
		addAnimation(ANIM_CALM, new Animation(arr_calm, 40));
		addAnimation(ANIM_HIT, new Animation(sheet_hit, 40));
		addAnimation(ANIM_LEFT, new Animation(sheet_left, 40));
		addAnimation(ANIM_RIGHT, new Animation(sheet_right, 40));
		addAnimation(ANIM_UP, new Animation(sheet_up, 40));
		addAnimation(ANIM_DOWN, new Animation(sheet_down, 40));
		addAnimation(ANIM_WOUNDED, new Animation(sheet_wounded, 40));
		addAnimation(ANIM_DEATH, new Animation(sheet_death, 40));
		setAnim(ANIM_CALM);
		//в данном случае, управление автоматическое, клавиш не задаётся
		//Задаём тип сущности, чтобы различать их при обнаружении столкновений
		addType(ENEMY);
		setStrategy(new PatrolStrategy(this));
		//Задаём область для обнаружения столновений
		setHitBox(0, 0, width, height);
		//клон хитбокса
		rect = new Rectangle(0, 0, width, height);
		}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
		if(isAlive) {
		//если есть путевая точка - следуем к ней

		if(checkPlayer()==null) {
		if(newx<x) move(LEFT); else if(newx>x) move(RIGHT);
		if(newy<y) move(UP); else if(newy>y) move(DOWN);
		}
		
		//вызов стратегии бота
		strategy.act();
		
		//это условие управляет обновлением кулдауна
		if(isCooldown) {
			if (cooldown > 0) cooldown--;
			else isCooldown = false;
		}}
		rect.setX(x);
		rect.setY(y);
	}
	
	public Player checkPlayer() {
		Entity player = null;
		if(collide(PLAYER, x+2, y)!=null) player = collide(PLAYER, x+2, y);
		if(collide(PLAYER, x-2, y)!=null) player = collide(PLAYER, x-2, y);
		//мы не ниндзя, чтобы бить назад
		//if(collide(PLAYER, x, y+2)!=null) player = collide(PLAYER, x, y+2);
		if(collide(PLAYER, x, y-2)!=null) player = collide(PLAYER, x, y-2);			
		if(player!=null)return (Player) player;
		else return null;
	} 
	
	public void hit(Player player) {
		setAnim(ANIM_HIT);
		//вычислим урон с учётом модификатора атаки
		float real_damage = this.damage + (this.damage * this.atack);
		//вычислим урон ослабленный модификатором защиты противника
		float reduced_damage = real_damage - (real_damage * player.defence); 
		//вычтем из жизни противника
		player.health -= reduced_damage;
		if(player.health>0) player.setAnim(ANIM_WOUNDED);
		else {
			player.setAnim(ANIM_DEATH);
			player.isAlive = false;
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
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
		g.drawString((int)health+"", x, y+height);
		g.draw(rect);
	}
}
