package pacman;

import pacman.game.AdviceView;
import pacman.game.Constants.MOVE;

public class GameStatus {// Encapsulation of necessary data members

	private boolean pause;
	private boolean mandatoryAdvice;
	private AdviceView av;
	private MOVE move;
	private MOVE adviceMove;
	private boolean isAdvice;

	public GameStatus(boolean pause, boolean mandatoryAdvice, AdviceView av,
			MOVE move, boolean isAdvice, MOVE adviceMove) {

		this.pause = pause;
		this.mandatoryAdvice = mandatoryAdvice;
		this.av = av;
		this.move = move;
		this.adviceMove = move;
		this.isAdvice = isAdvice;
		// TODO Auto-generated constructor stub
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public boolean isMandatoryAdvice() {
		return mandatoryAdvice;
	}

	public void setMandatoryAdvice(boolean mandatoryAdvice) {
		this.mandatoryAdvice = mandatoryAdvice;
	}

	public AdviceView getAv() {
		return av;
	}

	public void setAv(AdviceView av) {
		this.av = av;
	}

	public MOVE getMove() {
		return move;
	}

	public void setMove(MOVE move) {
		this.move = move;
	}

	public MOVE getAdviceMove() {
		return adviceMove;
	}

	public void setAdviceMove(MOVE adviceMove) {
		this.adviceMove = adviceMove;
	}

	public boolean isAdvice() {
		return isAdvice;
	}

	public void setAdvice(boolean isAdvice) {
		this.isAdvice = isAdvice;
	}

}
