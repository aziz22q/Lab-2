/**
 * 
 */

/**
 * @author Qudsia
 * 
 */
public class Cell {

	int row;
	int col;
	int value;
	String color;
	Cell parent;

	public Cell(int x, int y) {
		row = y;
		col = x;
		color="white";
	}

	public Cell(int x, int y, int value) {
		row = y;
		col = x;
		this.value = value;
		color="white";
	}
}