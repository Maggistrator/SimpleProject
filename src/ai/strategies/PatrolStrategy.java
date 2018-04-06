package ai.strategies;

import core.Strategy;
import logic.Enemy;

public class PatrolStrategy implements Strategy{

	float minDist = 0;
	float maxDist = 0;
	float x = 0;
	float y = 0;
	Enemy target = null;
	
	public PatrolStrategy(Enemy target) {
		x = target.x;
		y = target.y;
		this.target = target;
		maxDist = x + 50;
		minDist = x - 50;
		target.move(maxDist, target.y);
	}
	
	@Override
	public void act() {
		
		x = target.x;
		y = target.y;
		
		if(x==maxDist) target.move(minDist, target.y);
		if(x==minDist) target.move(maxDist, target.y);
		
		//если протагонист рядом - атакуем его
		if(target.cooldown<=0) {
			target.target = target.checkPlayer();
			if(target.target!=null) target.hit(target.target);
			target.cooldown = 50;
			target.isCooldown = true;
		}
	}
}
