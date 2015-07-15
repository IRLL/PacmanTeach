package pacman.teaching;

import pacman.Experiments;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Gives a fixed amount of front-loaded advice.
 */
public class AlternativeAdvise extends TeachingStrategy {

	private int left; // Advice to give
	private int step;
	private int m;

	public AlternativeAdvise(int m) {
		left = Experiments.BUDGET;
		this.m=m;
		step=0;
	}

	/** When there's some left. */
	public boolean giveAdvice(BasicRLPacMan teacher, MOVE _choice, MOVE _advice, Game game) {
		
		if (game.getTotalTime() % m == 0) {
			left--;
			return true;
		} else {
	
			return false;
		}
		
	}

	/** Until none left. */
	public boolean inUse() {
		//System.out.println("Left= " + left);
		return (left > 0);
	}
}
