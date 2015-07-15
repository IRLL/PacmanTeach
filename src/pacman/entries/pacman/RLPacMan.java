package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.controllers.KeyBoardInput;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Superclass for all learners (basic and student).
 */
public abstract class RLPacMan extends Controller<MOVE> {	

	public abstract void startEpisode(Game game, boolean testMode);
	public abstract void processStep(Game game);
	public abstract void savePolicy(String filename);
	public abstract MOVE getMove(Game game, long timeDue, boolean isPause, boolean mandatory);

	public double[] episodeData() { // Override to add data to learning curves
		double[] data = new double[0];
		return data;
	}
	public KeyBoardInput getKeyboardInput()
    {
    	return null;
    }
}
