package code;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Launcher extends StateBasedGame {

	public Launcher(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new Prologue(0));
		enterState(0);
	}


	public static void main(String[] args) throws SlickException {
		Launcher launcher = new Launcher("The Last Warrior");
		AppGameContainer container = new AppGameContainer(launcher);
		container.setDisplayMode(600, 400, false);
		container.setTargetFrameRate(60);
		container.start();
	}

}
