package scienes;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import it.marteEngine.World;
import logic.Enemy;
import logic.Player;

public class Prologue extends World {

	Player player;
	Enemy enemy;
	public Prologue(int id) {
		super(id);
		}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		super.init(container, game);
		player = new Player(50,50);
		enemy = new Enemy(250, 50);
		add(player);
		add(enemy);
	}

}
