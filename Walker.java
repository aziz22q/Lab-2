import java.util.Queue;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class Walker {

	static NXTRegulatedMotor leftMotor = Motor.C;
	static NXTRegulatedMotor rightMotor = Motor.A;
	static int speedDrive = 200;
	static int minDistance = 420; // 430
	static int TURNING_DISTANCE = 175; // 185
	static RealWorld real;
	static World legoworld;
	static boolean goalReached = false;
	static char direction = 'n';

	public static void main(String[] args) {
		Button.waitForAnyPress(); // has robot wait to run until it heres a button click

		World world;
		world = new World();
		world.buildObstacle(5, 3, 5, 1);
		world.buildObstacle(6, 5, 6, 5);
		world.setEnd(8, 1);
		world.setStart(1, 1);

		RealWorld real = new RealWorld(world);

		pathwalker();

	}

	public static void pathwalker() {
		
		int row = real.currentCell.row;
		int col = real.currentCell.col;

		Queue path = real.getPath();

		while (!path.empty()) {
			Cell c = (Cell) path.pop();

			if (c.col == col && c.row > row) // north
			{

				if (direction == 'n') {
					// go straight
					travel(minDistance);
				} else if (direction == 'e') { // facing east
					turn(true);
					direction = 'n';
					travel(minDistance);
				} else if (direction == 's') {
					turn(true);
					turn(true);
					direction = 'n';
					travel(minDistance);
				} else if (direction == 'w') {
					turn(false);
					direction = 'n';
					travel(minDistance);
				}
			} else if (c.col > col && c.row == row) // east
			{
				if (direction == 'n') {
					turn(false);// turn right
					direction = 'e'; // reset direction
					travel(minDistance);
				} else if (direction == 'e') { // facing east
					travel(minDistance);
				} else if (direction == 's') {
					turn(true);
					direction = 'e';
					travel(minDistance);
				} else if (direction == 'w') {
					turn(false);
					turn(false);
					direction = 'e';
					travel(minDistance);
				}

			} else if (c.col == col && c.row < row) { // south
				if (direction == 'n') {
					turn(false);// turn right
					turn(false);// turn right
					direction = 's'; // reset direction
					travel(minDistance);
				} else if (direction == 'e') { // facing east
					turn(false);// turn right
					direction = 's';
					travel(minDistance);
				} else if (direction == 's') {
					travel(minDistance); // go straight
				} else if (direction == 'w') {
					turn(true); // turn left
					direction = 's';
					travel(minDistance);
				}
			} else if (c.col < col && c.row == row) // west
			{
				if (direction == 'n') {
					turn(true);// turn left
					direction = 'w'; // reset direction
					travel(minDistance);
				} else if (direction == 'e') { // facing east
					turn(false);// turn right
					turn(false);// turn right
					direction = 'w';
					travel(minDistance);
				} else if (direction == 's') {
					turn(false); // turn right
					direction = 'w';
					travel(minDistance); // go straight
				} else if (direction == 'w') {
					travel(minDistance); // go straight
				}
			}

			col = c.col;
			row = c.row;
		}
	}

	/*
	 * This method moves the robot straight forward for a given distance
	 */
	public static void travel(int distance) {

		int numDegrees = distance;
		leftMotor.setSpeed(speedDrive); // set left motor
		leftMotor.resetTachoCount();
		rightMotor.setSpeed(speedDrive); // set right motor
		rightMotor.resetTachoCount();

		leftMotor.forward(); // engines on!
		rightMotor.forward();

		while ((leftMotor.getTachoCount() <= numDegrees) || // TachoCount =
															// motor angle in
															// degrees
				(rightMotor.getTachoCount() <= numDegrees)) {// check both
			if (leftMotor.getTachoCount() > numDegrees) {
				// if left is
				leftMotor.stop(); // done turn off
			}

			if (rightMotor.getTachoCount() > numDegrees) {
				// if right is
				rightMotor.stop(); // done turn off
			}

		}
		rightMotor.stop(); // to be sure
		leftMotor.stop(); // to be sure

	}

	/*
	 * if the given parameter is true ...turn robot to the left. if the given
	 * parameter is false...turn robot to the right.
	 */
	public static void turn(boolean left) {
		leftMotor.setSpeed(speedDrive); // set left motor
		leftMotor.resetTachoCount();
		rightMotor.setSpeed(speedDrive); // set right motor
		rightMotor.resetTachoCount();

		if (left) { // turn left
			leftMotor.backward(); // engines on!
			rightMotor.forward();

			while ((leftMotor.getTachoCount() >= -TURNING_DISTANCE) || // TachoCount
																		// =
																		// motor
																		// angle
																		// in
																		// degrees
					(rightMotor.getTachoCount() <= TURNING_DISTANCE)) // TachoCount
																		// =
																		// motor
																		// angle
																		// in
																		// degrees

			{// check both
				if (leftMotor.getTachoCount() < -TURNING_DISTANCE) {
					// if left is
					leftMotor.stop(); // done turn off
				}

				if (rightMotor.getTachoCount() > TURNING_DISTANCE) {
					// if right is
					rightMotor.stop(); // done turn off
				}

			}
			rightMotor.stop(); // to be sure
			leftMotor.stop(); // to be sure
		} else { // turn right
			leftMotor.forward(); // engines on!
			rightMotor.backward();

			while ((rightMotor.getTachoCount() >= -TURNING_DISTANCE) || // TachoCount
																		// =
																		// motor
																		// angle
																		// in
																		// degrees
					(leftMotor.getTachoCount() <= TURNING_DISTANCE)) // TachoCount
																		// =
																		// motor
																		// angle
																		// in
																		// degrees

			{// check both
				if (rightMotor.getTachoCount() < -TURNING_DISTANCE) {
					// if left is
					rightMotor.stop(); // done turn off
				}

				if (leftMotor.getTachoCount() > TURNING_DISTANCE) {
					// if right is
					leftMotor.stop(); // done turn off
				}

			}
			
			rightMotor.stop(); // to be sure
			leftMotor.stop(); // to be sure
		}
	}
}