package diningPhilosophers;

import java.awt.Color;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;

public class Philosopher extends Thread {
	// philosopher state definitions
	public enum State {
		THINKING, HUNGRY, EATING
	}
	// wait time
	private final int WAIT_TIME = 5000;

	private int id;
	private State state;
	private Semaphore self; // one semaphore per philosopher
	private Fork leftFork;
	private Fork rightFork;

	private JLabel label; // label for GUI

	// a philosopher begins with THINKING state
	public Philosopher(int id) {
		this(id, State.THINKING);
	}

	private Philosopher(int id, State state) {
		this.id = id;
		this.state = state;
		
		this.self = new Semaphore(0);
		this.label = new JLabel();
		this.leftFork = null;
		this.rightFork = null;
	}

	// start a random wait time for each state
	private void thinkOrEat() {
		try {
			Thread.sleep((long) Math.round(Math.random() * WAIT_TIME));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				// print current state
				System.out.println(toString());

				switch (state) {
				case THINKING:
					updatePhilosopherLabel();
					thinkOrEat();

					// philosopher declares they want to eat
					DiningPhilosophers.MUTEX.acquire();
					state = State.HUNGRY;

					break;

				case HUNGRY:
					updatePhilosopherLabel();

					// philosopher tries to acquire 2 forks
					checkBeforeEat(this);
					DiningPhilosophers.MUTEX.release();
					self.acquire();
					state = State.EATING;

					break;

				case EATING:
					// handle GUI
					changeColorPickUpForks();
					updatePhilosopherLabel();

					// random wait time
					thinkOrEat();

					// enter critical region
					DiningPhilosophers.MUTEX.acquire();

					// philosopher finishes eating
					state = State.THINKING;

					// handle GUI
					changeColorPutDownForks();

					// tell neighbors they can now eat
					checkBeforeEat(getLeft());
					checkBeforeEat(getRight());

					// exit critical region
					DiningPhilosophers.MUTEX.release();

					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// forks are red when in use
	private void changeColorPickUpForks() {
		leftFork.getLabel().setForeground(Color.red);
		rightFork.getLabel().setForeground(Color.red);
	}

	// forks are green when free
	private void changeColorPutDownForks() {
		leftFork.getLabel().setForeground(Color.green);
		rightFork.getLabel().setForeground(Color.green);
	}

	// return the philosopher on the left
	private Philosopher getLeft() {
		return DiningPhilosophers.philosophers[id == (DiningPhilosophers.SIZE - 1) ? 0 : (id + 1)];
	}
	
	// return the philosopher on the right
	private Philosopher getRight() {
		return DiningPhilosophers.philosophers[id == 0 ? (DiningPhilosophers.SIZE - 1) : id - 1];
	}

	private void checkBeforeEat(Philosopher p) {
		if (p.getLeft().state != State.EATING && p.state == State.HUNGRY && p.getRight().state != State.EATING) {
			p.state = State.EATING;
			p.self.release();
		}
	}

	private void updatePhilosopherLabel() {
		label.setText(toString());
		switch (state) {
		case THINKING:
			label.setForeground(Color.GREEN);
			break;
		case HUNGRY:
			label.setForeground(Color.ORANGE);
			break;
		case EATING:
			label.setForeground(Color.RED);
			break;
		}
	}

	public void setLeftFork(Fork leftFork) {
		this.leftFork = leftFork;
	}

	public void setRightFork(Fork rightFork) {
		this.rightFork = rightFork;
	}

	public String toString() {
		return "Philosopher " + id + " is " + state;
	}

	public JLabel getLabel() {
		return label;
	}
}