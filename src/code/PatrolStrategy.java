package code;

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
		if(x==maxDist) target.move(minDist, target.y);
		if(y==minDist) target.move(maxDist, target.y);
	}
}
