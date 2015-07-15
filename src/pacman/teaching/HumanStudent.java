package pacman.teaching;

import java.awt.event.KeyEvent;

import pacman.controllers.KeyBoardInput;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.entries.pacman.FeatureSet;
import pacman.entries.pacman.RLPacMan;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class HumanStudent extends BasicRLPacMan {
	private KeyBoardInput input;
	//private BasicRLPacMan teacher; // Gives advice
	//private TeachingStrategy strategy; // Determines when advice is given

	public HumanStudent() {
		// TODO Auto-generated constructor stub
	}
	
	public HumanStudent(KeyBoardInput input) {
		this.input=input;
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		switch(input.getKey())
    	{
	    	case KeyEvent.VK_UP: 	return MOVE.UP;
	    	case KeyEvent.VK_RIGHT: return MOVE.RIGHT;
	    	case KeyEvent.VK_DOWN: 	return MOVE.DOWN;
	    	case KeyEvent.VK_LEFT: 	return MOVE.LEFT;
	    	case KeyEvent.VK_P:		return MOVE.PAUSE;
	    	case KeyEvent.VK_R:		return MOVE.RESUME;
	    	default: 				return MOVE.NEUTRAL;
    	}
	}
	
	@Override
    public KeyBoardInput getKeyboardInput()
    {
    	return input;
    }

	@Override
	public void setMove(MOVE move) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MOVE[] getMoves() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getQValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeatureSet getFeatures(MOVE move) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadPolicy(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startEpisode(Game game, boolean testMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processStep(Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void savePolicy(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MOVE getMove(Game game, long timeDue, boolean isPause, boolean mandatory) {
		// TODO Auto-generated method stub
		return MOVE.NEUTRAL;
	}

}
