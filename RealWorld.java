import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * @author Qudsia
 * 
 */
public class RealWorld {

	int cellWidth = 200; // 20 centimeters = 200 millimeters
	static final int ROWS = 7;
	static final int COLS = 10;
	private static int[][] grid;
	World world;
	Cell currentCell;
	Cell goal;

	private static Queue<Cell> path = new Queue<Cell>(); // the path found
																// by bestfs

	// pq is a list that is used like a priority queue with the help of pqPeek()
	// and pqPoll()
	// because lejos doesn't provide a priority queue
	private List<Cell> pq = new LinkedList<Cell>();
	static List<Cell> cells = new ArrayList<Cell>();

	public RealWorld(World world) {

		setGrid(new int[ROWS][COLS]);
		populate();
		this.world = world;
		setEnd(world.goal.col, world.goal.row);
		setStart(world.currentCell.col, world.currentCell.row);
	}

	public Cell getCell(int row, int col) {
		if (cells.isEmpty()) {
			return null;
		} else {
			for (Cell c : cells) {
				if (c.row == row && c.col == col) {
					return c;
				}
			}
		}

		return null;
	}
	
	// populates with -1 or 99
	public void populate() {
		// assign -1 to edges and 99 to inside cells
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (i == 0 || j == 0 || i == 6 || j == 9) {
					getGrid()[i][j] = -1;
				} else {
					getGrid()[i][j] = 99;
				}

				cells.add(new Cell(j, i, getGrid()[i][j]));
			}
		}

	}

	public void updateGridValues() {
		// reset all values to 99 except borders and obstacles
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (getGrid()[i][j] != -1) {
					getGrid()[i][j] = 99;
					Cell c = getCell(i, j);
					c.value = 99;
				}
			}
		}
		// this.printGrid();
		// call bfs to update new distance values
		bfs();

	}

	// populate distance from goal using breadth first search
	public void bfs() {

		// Cell cell = currentCell;
		Queue<Cell> qe = new Queue<Cell>();
		goal.value = 0;
		qe.push(goal);

		while (!qe.isEmpty()) {
			Cell cell = (Cell) qe.pop();

			// if it is not a border or obstacle
			if (getGrid()[cell.row][cell.col] == 99) {
				// populate the grid with the distance from goal
				getGrid()[cell.row][cell.col] = cell.value;

				Cell c = getCell(cell.row, cell.col);
				c.value = cell.value;
			}
			// if top cell hasn't been populated
			if (cell.row - 1 > 0 && getGrid()[cell.row - 1][cell.col] == 99) {
				// make a cell with distance value and add to queue
				qe.push(new Cell(cell.col, cell.row - 1, cell.value + 1));

				Cell c = getCell(cell.row - 1, cell.col);
				c.value = cell.value + 1;
			}
			// if bottom cell hasn't been populated
			if (cell.row + 1 < ROWS && getGrid()[cell.row + 1][cell.col] == 99) {
				// make a cell with distance value and add to queue
				qe.push(new Cell(cell.col, cell.row + 1, cell.value + 1));
				Cell c = getCell(cell.row + 1, cell.col);
				c.value = cell.value + 1;
			}
			if (cell.col + 1 < COLS && getGrid()[cell.row][cell.col + 1] == 99) {
				// make a cell with distance value and add to queue
				qe.push(new Cell(cell.col + 1, cell.row, cell.value + 1));
				Cell c = getCell(cell.row, cell.col + 1);
				c.value = cell.value + 1;
			}
			if (cell.col - 1 > 0 && getGrid()[cell.row][cell.col - 1] == 99) {
				// make a cell with distance value and add to queue
				qe.push(new Cell(cell.col - 1, cell.row, cell.value + 1));
				Cell c = getCell(cell.row, cell.col - 1);
				c.value = cell.value + 1;
			}

		}
	}

	// prints the grid (debug code)
	public void printGrid() {
		// testing the grid
		String output = "";
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				output += "\t" + getGrid()[row][col];
			}
			output += "\n";
		}
		System.out.println(output);
	}

	public double cellDistance(int toMove) {
		return (toMove * cellWidth);
	}

	// sets the goal
	public void setEnd(int x, int y) {
		getGrid()[y][x] = 0;
		goal = new Cell(x, y);
		Cell c = getCell(y, x);
		c.value = 0;
		bfs();
	}

	// builds obstacles
	public void buildObstacle(int x1, int y1, int x2, int y2) {

		for (int i = y2; i <= y1; i++) {
			for (int j = x2; j <= x1; j++) {
				getGrid()[i][j] = -1;

				Cell c = getCell(i, j);
				c.value = -1;
			}
		}
	}

	// sets the starting point the robot is on
	public void setStart(int x, int y) {
		currentCell = new Cell(x, y, getGrid()[y][x]);
	}

	public void findPath() {
		Cell cell = gbfs();
		if (cell != null) {
			generatePath(cell);
		} else {
			System.out.println("GBFS failed!");
		}

	}

	// finds the goal using greedy best first search
	public Cell gbfs() {

		// make a (CLOSED) list
		List<Cell> pqClosed = new LinkedList<Cell>();

		// add the start cell to (OPEN) priority queue pq
		Cell cell = getCell(currentCell.row, currentCell.col);
		cell.color = "blue";
		pq.add(cell);

		while (!pq.isEmpty()) {

			cell = (Cell) pqPoll();

			detectObstacle(cell);
			// this.printGrid();

			if (!pqClosed.contains(cell)) {
				pqClosed.add(cell);

				if (cell.value == 0) {
					return cell;
				}

				Cell top, bottom, left, right;

				List<Cell> neighbors = new ArrayList<Cell>();

				// top cell
				if (cell.row - 1 > 0 && getGrid()[cell.row - 1][cell.col] != -1) {

					top = getCell(cell.row - 1, cell.col);
					if (!top.color.equals("blue") && !pqClosed.contains(top)) {
						top.color = "blue";
						top.parent = cell;
						neighbors.add(top);
					}

				}
				// bottom cell
				if (cell.row + 1 < ROWS && getGrid()[cell.row + 1][cell.col] != -1) {

					bottom = getCell(cell.row + 1, cell.col);
					if (!bottom.color.equals("blue")
							&& !pqClosed.contains(bottom)) {
						bottom.color = "blue";
						bottom.parent = cell;
						neighbors.add(bottom);
					}
				}
				// right cell
				if (cell.col + 1 < COLS && getGrid()[cell.row][cell.col + 1] != -1) {

					right = getCell(cell.row, cell.col + 1);
					if (!right.color.equals("blue")
							&& !pqClosed.contains(right)) {
						right.color = "blue";
						right.parent = cell;
						neighbors.add(right);
					}
				}

				// left cell
				if (cell.col - 1 > 0 && getGrid()[cell.row][cell.col - 1] != -1) {

					left = getCell(cell.row, cell.col - 1);
					if (!left.color.equals("blue") && !pqClosed.contains(left)) {
						left.color = "blue";
						left.parent = cell;
						neighbors.add(left);
					}
				}

				// find the smallest child node
				if (neighbors.size() > 0) {

					Cell c = neighbors.get(0);
					for (int i = 0; i < neighbors.size(); i++) {
						if (c.value > neighbors.get(i).value) {
							c = neighbors.get(i);
						}
					}

					// add the child with the smallest distance value to the
					// queue
					pq.add(c);

				}
			}
		}
		return null;
	}

	public static void generatePath(Cell cell) {

		Stack<Cell> tmp = new Stack<Cell>();
		tmp.push(cell);
		Cell parent = cell.parent;

		while (parent != null) {
			tmp.push(parent);
			parent = parent.parent;
		}

		while (!tmp.isEmpty()) {
			path.push(tmp.pop());
		}
	}

	public void detectObstacle(Cell cell) {
		Cell l, r, t, b, trd, tld, brd, bld;
		int row, col;
		int[][] g = world.grid;

		// top cell
		row = cell.row - 1;
		col = cell.col;
		if (row > 0 && g[row][col] == -1) {

			t = getCell(row, col);
			if (t.value != -1) {
				getGrid()[row][col] = -1;
				t.value = -1;
				// update values
				updateGridValues();
			}

		}
		// top right diagonal
		col = col + 1;
		if (row > 0 && col < COLS && g[row][col] == -1) {

			trd = getCell(row, col);
			if (trd.value != -1) {
				getGrid()[row][col] = -1;
				trd.value = -1;
				// update values
				updateGridValues();
			}

		}

		// top left diagonal
		col = col - 2;
		if (row > 0 && col > 0 && g[row][col] == -1) {

			tld = getCell(row, col);
			if (tld.value != -1) {
				getGrid()[row][col] = -1;
				tld.value = -1;
				// update values
				updateGridValues();
			}

		}
		// bottom cell
		row = cell.row + 1;
		col = cell.col;
		if (row < ROWS && g[row][col] == -1) {

			b = getCell(row, col);
			if (b.value != -1) {
				getGrid()[row][col] = -1;
				b.value = -1;
				// update values
				updateGridValues();
			}
		}

		// bottom right diagonal cell
		col = col + 1;
		if (row < ROWS && col < COLS && g[row][col] == -1) {

			brd = getCell(row, col);
			if (brd.value != -1) {
				getGrid()[row][col] = -1;
				brd.value = -1;
				// update values
				updateGridValues();
			}
		}

		// bottom left diagonal cell
		col = col - 2;
		if (row < ROWS && col > 0 && g[row][col] == -1) {

			bld = getCell(row, col);
			if (bld.value != -1) {
				getGrid()[row][col] = -1;
				bld.value = -1;
				// update values
				updateGridValues();
			}
		}

		// right cell
		row = cell.row;
		col = cell.col + 1;
		if (col < COLS && g[row][col] == -1) {

			r = getCell(row, col);
			if (r.value != -1) {
				getGrid()[row][col] = -1;
				r.value = -1;
				// update values
				// System.out.println("obstacle found!");
				updateGridValues();
			}
		}
		// left cell
		row = cell.row;
		col = cell.col - 1;
		if (col > 0 && g[row][col] == -1) {

			l = getCell(row, col);
			if (l.value != -1) {
				getGrid()[row][col] = -1;
				l.value = -1;
				// update values
				updateGridValues();
			}
		}

	}

	// returns the smallest value cell and removes it from the pq list
	public Cell pqPoll() {

		if (pq != null) {

			Cell c = pq.get(0);
			// int index;

			for (int i = 0; i < pq.size(); i++) {
				if (c.value > pq.get(i).value) {
					c = pq.get(i);
					// index = i;
				}
			}

			pq.remove(c);

			return c;

		}

		return null;
	}

	// returns the smallest value cell
	public Cell pqPeek() {

		if (pq != null) {

			Cell c = pq.get(0);
			// int index;

			for (int i = 0; i < pq.size(); i++) {
				if (c.value > pq.get(i).value) {
					c = pq.get(i);
					// index = i;
				}
			}

			return c;

		}

		return null;
	}

	public static Queue<Cell> getPath() {
		return path;
	}

	public static void setPath(Queue<Cell> path) {
		path = path;
	}

	public static int[][] getGrid() {
		return grid;
	}

	public static void setGrid(int[][] g) {
		grid = g;
	}
}