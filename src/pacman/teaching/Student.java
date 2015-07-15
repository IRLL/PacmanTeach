package pacman.teaching;

import java.awt.event.KeyEvent;
import java.util.Random;

import pacman.controllers.KeyBoardInput;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.entries.pacman.RLPacMan;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
 * Superclass for all student learners.
 */
public class Student extends RLPacMan {

	private KeyBoardInput input;
	private BasicRLPacMan teacher; // Gives advice
	private BasicRLPacMan student; // Takes advice
	private TeachingStrategy strategy; // Determines when advice is given

	private boolean testMode; // When set, will not explore or learn or take
								// advice
	private int adviceCount; // During the last episode

	private MOVE choice;
	

	public Student(BasicRLPacMan teacher, BasicRLPacMan student,
			TeachingStrategy strategy) {
		this.teacher = teacher;
		this.student = student;
		this.strategy = strategy;
		choice = MOVE.NEUTRAL;
	}

	public Student(BasicRLPacMan teacher, BasicRLPacMan student,
			TeachingStrategy strategy, KeyBoardInput input) {
		this.teacher = teacher;
		this.student = student;
		this.strategy = strategy;
		this.input = input;
	}

	/** Prepare for the first move. */
	public void startEpisode(Game game, boolean testMode) {
		this.testMode = testMode;
		adviceCount = 0;
		student.startEpisode(game, testMode);

		if (!testMode && strategy.inUse()) {
			strategy.startEpisode();
			teacher.startEpisode(game, true);
		}
	}

	@Override
	public KeyBoardInput getKeyboardInput() {
		return input;
	}

	/** get teacher **/
	public BasicRLPacMan getTeacher() {

		return teacher;
	}

	/** Choose a move, possibly with advice. */
	public MOVE getMove(Game game, long timeDue, boolean isPause,
			boolean mandatory) {

		switch (input.getKey()) {
		case KeyEvent.VK_UP:
			if (isPause == false) {
				if ((choice = giveAdvice(game, timeDue, MOVE.UP)) == MOVE.NOAD) {
					return MOVE.UP;
				} else {

					return choice;
				}
			} else if (isPause == true && mandatory == true) {

				return MOVE.NEUTRAL;

			} else {

				return choice = MOVE.UP;
			}

		case KeyEvent.VK_RIGHT:
			if (isPause == false) {
				if ((choice = giveAdvice(game, timeDue, MOVE.UP)) == MOVE.NOAD) {
					return MOVE.RIGHT;
				} else {

					return choice;
				}
			} else if (isPause == true && mandatory == true) {

				return MOVE.NEUTRAL;

			} else {

				return choice = MOVE.RIGHT;
			}

		case KeyEvent.VK_DOWN:
			if (isPause == false) {
				if ((choice = giveAdvice(game, timeDue, MOVE.UP)) == MOVE.NOAD) {
					return MOVE.DOWN;
				} else if (isPause == true && mandatory == true) {

					return MOVE.NEUTRAL;

				} else {

					return choice;
				}
			} else {
				return choice = MOVE.DOWN;
			}

		case KeyEvent.VK_LEFT:
			if (isPause == false) {
				if ((choice = giveAdvice(game, timeDue, MOVE.UP)) == MOVE.NOAD) {
					return MOVE.LEFT;
				} else if (isPause == true && mandatory == true) {

					return MOVE.NEUTRAL;

				} else {

					return choice;
				}
			} else {

				return choice = MOVE.LEFT;
			}

		case KeyEvent.VK_P:
			if (isPause == false) {

				return MOVE.PAUSE;

			} else if (isPause == true && mandatory == true) {

				return MOVE.NEUTRAL;

			} else {
				return choice;
			}

		case KeyEvent.VK_R:
			if (isPause == true) {
				return MOVE.RESUME;
			} else if (isPause == false && mandatory == true) {

				return MOVE.NEUTRAL;

			} else {
				return choice;
			}
		default:
			return MOVE.NEUTRAL;
		}
		/*
		 * MOVE choice = student.getMove(game, timeDue);
		 * 
		 * if (!testMode && strategy.inUse()) { MOVE advice =
		 * teacher.getMove(game, timeDue);
		 * 
		 * if (strategy.giveAdvice(teacher, choice, advice)) {
		 * student.setMove(advice); adviceCount++; return advice; } }
		 * 
		 * return choice;
		 */
	}
	public MOVE giveRandomAdvice(){
		Random random=new Random();
		int index=random.nextInt(4);
		MOVE move=MOVE.values()[index];
		//System.out.println(move.name());
		return move;
			
	}
	public MOVE giveAdvice(Game game, long timeDue, MOVE choice) {
		if (!testMode && strategy.inUse()) {
			//MOVE advice = teacher.getMove(game, timeDue);
			
			MOVE advice=giveRandomAdvice();

			if (strategy.giveAdvice(teacher, choice, advice, game)) {
				if (advice == MOVE.DOWN) {

					advice = MOVE.ADDOWN;

				} else if (advice == MOVE.LEFT) {

					advice = MOVE.ADLEFT;

				} else if (advice == MOVE.RIGHT) {

					advice = MOVE.ADRIGHT;

				} else if (advice == MOVE.UP) {

					advice = MOVE.ADUP;
				}
				// student.setMove(advice);
				adviceCount++;
				return advice;
			}
		}

		return MOVE.NOAD;
	}

	/** return testMode **/
	public boolean getTestMode() {

		return testMode;
	}

	public TeachingStrategy getStrategy() {

		return strategy;
	}

	/** Prepare for the next move. */
	public void processStep(Game game) {
		student.processStep(game);

		if (!testMode && strategy.inUse())
			teacher.processStep(game);
	}

	/** Save the current policy to a file. */
	public void savePolicy(String filename) {
		student.savePolicy(filename);
	}

	/**
	 * Report amount of advice given in the last episode, along with any other
	 * data the strategy wants to record.
	 */
	public double[] episodeData() {

		double[] extraData = strategy.episodeData();

		double[] data = new double[extraData.length + 1];
		data[0] = adviceCount;

		for (int d = 0; d < extraData.length; d++)
			data[d + 1] = extraData[d];

		return data;
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		return null;
	}
}
