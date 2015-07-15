package pacman;

import static pacman.game.Constants.DELAY;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

import pacman.controllers.Controller;
import pacman.controllers.HumanController;
import pacman.controllers.KeyBoardInput;
import pacman.entries.ghosts.StandardGhosts;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.entries.pacman.CustomFeatureSet;
import pacman.entries.pacman.DepthFeatureSet;
import pacman.entries.pacman.FeatureSet;
import pacman.entries.pacman.QPacMan;
import pacman.entries.pacman.RLPacMan;
import pacman.entries.pacman.SarsaPacMan;
import pacman.game.AdviceView;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.MOVE;
import pacman.teaching.DynamicAdviceImportantStates;
import pacman.teaching.AlternativeAdvise;
import pacman.teaching.AlternativeAdviseImportantStates;
import pacman.teaching.DynamicCorrectImportantMistakes;
import pacman.teaching.AlternativeCorrectImportantMistakes;
import pacman.teaching.PredictImportantMistakes;
import pacman.teaching.Student;
import pacman.teaching.HumanStudent;
import pacman.teaching.TeachingStrategy;
import pacman.utils.DataFile;
import pacman.utils.LearningCurve;
import pacman.utils.Stats;

/*1) The first but optional step is training. The train function will train a student (who may be used as a teacher 
 * in the future) and save its policy in the corresponding folders

 2) The second is to create an RLpacMAn object by loading a saved policy (from step 1 or from previous experiments), 
 you do this by passing a string parameter to the create function which actually corresponds to the folder name, 
 so the create function adds a  slash "policy" to go to the filename of the policy file 
 (e.g. you took one existing policy file and renamed it to plain "policy")

 3) (optional ) you can watch the agent on the game board by passing an already created RLpacman object to it or the 
 create function itself with its string parameters (which returns RLpacman objects)
 */

public class Experiments {

	public static String TEACHER = "customS"; // Teacher feature set and
												// algorithm
	public static String STUDENT = "customS"; // Student feature set and
												// algorithm
	public static String DIR = "myData/" + TEACHER + "/" + STUDENT; // Where to
																	// store
																	// data

	public static int BUDGET = 1000; // Advice budget
	public static int REPEATS = 30; // Curves to average
	public static int LENGTH = 100; // Points per curve
	public static int TEST = 30; // Test episodes per point
	public static int TRAIN = 10; // Train episodes per point
	public static int STEP = 1000;// steps for players

	public static Random rng = new Random();
	public static StandardGhosts ghosts = new StandardGhosts();

	private static MOVE lastMove = MOVE.NEUTRAL;

	// constructor

	public Experiments() {

		lastMove = MOVE.NEUTRAL;
	}

	/**
	 * Run experiments.
	 */
	public static void main(String[] args) {

		// watch(create("humanbaseline"));
		// train("humancorrectd50", 0);
		// episode(create("humanadvised50"), false);
		//humantraincontrol("humanadvised50");
		//humantrain("humanadvised50");
		humantrain("humanrandom");
		//humantrain("humancorrect100");

	}

	/** Set up a learner. */
	public static RLPacMan create(String learner) {

		FeatureSet teacherProto = TEACHER.startsWith("custom") ? new CustomFeatureSet()
				: new DepthFeatureSet();
		FeatureSet studentProto = STUDENT.startsWith("custom") ? new CustomFeatureSet()
				: new DepthFeatureSet();

		// Lone teacher
		if (learner.startsWith("teacher")) {
			BasicRLPacMan teacher = TEACHER.endsWith("S") ? new SarsaPacMan(
					teacherProto) : new QPacMan(teacherProto);
			teacher.loadPolicy("myData/" + TEACHER + "/teacher/policy");
			return teacher;
		}

		// Lone student
		else if (learner.startsWith("independent")) {
			return STUDENT.endsWith("S") ? new SarsaPacMan(studentProto)
					: new QPacMan(studentProto);
		}
		// human student and agent teacher
		else if (learner.startsWith("human")) {

			BasicRLPacMan student = new HumanStudent();
			BasicRLPacMan teacher = TEACHER.endsWith("S") ? new SarsaPacMan(
					teacherProto) : new QPacMan(teacherProto);
			teacher.loadPolicy("myData/" + TEACHER + "/teacher/policy");

			// Front-load the advice budget
			if (learner.startsWith("humanrandom")) {
				TeachingStrategy strategy = new AlternativeAdvise(30);
				return new Student(teacher, student, strategy,
						new KeyBoardInput());
			}

			// Improved important states
			if (learner.startsWith("humanadvised")) {
				int threshold = Integer.parseInt(learner.substring(12));
				// int threshold = 10;
				TeachingStrategy strategy = new DynamicAdviceImportantStates(
						threshold, 100, 0.9);
				return new Student(teacher, student, strategy,
						new KeyBoardInput());
			}

			// Advise in important states
			if (learner.startsWith("humanadvise")) {
				int threshold = Integer.parseInt(learner.substring(11));
				// int threshold = 10;
				TeachingStrategy strategy = new AlternativeAdviseImportantStates(
						threshold, 20);
				return new Student(teacher, student, strategy,
						new KeyBoardInput());
			}

			// Improved important mistakes
			if (learner.startsWith("humancorrectd")) {
				int threshold = Integer.parseInt(learner.substring(13));
				// int threshold = 6;
				TeachingStrategy strategy = new DynamicCorrectImportantMistakes(
						threshold, 100, 0.9);
				return new Student(teacher, student, strategy,
						new KeyBoardInput());
			}

			// Correct important mistakes
			if (learner.startsWith("humancorrect")) {
				int threshold = Integer.parseInt(learner.substring(12));
				// int threshold = 6;
				TeachingStrategy strategy = new AlternativeCorrectImportantMistakes(
						threshold, 20);
				return new Student(teacher, student, strategy,
						new KeyBoardInput());
			}

			// Advise in important states with predicted mistakes
			if (learner.startsWith("humanpredict")) {
				int threshold = Integer.parseInt(learner.substring(12));
				TeachingStrategy strategy = new PredictImportantMistakes(
						threshold);
				return new Student(teacher, student, strategy);
			}

		}
		// Student-teacher pair
		else {
			BasicRLPacMan student = STUDENT.endsWith("S") ? new SarsaPacMan(
					studentProto) : new QPacMan(studentProto);
			BasicRLPacMan teacher = TEACHER.endsWith("S") ? new SarsaPacMan(
					teacherProto) : new QPacMan(teacherProto);
			teacher.loadPolicy("myData/" + TEACHER + "/teacher/policy");

			// Front-load the advice budget
			if (learner.startsWith("baseline")) {
				TeachingStrategy strategy = new AlternativeAdvise(1);
				return new Student(teacher, student, strategy);
			}

			// Advise in important states
			if (learner.startsWith("advise")) {
				int threshold = Integer.parseInt(learner.substring(6));
				TeachingStrategy strategy = new AlternativeAdviseImportantStates(
						threshold, 20);
				return new Student(teacher, student, strategy);
			}

			// Correct important mistakes
			if (learner.startsWith("correct")) {
				int threshold = Integer.parseInt(learner.substring(7));
				TeachingStrategy strategy = new AlternativeCorrectImportantMistakes(
						threshold, 20);
				return new Student(teacher, student, strategy);
			}

			// Advise in important states with predicted mistakes
			if (learner.startsWith("predict")) {
				int threshold = Integer.parseInt(learner.substring(7));
				TeachingStrategy strategy = new PredictImportantMistakes(
						threshold);
				return new Student(teacher, student, strategy);
			}
		}

		return null;
	}

	public static void rewardMessage(double score) {

		double reward = 0;

		if (score > 1000 && score < 4000) {

			reward = 0.5;
		} else if (score >= 4000) {

			reward = 1.0;
		}

		JOptionPane.showMessageDialog(null,
				"Congratulations!\nThe score of last round is: " + score
						+ "\nThe reward is: " + reward + "$");

	}

	/** For human training control **/
	public static void humantraincontrol(String learner) {
		RLPacMan pacman = create(learner);
		DataContainer data = new DataContainer(STEP);
		DataFile datafile = new DataFile("Control" + "data.txt");
		double[] finaldata = new double[3];
		data.setDelay(50);
		for (int i = 0; i < 3; i++) {// three group
			//System.out.println(i);
			while (data.getStep() > 0 && i < 2) {
				// System.out.println(i + 1);
				episode(pacman, true, data);
				// System.out.println(data.getStep() + " " + data.getScore()
				// + "\n");

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
			if (i < 2) {
				JOptionPane.showConfirmDialog(null, "Round " + (i + 1)
						+ " is completed\nRound " + (i + 2) + " will begin!",
						"Message", JOptionPane.YES_OPTION);
			}

			if (i == 2) {
				// System.out.println(i + 1);
				data.setStep(5000);
				episode(pacman, true, data);
				// System.out.println(data.getStep() + " " + data.getScore()
				// + "\n");
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}

			}

			finaldata[i] = data.getScore();
			data.setStep(STEP);
			data.setScore(0.0);
		}
		// System.out.println(finaldata[0]+" "+finaldata[1]+" "+finaldata[2]+"\n");
		datafile.append(finaldata[0] + " " + finaldata[1] + " " + finaldata[2]
				+ "\n");// store the data
		datafile.close();
		DataFile finaldatafile = new DataFile("adivcerecords.txt");
		finaldatafile.append("Fin\n");
		finaldatafile.close();
		//rewardMessage(finaldata[2]);
		JOptionPane.showMessageDialog(null,
				"Congratulations!\n"+"The game is completed!");
	}

	/** For human training **/
	public static void humantrain(String learner) {
		RLPacMan pacman = create(learner);
		DataContainer data = new DataContainer(STEP);
		DataFile datafile = new DataFile(learner + "data.txt");
		double[] finaldata = new double[3];

		for (int i = 0; i < 3; i++) {// three group
			while (data.getStep() > 0 && i < 2) {
				if (i != 1) {
					data.setDelay(50);
					episode(pacman, true, data);
					// System.out.println(data.getStep() + " " + data.getScore()
					// + "\n");
				} else {
					data.setDelay(50);
					episode(pacman, false, data);
					// System.out.println(data.getStep() + " " + data.getScore()
					// + "\n");
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
			
			if (i < 2) {
				JOptionPane.showConfirmDialog(null, "Round " + (i + 1)
						+ " is completed\nRound " + (i + 2) + " will begin!",
						"Message", JOptionPane.YES_OPTION);
			}
			
			if (i == 2) {

				data.setStep(5000);
				episode(pacman, true, data);
				// System.out.println(data.getStep() +
				// " "+data.getScore()+"\n");
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}

			}

			finaldata[i] = data.getScore();
			data.setStep(STEP);
			data.setScore(0.0);
		}
		datafile.append(finaldata[0] + " " + finaldata[1] + " " + finaldata[2]
				+ "\n");// store the data
		datafile.close();
		DataFile finaldatafile = new DataFile("adivcerecords.txt");
		finaldatafile.append("Fin\n");
		finaldatafile.close();
		//rewardMessage(finaldata[2]);
		JOptionPane.showMessageDialog(null,
				"Congratulations!\n"+"The game is completed!");
		//rewardMessage(finaldata[2]);
	}

	/** Generate learning curves. */
	public static void train(String learner, int start) {

		// Make sure directory exists
		File file = new File(DIR + "/" + learner);
		if (!file.exists())
			file.mkdir();

		// Load old curves
		LearningCurve[] curves = new LearningCurve[REPEATS];
		for (int i = 0; i < start; i++)
			curves[i] = new LearningCurve(LENGTH + 1, TRAIN, DIR + "/"
					+ learner + "/curve" + i);

		// Begin new curves
		for (int i = start; i < REPEATS; i++) {
			curves[i] = new LearningCurve(LENGTH + 1, TRAIN);

			System.out.println("Training " + DIR + "/" + learner + " " + i
					+ "...");
			RLPacMan pacman = create(learner);

			// First point
			double[] initialData = pacman.episodeData();
			double initialScore = evaluate(pacman, TEST);
			curves[i].set(0, initialScore, initialData);

			// Rest of the points
			for (int x = 1; x <= LENGTH; x++) {
				double[] data = new double[initialData.length];

				for (int y = 0; y < TRAIN; y++) {
					episode(pacman, false, null);

					double[] episodeData = pacman.episodeData();
					for (int d = 0; d < data.length; d++)
						data[d] += episodeData[d];
				}

				double score = evaluate(pacman, TEST);
				curves[i].set(x, score, data);
			}

			// Save new curve and policy
			pacman.savePolicy(DIR + "/" + learner + "/policy" + i);
			curves[i].save(DIR + "/" + learner + "/curve" + i);

			// Average all curves
			LearningCurve avgCurve = new LearningCurve(Arrays.copyOf(curves,
					i + 1));
			avgCurve.save(DIR + "/" + learner + "/avg_curve");
		}

		System.out.println("Done.");
	}

	/*
	 * Determine the value of isPause according to current move and current
	 * pause status
	 */
	public static void determinePause(GameStatus gs) {

		boolean isPause = gs.isPause();
		boolean mandatoryAdvice = gs.isMandatoryAdvice();
		AdviceView av = gs.getAv();
		MOVE move = gs.getMove();

		// System.out.println(move.name());

		if (move == MOVE.PAUSE) {
			av.pauseGame();
			isPause = true;
			gs.setPause(isPause);
		} else if (move == MOVE.RESUME) {
			av.resumeGame();
			isPause = false;
			gs.setPause(isPause);
		}

		if (move == MOVE.ADDOWN) {
			isPause = true;
			av.giveAdvice("Down");
			gs.setPause(isPause);
			av.setCurretMove(MOVE.DOWN);
			if (mandatoryAdvice == true) {
				gs.setAdviceMove(MOVE.DOWN);
				gs.setAdvice(true);
				// System.out.println(gs.isAdvice());
			}

		} else if (move == MOVE.ADLEFT) {
			isPause = true;
			av.giveAdvice("Left");
			gs.setPause(isPause);
			av.setCurretMove(MOVE.LEFT);
			if (mandatoryAdvice == true) {
				gs.setAdviceMove(MOVE.LEFT);
				gs.setAdvice(true);
				// System.out.println(gs.isAdvice());
			}
		} else if (move == MOVE.ADRIGHT) {
			isPause = true;
			av.giveAdvice("Right");
			gs.setPause(isPause);
			av.setCurretMove(MOVE.RIGHT);
			if (mandatoryAdvice == true) {
				gs.setAdviceMove(MOVE.RIGHT);
				gs.setAdvice(true);
				// System.out.println(gs.isAdvice());
			}
		} else if (move == MOVE.ADUP) {
			isPause = true;
			av.giveAdvice("Up");
			gs.setPause(isPause);
			av.setCurretMove(MOVE.UP);
			if (mandatoryAdvice == true) {
				gs.setAdviceMove(MOVE.UP);
				gs.setAdvice(true);
				// System.out.println(gs.isAdvice());
			}
		}

		if (mandatoryAdvice == false && isPause == true) {
			if (move == MOVE.DOWN && lastMove != move) {
				gs.setAdvice(true);
				// gs.setMove(MOVE.DOWN);
				av.setCurretMove(move);
				lastMove = move;

			} else if (move == MOVE.LEFT && lastMove != move) {
				gs.setAdvice(true);
				av.setCurretMove(move);
				// gs.setMove(MOVE.LEFT);

				lastMove = move;

			} else if (move == MOVE.RIGHT && lastMove != move) {
				gs.setAdvice(true);
				av.setCurretMove(move);
				// gs.setMove(MOVE.RIGHT);
				lastMove = move;

			} else if (move == MOVE.UP && lastMove != move) {
				av.setCurretMove(move);
				gs.setAdvice(true);
				// gs.setMove(MOVE.UP);
				lastMove = move;
			}
		}

	}

	/** Train a learner for one more episode. */
	public static void episode(RLPacMan pacman, boolean TestModel,
			DataContainer data) {

		int steps = data.getStep();
		double score = data.getScore();

		boolean mandatoryAdvice = false; // Mandatory advice indicator
		Game game = new Game(rng.nextLong(), 3);
		pacman.startEpisode(game, TestModel);
		AdviceView av = new AdviceView(mandatoryAdvice);
		av.setVisible(true);
		GameView gv = new GameView(game).showGame();


		gv.getFrame().addKeyListener(pacman.getKeyboardInput());
		boolean isPause = false;// Pause indicator

		GameStatus gs = new GameStatus(isPause, mandatoryAdvice, av,
				MOVE.NEUTRAL, false, MOVE.NEUTRAL);

		av.setGs(gs);

		while (!game.gameOver()) {
			
			MOVE move = pacman.getMove(game.copy(), -1, gs.isPause(),
					gs.isMandatoryAdvice());

			// pass the move to object gs
			gs.setMove(move);
			determinePause(gs);

			// System.out.println(gs.isMandatoryAdvice()+" "+gs.isAdvice());
			// System.out.println(move.name());
			if (gs.isPause() == false) {

				if (gs.isAdvice() == true) {
					if (gs.isMandatoryAdvice() == true) {
						move = gs.getAdviceMove();
					} else {
						move = lastMove;
					}
					// System.out.println(move.name());
					gs.setAdvice(false);
				} else {

					move = gs.getMove();
				}
				// System.out.println(move.name());
				game.advanceGame(move, ghosts.getMove(game.copy(), -1));
				pacman.processStep(game);
				steps--;
				if (steps == 0) {
					break;
				}
				try {
					// System.out.println(data.getDelay());
					Thread.sleep(data.getDelay());
				} catch (Exception e) {
				}
				gv.repaint();
			}
		}
		data.setStep(steps);
		score += game.getScore();
		data.setScore(score);
		pacman.terminate();
		gv.getFrame().dispose();
		av.dispose();
	}

	/** Estimate the current performance of a learner. */
	public static double evaluate(RLPacMan pacman, int width) {

		double sumScore = 0;

		for (int i = 0; i < width; i++) {
			Game game = new Game(rng.nextLong(), 3);
			pacman.startEpisode(game, true);

			while (!game.gameOver()) {
				game.advanceGame(pacman.getMove(game.copy(), -1),
						ghosts.getMove(game.copy(), -1));
				pacman.processStep(game);
			}

			sumScore += game.getScore();
		}

		return sumScore / width;
	}

	/** Observe a learner play a game. */
	public static void watch(RLPacMan pacman) {
		// Controller<MOVE> pacManController=new HumanController(new
		// KeyBoardInput());
		Game game = new Game(0);
		AdviceView av = new AdviceView(true);
		pacman.startEpisode(game, true);
		av.setVisible(true);
		GameView gv = new GameView(game).showGame();

		// GameView gvmessage=new GameView(game).showGame();

		gv.getFrame().addKeyListener(pacman.getKeyboardInput());

		boolean isPause = false;
		while (!game.gameOver()) {
			// game.advanceGame(pacman.getMove(game.copy(), -1),
			// ghosts.getMove(game.copy(), -1));
			MOVE move = pacman.getMove(game.copy(), -1);

			if (isPause == true && move != MOVE.RESUME) {

				move = MOVE.NEUTRAL;
			}

			if (move == MOVE.PAUSE) {
				av.pauseGame();
				isPause = true;
				// move = MOVE.NEUTRAL;
			} else if (move == MOVE.RESUME) {
				av.resumeGame();
				isPause = false;
				move = MOVE.NEUTRAL;
			}

			if (move == MOVE.ADDOWN) {
				isPause = true;
				av.giveAdvice("Down");
				move = MOVE.NEUTRAL;
			} else if (move == MOVE.ADLEFT) {
				isPause = true;
				move = MOVE.NEUTRAL;
			} else if (move == MOVE.ADRIGHT) {
				isPause = true;
				move = MOVE.NEUTRAL;
			} else if (move == MOVE.ADUP) {
				isPause = true;
				move = MOVE.NEUTRAL;
			}

			if (isPause == false) {
				game.advanceGame(move, ghosts.getMove(game.copy(), -1));
				pacman.processStep(game);

				try {
					Thread.sleep(DELAY);
				} catch (Exception e) {
				}
				gv.repaint();
			}
			// gvmessage.repaint();
		}
		// pacman.terminate();
	}

	/** Select a teacher from the independent students. */
	public static void findBestTeacher() {

		double[] scores = new double[REPEATS];

		for (int i = 0; i < REPEATS; i++) {
			BasicRLPacMan pacman = (BasicRLPacMan) create("independent");
			pacman.loadPolicy(DIR + "/independent/policy" + i);
			scores[i] = evaluate(pacman, 500);
			System.out.println(DIR + "/independent/policy" + i + ": "
					+ scores[i]);
		}

		int bestPolicy = 0;
		for (int i = 0; i < REPEATS; i++)
			if (scores[i] > scores[bestPolicy])
				bestPolicy = i;

		System.out.println("Best: " + DIR + "/independent/policy" + bestPolicy);
	}

	/** Make a plottable file of Q-value gaps over a few episodes. */
	public static void plotGaps() {

		DataFile file = new DataFile("myData/" + TEACHER + "/teacher/gaps");
		file.clear();

		BasicRLPacMan pacman = (BasicRLPacMan) create("teacher");
		int x = 0;

		for (int i = 0; i < 1; i++) {
			Game game = new Game(rng.nextLong());
			pacman.startEpisode(game, true);

			while (!game.gameOver()) {

				double[] qvalues = pacman.getQValues();
				Arrays.sort(qvalues);
				double gap = qvalues[qvalues.length - 1] - qvalues[0];

				file.append(x + "\t" + gap + "\n");
				x++;

				game.advanceGame(pacman.getMove(game.copy(), -1),
						ghosts.getMove(game.copy(), -1));
				pacman.processStep(game);
			}
		}

		file.close();
	}

	/** Test SVM choice prediction. */
	public static void testSVM() {

		BasicRLPacMan student = (BasicRLPacMan) create("independent");
		BasicRLPacMan teacher = (BasicRLPacMan) create("teacher");
		PredictImportantMistakes strategy = new PredictImportantMistakes(0);

		for (int i = 0; i < 300; i++) {
			Game game = new Game(rng.nextLong());
			student.startEpisode(game, false);
			teacher.startEpisode(game, true);

			strategy.startEpisode();
			int right = 0, wrong = 0, truePos = 0, falseNeg = 0, falsePos = 0;

			while (!game.gameOver()) {
				MOVE advice = teacher.getMove(game, -1);
				MOVE choice = student.getMove(game, -1);
				strategy.recordExample(teacher, choice);

				if (i > 0) {
					MOVE guess = strategy.predictChoice(teacher);
					boolean predict = (guess != advice);
					boolean mistake = (choice != advice);

					if (guess == choice)
						right++;
					else
						wrong++;

					if (mistake && predict)
						truePos++;
					else if (mistake && !predict)
						falseNeg++;
					else if (!mistake && predict)
						falsePos++;
				}

				game.advanceGame(choice, ghosts.getMove(game.copy(), -1));
				student.processStep(game);
				teacher.processStep(game);
			}

			if (i > 0) {
				double accuracy = right / (double) (right + wrong);
				double precision = truePos / (double) (truePos + falsePos);
				double recall = truePos / (double) (truePos + falseNeg);

				DecimalFormat f = new DecimalFormat("#.##");
				System.out.println("During episode " + i + ": a="
						+ f.format(accuracy) + ", p=" + f.format(precision)
						+ ", r=" + f.format(recall));
			}
		}
	}

	/** Compare areas under two types of learning curves. */
	public static void compare(String dir1, String dir2) {

		LearningCurve[] curves1 = new LearningCurve[REPEATS];
		for (int i = 0; i < REPEATS; i++)
			curves1[i] = new LearningCurve(LENGTH + 1, TRAIN, "myData/" + dir1
					+ "/curve" + i);

		double[] areas1 = new double[REPEATS];
		for (int i = 0; i < REPEATS; i++)
			areas1[i] = curves1[i].area();

		LearningCurve[] curves2 = new LearningCurve[REPEATS];
		for (int i = 0; i < REPEATS; i++)
			curves2[i] = new LearningCurve(LENGTH + 1, TRAIN, "myData/" + dir2
					+ "/curve" + i);

		double[] areas2 = new double[REPEATS];
		for (int i = 0; i < REPEATS; i++)
			areas2[i] = curves2[i].area();

		double t0 = Stats.t(areas1, areas2);
		double dof = Stats.dof(areas1, areas2);
		System.out.println(dir1 + " > " + dir2 + " with 95% confidence if:");
		System.out.println(t0 + " > t_0.05_" + dof);
	}
}
