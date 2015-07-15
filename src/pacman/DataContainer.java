package pacman;

public class DataContainer {
	
	private int step;
	private double score;
	private int delay;
	
	public DataContainer(int step) {
		this.step=step;
		score=0.0;
		delay=40; 
		// TODO Auto-generated constructor stub
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

}
