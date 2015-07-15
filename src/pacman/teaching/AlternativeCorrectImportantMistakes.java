package pacman.teaching;

import pacman.Experiments;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.utils.Stats;

/**
 * Gives a fixed amount of advice in important states where the student makes a mistake.
 */
public class AlternativeCorrectImportantMistakes extends TeachingStrategy {
	
	private int left; // Advice to give
	private int threshold; // Of mistake importance
	private int step;// step counter
	private int m; // no advice for the following m steps after advising
		
	public AlternativeCorrectImportantMistakes(int t, int m) {
		left = Experiments.BUDGET;
		threshold = t;
		step=0;
		this.m = m;
	}

	/** When the state has widely varying Q-values, and the student doesn't take the advice action. */
	public boolean giveAdvice(BasicRLPacMan teacher, MOVE choice, MOVE advice, Game game) {
		
		double[] qvalues = teacher.getQValues();
		double gap = Stats.max(qvalues) - Stats.min(qvalues);
		boolean important = (gap > threshold);

		if (important) {
		
			boolean mistake = (choice != advice);

			if (mistake && step>=m) {
				left--;
				step=0;
				return true;
			}
		}
		
		step++;
		
		return false;
	}
	
	/** Until none left. */
	public boolean inUse() {
		return (left > 0);
	}
}
