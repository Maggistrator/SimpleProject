package code;

import it.marteEngine.entity.Entity;

public class Enemy extends Entity {

	String ANIM_HIT = "anim_hit";
	String ANIM_LEFT = "anim_left";
	String ANIM_RIGHT = "anim_right";
	String ANIM_UP = "anim_up";
	String ANIM_DOWN = "anim_down";
	String ANIM_CALM = "anim_calm";
	String ANIM_DEATH = "anim_death";
	String ANIM_WOUNDED = "anim_wouded";
	
	public float health = 100;
	public float defence = 0.1f;
	public float atack = 0.1f;
	public float damage = 10;
	
	boolean isAlive = true;
	
	public Enemy(float x, float y) {
		super(x, y);
		
	}
	
}
