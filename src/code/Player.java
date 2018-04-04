package code;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import it.marteEngine.entity.Entity;

public class Player extends Entity{
	//Дольше, но использовать константы - добро
	//Константы анимаций
	public static final String ANIM_HIT = "anim_hit";
	public static final String ANIM_LEFT = "anim_left";
	public static final String ANIM_RIGHT = "anim_right";
	public static final String ANIM_UP = "anim_up";
	public static final String ANIM_DOWN = "anim_down";
	public static final String ANIM_CALM = "anim_calm";
	public static final String ANIM_DEATH = "anim_death";
	public static final String ANIM_WOUNDED = "anim_wouded";
	
	//Константы передвижения для ввода
	private final String LEFT = "left";
	private final String RIGHT = "right";
	private final String UP = "up";
	private final String DOWN = "down";
	private final String ATTACK = "attack";
	
	//переключатель обновления кулдауна
	private boolean isCooldown = false;
	
	//Базовые статы
	//здоровье 
	public float health = 100;	
	//коэффициент защиты, ослабляет итоговый урон врага
	public float defence = 0.1f;
	//коэффициет атаки, усиливает ваш урон
	public float atack = 0.1f;	
	//урон, теоретически должен зависеть от оружия
	public float damage = 10;
	
	//переменная счетчика кулдауна
	private int cooldown = 0;
	
	public static String ENEMY = "enemy";
	
	//персонаж жив (или нет)
	public boolean isAlive = true;

	public Player(float x, float y) throws SlickException {
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
		//создаем быстрые клавиши для управления
		define("RIGHT", Input.KEY_D);
		define("LEFT", Input.KEY_A);
		define("UP", Input.KEY_W);
		define("DOWN", Input.KEY_S);
		define("ATTACK", Input.MOUSE_LEFT_BUTTON);
		//Задаём тип сущности, чтобы различать их при обнаружении столкновений
		addType(PLAYER);
		//Задаём область для обнаружения столновений
		setHitBox(0, 0, width, height);
		}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
		//пока персонаж жив, его логика обновляется
		if(isAlive) {
		//Перемещение
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
		//Если персонаж атакует..
		if(check(ATTACK)) {
			//..и кулдаун ему это позволяет..
			if (cooldown <= 0) {
				//..мы ищем врага в области досягаемости..
				Enemy enemy = checkEnemy();
				//..и если таковой имеется, всыпаем ему
				if (enemy != null) {
					hit(enemy);
				}
				//удар нанесён, устанавливаем кулдаун
				isCooldown = true;
				cooldown = 50;
			}
		}
		//это условие управляет обновлением кулдауна
		if(isCooldown) {
			if (cooldown > 0) cooldown--;
			else isCooldown = false;
		}}
	}
	
	/**поиск противников вокруг игрока*/
	//TODO: переписать в checkTarget, для взаимодействия с миром
	private Enemy checkEnemy() {
		Entity enemy = null;
		if(collide(ENEMY, x+2, y)!=null) enemy = collide(ENEMY, x+2, y);
		if(collide(ENEMY, x-2, y)!=null) enemy = collide(ENEMY, x-2, y);
		//мы не ниндзя, чтобы бить назад
		//if(collide(ENEMY, x, y+2)!=null) enemy = collide(ENEMY, x, y+2);
		if(collide(ENEMY, x, y-2)!=null) enemy = collide(ENEMY, x, y-2);			
		if(enemy!=null)return (Enemy) enemy;
		else return null;
	} 
	
	private void hit(Enemy enemy) {
		setAnim(ANIM_HIT);
		//вычислим урон с учётом модификатора атаки
		float real_damage = this.damage + (this.damage * this.atack);
		//вычислим урон ослабленный модификатором защиты противника
		float reduced_damage = real_damage - (real_damage * enemy.defence); 
		//вычтем из жизни противника
		enemy.health -= reduced_damage;
		if(enemy.health>0) enemy.setAnim(ANIM_WOUNDED);
		else {
			setAnim(ANIM_DEATH);
			isAlive = false;
		}
	}
	
	@Override
	public void addAnimation(String animName, Animation animation) {
		//переопределение стандартного метода MarteEngine, 
		//чтобы анимации по-умлчнию не зацикливались, это важно
		animation.setLooping(false);
		super.addAnimation(animName, animation);
	}
}
