package diningPhilosophers ;

import java.awt.Dimension;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DiningPhilosophers extends JPanel {
	private static final long serialVersionUID = 1L;

	// number of philosophers and forks
	public static final int SIZE = 5;
	// mutual exclusion for critical regions
	public static final Semaphore MUTEX = new Semaphore(1);

	// arrays of philosophers and forks
	public static Philosopher[] philosophers = new Philosopher[SIZE];
	public static Fork[] forks = new Fork[SIZE];

	private final int LABEL_WIDTH = 180;
	private final int LABEL_HEIGHT = 25;

	private final int PANEL_WIDTH = 700;
	private final int PANEL_HEIGHT = 350;

	private JLabel[] philLabels = new JLabel[SIZE];
	private JLabel[] forkLabels = new JLabel[SIZE];

	public DiningPhilosophers() {
		// set forks on the table
		for (int i = 0; i < SIZE; i++) {
			forks[i] = new Fork(i);
		}

		// initialize threads
		for (int i = 0; i < SIZE; i++) {
			philosophers[i] = new Philosopher(i);

			// assign forks to each philosopher
			philosophers[i].setRightFork(forks[i]);
			philosophers[i].setLeftFork(i == (SIZE - 1) ? forks[0] : forks[i + 1]);
		}

		constructGui();

		// start threads
		for (Thread t : philosophers) {
			t.start();
		}
	}

	private void constructGui() {
		for (int i = 0; i < SIZE; i++) {
			forkLabels[i] = forks[i].getLabel();
			philLabels[i] = philosophers[i].getLabel();
		}

		// set component bounds (only needed by Absolute Positioning)
		philLabels[0].setBounds(260, 15, LABEL_WIDTH, LABEL_HEIGHT);
		philLabels[1].setBounds(450, 110, LABEL_WIDTH, LABEL_HEIGHT);
		philLabels[2].setBounds(380, 240, LABEL_WIDTH, LABEL_HEIGHT);
		philLabels[3].setBounds(135, 240, LABEL_WIDTH, LABEL_HEIGHT);
		philLabels[4].setBounds(80, 110, LABEL_WIDTH, LABEL_HEIGHT);

		forkLabels[0].setBounds(190, 55, LABEL_WIDTH, LABEL_HEIGHT);
		forkLabels[1].setBounds(415, 55, LABEL_WIDTH, LABEL_HEIGHT);
		forkLabels[2].setBounds(435, 165, LABEL_WIDTH, LABEL_HEIGHT);
		forkLabels[3].setBounds(310, 280, LABEL_WIDTH, LABEL_HEIGHT);
		forkLabels[4].setBounds(150, 165, LABEL_WIDTH, LABEL_HEIGHT);

		// add components to panel
		for (JLabel label : philLabels) {
			add(label);
		}
		for (JLabel label : forkLabels) {
			add(label);
		}

		// adjust size and set layout
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		setLayout(null);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Dining Philosophers");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new DiningPhilosophers());
		frame.pack();
		frame.setVisible(true);
	}
}