package pacman.teaching;

import pacman.Experiments;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.utils.Stats;

public class DynamicCorrectImportantMistakes extends TeachingStrategy {

	private int left; // Advice to give
	private int threshold; // Of mistake importance
	private double gamma;// discount factor
	private double powergamma;// mth power of gamm
	private double factor;// factor which is used to enlarge the threshold

	public DynamicCorrectImportantMistakes(int t, int factor, double gamma) {

		left = Experiments.BUDGET;
		threshold = t;
		this.factor = factor;
		this.gamma = gamma;
		powergamma = gamma;
		// TODO Auto-generated constructor stub
	}

	/**
	 * When the state has widely varying Q-values, and the student doesn't take
	 * the advice action.
	 */
	public boolean giveAdvice(BasicRLPacMan teacher, MOVE choice, MOVE advice, Game game) {

		double[] qvalues = teacher.getQValues();
		double gap = Stats.max(qvalues) - Stats.min(qvalues);

		// calculating the gamma*threshold
		int realthreshold = (int) (powergamma * factor * threshold);

		if (realthreshold < threshold) {
			realthreshold = threshold;
		}

		//System.out.println(" threshold= " + realthreshold);

		powergamma = gamma * powergamma;
		
		
		
		boolean important = (gap > realthreshold);

		if (important) {

			boolean mistake = (choice != advice);

			if (mistake) {
				left--;
				powergamma=gamma;
				return true;
			}
		}

		return false;
	}

	/** Until none left. */
	public boolean inUse() {
		return (left > 0);
	}

}
