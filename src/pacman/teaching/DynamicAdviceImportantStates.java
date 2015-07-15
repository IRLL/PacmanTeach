package pacman.teaching;

import pacman.Experiments;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.utils.DataFile;
import pacman.utils.Stats;

public class DynamicAdviceImportantStates extends TeachingStrategy {

	private int left; // Advice to give
	private int threshold; // Of action importance
	private double gamma;// discount factor
	private double powergamma;// mth power of gamm
	private double factor;// factor which is used to enlarge the threshold
	private int step;
	

	public DynamicAdviceImportantStates(int t, int factor, double gamma) {

		left = Experiments.BUDGET;
		threshold = t;
		this.factor = factor;
		this.gamma = gamma;
		powergamma = gamma;
		step=0;
		// System.out.println(" threshold= "+threshold);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean giveAdvice(BasicRLPacMan teacher, MOVE choice, MOVE advice, Game game) {
		//DataFile file=new DataFile("threshold.txt");
		step++;
		// TODO Auto-generated method stub
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
			left--;
			powergamma = gamma;
			//file.append(step+" "+gap+" "+realthreshold+" "+threshold+"\n");
			//file.close();
			return true;
		}
		
		//file.append(step+" "+gap+" "+realthreshold+" "+threshold+"\n");
		//file.close();
		return false;
	}

	@Override
	public boolean inUse() {
		// TODO Auto-generated method stub
		return (left > 0);
	}

}
