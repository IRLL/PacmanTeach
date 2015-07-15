package pacman.teaching;

import pacman.Experiments;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.utils.Stats;

/**
 * Gives a fixed amount of advice in important states.
 */
public class AlternativeAdviseImportantStates extends TeachingStrategy {

	private int left; // Advice to give
	private int threshold; // Of action importance
	private int step;// step counter
	private int m; // no advice for the following m steps after advising

	public AlternativeAdviseImportantStates(int t, int m) {
		left = Experiments.BUDGET;
		threshold = t;
		step = 0;
		this.m = m;
	}

	/** When the state has widely varying Q-values. */
	public boolean giveAdvice(BasicRLPacMan teacher, MOVE _choice, MOVE _advice, Game game) {

		double[] qvalues = teacher.getQValues();
		double gap = Stats.max(qvalues) - Stats.min(qvalues);
		boolean important = (gap > threshold);

		if (important && step >= m) {
			left--;
			step = 0;
			return true;
		}

		step++;

		return false;
	}

	/** Until none left. */
	public boolean inUse() {
		return (left > 0);
	}
}
