import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * @author Qudsia
 * 
 */
public class World {

	int cellWidth = 200; // 20 centimeters = 200 millimeters
	static final int ROWS = 7;
	static final int COLS = 10;
	static int[][] grid;
	Cell currentCell;
	Cell goal;
	static Stack<Cell> st = new Stack<Cell>();// stack used in dfs
	private List<Cell> pq = new LinkedList<Cell>();

	private static Queue<Cell> path = new Queue<Cell>(); // the path found
																// by dfs
	static List<Cell> cells = new ArrayList<Cell>();

	public static Cell getCell(int row, int col) {
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

	public World() {

		grid = new int[ROWS][COLS];
		populate();

	}

	// populates with -1 or 99
	public void populate() {
		// assign -1 to edges and 99 to inside cells
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (i == 0 || j == 0 || i == 6 || j == 9) {
					grid[i][j] = -1;
				} else {
					grid[i][j] = 99;
				}

				cells.add(new Cell(j, i, grid[i][j]));
			}
		}

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
			if (grid[cell.row][cell.col] == 99) {
				// populate the grid with the distance from goal
				grid[cell.row][cell.col] = cell.value;

				Cell c = getCell(cell.row, cell.col);
				c.value = cell.value;
			}
			// if top cell hasn't been populated
			if (cell.row - 1 > 0 && grid[cell.row - 1][cell.col] == 99) {
				// make a cell with distance value and add to queue
				qe.push(new Cell(cell.col, cell.row - 1, cell.value + 1));

				Cell c = getCell(cell.row - 1, cell.col);
				c.value = cell.value + 1;
			}
			// if bottom cell hasn't been populated
			if (cell.row + 1 < ROWS && grid[cell.row + 1][cell.col] == 99) {
				// make a cell with distance value and add to queue
				qe.push(new Cell(cell.col, cell.row + 1, cell.value + 1));
				Cell c = getCell(cell.row + 1, cell.col);
				c.value = cell.value + 1;
			}
			if (cell.col + 1 < COLS && grid[cell.row][cell.col + 1] == 99) {
				// make a cell with distance value and add to queue
				qe.push(new Cell(cell.col + 1, cell.row, cell.value + 1));
				Cell c = getCell(cell.row, cell.col + 1);
				c.value = cell.value + 1;
			}
			if (cell.col - 1 > 0 && grid[cell.row][cell.col - 1] == 99) {
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
				output += "\t" + grid[row][col];
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
		grid[y][x] = 0;
		goal = new Cell(x, y);
		Cell c = getCell(y, x);
		c.value = 0;
		bfs();
	}

	// builds obstacles
	public void buildObstacle(int x1, int y1, int x2, int y2) {

		for (int i = y2; i <= y1; i++) {
			for (int j = x2; j <= x1; j++) {
				grid[i][j] = -1;

				Cell c = getCell(i, j);
				c.value = -1;
			}
		}
	}

	// sets the starting point the robot is on
	public void setStart(int x, int y) {
		currentCell = getCell(x, y);
		;
	}

	public static void generatePath(Cell cell) {

		Stack<Cell> tmp = new Stack<Cell>();
		tmp.push(cell);
		Cell parent = cell.parent;
		path.clear();
		// System.out.println("depth first search path");
		// System.out.println(cell.value + " " + cell.row + " " + cell.col );
		while (parent != null) {
			// System.out.println(parent.value + " " + parent.row + " " +
			// parent.col );
			tmp.push(parent);
			parent = parent.parent;
		}

		while (!tmp.isEmpty()) {
			path.push(tmp.pop());
		}
	}

	public void findPath() {

		st.clear();
		st.push(this.currentCell);
		dfs();

	}
	
	public void findPathGBFS() {

		Cell cell = gbfs();
		if (cell != null) {
			generatePath(cell);
		} else {
			System.out.println("GBFS failed!");
		}

	}

	// depth first search that finds the path
	public static void dfs() {

		// pop the stack;
		Cell cell = st.pop();

		// go to the popped cell
		// mark the popped cell as gray
		cell.color = "grey";

		// if (item popped == final cell)
		if (cell.value == 0) {
			// done!

			generatePath(cell);
			return;
		} else {

			Cell top, bottom, left, right;

			List<Cell> neighbors = new ArrayList<Cell>();

			if (cell.row - 1 > 0 && grid[cell.row - 1][cell.col] != -1) {
				top = getCell(cell.row - 1, cell.col);

				if (!top.color.equals("grey") && !top.color.equals("black")) {
					top.parent = cell;
					neighbors.add(top);
				}
			}
			if (cell.row + 1 < ROWS && grid[cell.row + 1][cell.col] != -1) {
				bottom = getCell(cell.row + 1, cell.col);

				if (!bottom.color.equals("grey")
						&& !bottom.color.equals("black")) {
					bottom.parent = cell;
					neighbors.add(bottom);
				}
			}
			if (cell.col + 1 < COLS && grid[cell.row][cell.col + 1] != -1) {
				right = getCell(cell.row, cell.col + 1);

				if (!right.color.equals("grey") && !right.color.equals("black")) {
					right.parent = cell;
					neighbors.add(right);
				}
			}
			if (cell.col - 1 > 0 && grid[cell.row][cell.col - 1] != -1) {

				left = getCell(cell.row, cell.col - 1);

				if (!left.color.equals("grey") && !left.color.equals("black")) {
					left.parent = cell;
					neighbors.add(left);
				}
			}

			for (int i = 0; i < neighbors.size(); i++) {

				st.push(neighbors.get(i));
				// visit the child cell
				dfs();
			}

			cell.color = "black";

		}

	}

	public static Queue<Cell> getPath() {
		return path;
	}

	public static void setPath(Queue<Cell> path) {
		path = path;
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


			// this.printGrid();

			if (!pqClosed.contains(cell)) {
				pqClosed.add(cell);

				if (cell.value == 0) {
					return cell;
				}

				Cell top, bottom, left, right;

				List<Cell> neighbors = new ArrayList<Cell>();

				// top cell
				if (cell.row - 1 > 0 && grid[cell.row - 1][cell.col] != -1) {

					top = getCell(cell.row - 1, cell.col);
					if (!top.color.equals("blue") && !pqClosed.contains(top)) {
						top.color = "blue";
						top.parent = cell;
						neighbors.add(top);
					}

				}
				// bottom cell
				if (cell.row + 1 < ROWS && grid[cell.row + 1][cell.col] != -1) {

					bottom = getCell(cell.row + 1, cell.col);
					if (!bottom.color.equals("blue")
							&& !pqClosed.contains(bottom)) {
						bottom.color = "blue";
						bottom.parent = cell;
						neighbors.add(bottom);
					}
				}
				// right cell
				if (cell.col + 1 < COLS && grid[cell.row][cell.col + 1] != -1) {

					right = getCell(cell.row, cell.col + 1);
					if (!right.color.equals("blue")
							&& !pqClosed.contains(right)) {
						right.color = "blue";
						right.parent = cell;
						neighbors.add(right);
					}
				}

				// left cell
				if (cell.col - 1 > 0 && grid[cell.row][cell.col - 1] != -1) {

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
}