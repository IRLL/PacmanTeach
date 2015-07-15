package pacman.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.CardLayout;

import javax.swing.JTextPane;

import pacman.GameStatus;
import pacman.game.Constants.MOVE;
import pacman.utils.DataFile;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdviceView extends JFrame {

	private JPanel contentPane;
	private JTextPane txtpnT;
	private String lastadvice;
	private JButton jbup;
	private JButton jbdown;
	private JButton jbleft;
	private JButton jbright;
	private boolean mandatoryAdvice;
	private GameStatus gs;
	private MOVE curretMove;

	public GameStatus getGs() {
		return gs;
	}

	public void setGs(GameStatus gs) {
		this.gs = gs;
	}

	/**
	 * Create the frame.
	 */
	public AdviceView(boolean mandatoryAdvice) {
		curretMove = MOVE.NEUTRAL;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) (screen.getWidth() * 18 / 32),
				(int) (screen.getHeight() * 3 / 8));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		txtpnT = new JTextPane();
		txtpnT.setEditable(false);
		this.mandatoryAdvice = mandatoryAdvice;

		if (mandatoryAdvice == false) {
			txtpnT.setText("The game is runnning\nPress P to Pause\nPress R to Resume\n"
					+ "Advice:"
					+ "\nPlayer's Choice: NO Choice");
		} else {
			txtpnT.setText("The game is runnning\nPress P to Pause\nPress R to Resume\n"
					+ "Mandatory Advice: " + mandatoryAdvice + "\nAdvice:");

		}
		lastadvice = "No advice";
		contentPane.setLayout(new CardLayout(0, 0));
		contentPane.add(txtpnT, "name_1388016223004450000");

		JPanel jbButtons = new JPanel();

		jbButtons.setLayout(new FlowLayout());
		// Initial 4 buttons
		jbup = new JButton("UP");
		jbdown = new JButton("DOWN");
		jbleft = new JButton("LEFT");
		jbright = new JButton("RIGHT");

		jbButtons.add(jbup);
		jbButtons.add(jbdown);
		jbButtons.add(jbleft);
		jbButtons.add(jbright);

		jbup.setToolTipText("Choose UP action");
		jbdown.setToolTipText("Choose DOWN action");
		jbleft.setToolTipText("Choose LEFT action");
		jbright.setToolTipText("Choose RIGHT action");

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPane, BorderLayout.CENTER);
		// getContentPane().add(jbButtons, BorderLayout.SOUTH);

		if (mandatoryAdvice == true) {
			jbup.setEnabled(false);
			jbdown.setEnabled(false);
			jbleft.setEnabled(false);
			jbright.setEnabled(false);

		}

		// setContentPane(contentPane);

		jbup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gs.setAdviceMove(MOVE.UP);
				gs.setPause(false);
				// TODO Auto-generated method stub
			}

		});

		jbdown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				gs.setAdviceMove(MOVE.DOWN);
				gs.setPause(false);
				// TODO Auto-generated method stub
			}

		});

		jbleft.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				gs.setAdviceMove(MOVE.LEFT);
				gs.setPause(false);
				// TODO Auto-generated method stub
			}

		});

		jbright.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				gs.setAdviceMove(MOVE.RIGHT);
				gs.setPause(false);
				// TODO Auto-generated method stub
			}

		});

	}

	public MOVE getCurretMove() {
		return curretMove;
	}

	public void setCurretMove(MOVE curretMove) {
		this.curretMove = curretMove;
		String message = null;
		if (mandatoryAdvice == false) {
			message = "The game is paused\nPress P to Pause\nPress R to Resume\n"
					+ "Advice: "
					+ lastadvice + "\nPlayer's Choice: " + curretMove.name();
		} else {
			message = "The game is paused\nPress P to Pause\nPress R to Resume\n"
					+ "Advice: "
					+ lastadvice;

		}
		txtpnT.setText(message);
	}

	// display the advice
	public void giveAdvice(String advice) {
		lastadvice = advice;
		String message = null;
		if (mandatoryAdvice == false) {
			message = "The game is paused\nPress P to Pause\nPress R to Resume\n"
					+ "Advice: "
					+ advice + "\nPlayer's Choice: NO Choice";
		} else {
			message = "The game is paused\nPress P to Pause\nPress R to Resume\n"
					+ "\nAdvice: "
					+ advice;

		}
		txtpnT.setText(message);
	}

	// resume game
	public void resumeGame() {
		String message = null;
		if (mandatoryAdvice == false) {
			message = "The game is running\nPress P to Pause\nPress R to Resume\n"
					+ "Advice: Player Resumes, No Advice"
					+ "\nPlayer's Choice: NO Choice";
		} else {
			message = "The game is running\nPress P to Pause\nPress R to Resume\n"
					+ "Advice: Player Resumes, No Advice";

		}
		txtpnT.setText(message);
		if (gs.isAdvice() == true) {
			DataFile datafile = new DataFile("adivcerecords.txt");
			datafile.append(lastadvice+ " "
					+ this.curretMove.name() + "\n");
			datafile.close();
		}
	}

	// resume game
	public void pauseGame() {
		String message = null;
		if (mandatoryAdvice == false) {
			message = "The game is paused\nPress P to Pause\nPress R to Resume\n"
					+ "Advice: Player Pauses, No Advice"
					+ "\nPlayer's Choice: NO Choice";
		} else {
			message = "The game is paused\nPress P to Pause\nPress R to Resume\n"
					+ "Advice: Player Pauses, No Advice";

		}
		txtpnT.setText(message);
	}

}
