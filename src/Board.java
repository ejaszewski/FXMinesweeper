import java.util.Random;

public class Board {

	/**
	 * Board Size Constants. Defined:
	 * 		Small 	  - 8x8, 10 Mines
	 * 		Medium 	  - 16x16, 40 Mines
	 * 		Large 	  - 16x32, 100 Mines
	 * 		Humongous - 32x32, 225 Mines
	 */
	public static final int SMALL = 0;
	public static final int MEDIUM = 1;
	public static final int LARGE = 2;
	public static final int HUMONGOUS = 3;

	/**
	 * Board View Matrix Constants
	 */
	public static final int HIDDEN = 0;
	public static final int SHOWN = 1;
	public static final int FLAGGED = 2;
	public static final int QMARK = 3;

	/**
	 * Board Constants
	 */
	public static final int MINE = -1;

	private static final int[][] boardSizes = { { 8, 8, 10 }, { 16, 16, 40 }, { 16, 32, 100 }, { 32, 32, 225 } };

	private int[][] board, viewMatrix;
	private int rows, cols, mines, flags;
	private int size;

	/**
	 * Creates a 'SMALL' board.
	 */
	public Board() {
		this(SMALL);
	}

	/**
	 * Creates a board using the defined board size constants.
	 * 
	 * @param size board size constant
	 */
	public Board(int size) {
		this(boardSizes[size][0], boardSizes[size][1], boardSizes[size][2]);
		this.size = size;
	}

	/**
	 * Creates a board of the given size with the given number of mines.
	 * 
	 * @param rows number of rows in the board
	 * @param cols number of cols in the board
	 * @param mines number of mines in the board.
	 */
	public Board(int rows, int cols, int mines) {
		this.rows = rows;
		this.cols = cols;
		this.mines = mines;
		this.flags = 0;
		board = new int[rows][cols];
		viewMatrix = new int[rows][cols];
		addMines(mines);
	}
	
	/**
	 * Creates a board using an integer array with the mines placed, and a view matrix.
	 * 
	 * @param board board with only mines placed
	 * @param viewMatrix view matrix to use
	 */
	public Board(int[][] board, int[][] viewMatrix, int mines) {
	    this.board = board;
	    this.viewMatrix = viewMatrix;
	    this.rows = board.length;
	    this.cols = board[0].length;
	    this.mines = mines;
	    this.flags = 0;
	    countMines();
	}

	/**
	 * Places the given number of mines on the board. Also generates adjacent
	 * mine numbers.
	 * 
	 * @param numMines number of mines to place
	 */
	private void addMines(int numMines) {
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < numMines; i++) {
			int r = rand.nextInt(rows), c = rand.nextInt(cols);
			while (board[r][c] == MINE) {
				r = rand.nextInt(rows);
				c = rand.nextInt(cols);
			}
			board[r][c] = MINE;
		}
		countMines();
	}
	
	/**
	 * Counts the mines around each square in the board.
	 */
	private void countMines() {
	    for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (board[r][c] == MINE)
                    for (int ro = -1; ro < 2; ro++)
                        for (int co = -1; co < 2; co++)
                            if (r + ro > -1 && r + ro < rows && c + co > -1 && c + co < cols
                                    && board[r + ro][c + co] != MINE)
                                board[r + ro][c + co]++;
	}
	
	/**
	 * A method that reveals an area of cells originating at the given row and col.
	 * Calls a separate recursive method that performs the revealing.
	 * 
     * @param row row of square to be revealed
     * @param col col of square to be revealed
	 * @return true if reveal was successful, false if out of bounds or on a mine
	 */
	public boolean reveal(int row, int col) {
		if (row < 0 || col < 0 || row > rows || col > cols)
			return false;
		if (board[row][col] == MINE)
			return false;
		revealImpl(row, col);
		return true;
	}

	private void revealImpl(int r, int c) {
		if (r < 0 || c < 0 || r >= rows || c >= cols)
			return;
		if (board[r][c] == -1)
			return;
		if (viewMatrix[r][c] == SHOWN)
			return;

		viewMatrix[r][c] = SHOWN;
		if (board[r][c] == 0) {
			for (int ro = -1; ro < 2; ro++)
				for (int co = -1; co < 2; co++)
					if (ro != 0 || co != 0)
						revealImpl(r + ro, c + co);
		}
	}

	/**
	 * Performs a standard flagging procedure on the given tile.
	 * If flags < mines then:
	 *     Hidden -> Flagged -> Question Mark -> Hidden
	 * If flags > mines then:
	 *     Hidden -> Question Mark -> Hidden
	 *  --or--
	 *     Flagged -> Question Mark
	 *     
	 * @param row row of square to be flagged
	 * @param col col of square to be flagged
	 */
	public void flag(int row, int col) {
		switch(viewMatrix[row][col]) {
		case HIDDEN: viewMatrix[row][col] = FLAGGED; flags++; break;
		case FLAGGED: viewMatrix[row][col] = QMARK; flags--; break;
		case QMARK: viewMatrix[row][col] = HIDDEN; break;
		}
	}
	
	/**
	 * This print method prints the board as it would be displayed (hiding, showing,
	 * flags, question marks). It calls the object's toString method.
	 */
	public void printBoard() {
		System.out.println(this);
	}
	
	/**
	 * This toString returns a String representation of the board as it would be displayed,
	 * i.e. with hidden squares, flagged squares, etc.
	 * 
	 * @return String representation of this board
	 */
	@Override
	public String toString() {
	    StringBuilder builder = new StringBuilder();
	    for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (viewMatrix[r][c] == SHOWN) {
                    if (board[r][c] < 0)
                        builder.append("M");
                    else if (board[r][c] == 0)
                        builder.append(" ");
                    else
                        builder.append(board[r][c]);
                } else
                    System.out.print("#");
            }
            builder.append("\n");
        }
	    return builder.toString().trim();
	}

	/**
	 * Gets the internal mines board.
	 * @return mines board
	 */
	public int[][] getBoard() {
		return this.board;
	}

	/**
	 * Gets the internal view matrix.
	 * @return view matrix
	 */
	public int[][] getViewMatrix() {
		return this.viewMatrix;
	}
	
	/**
	 * Sets the internal view matrix.
	 * @param viewMatrix new view matrix
	 */
	public void setViewMatrix(int[][] viewMatrix) {
		this.viewMatrix = viewMatrix;
	}

	/**
	 * Gets the number of rows in the board.
	 * @return number of rows in the board
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Gets the number of rows in the board.
	 * @return number of rows in the board
	 */
	public int getCols() {
		return cols;
	}
	
	/**
	 * Gets the number of mines on the board.
	 * @return the mines
	 */
	public int getMines() {
		return mines;
	}

	/**
	 * Gets the number of flagged squares.
	 * @return the flags
	 */
	public int getFlags() {
		return flags;
	}
	
	/**
	 * Gets the size value of board (Small, Medium, etc.)
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns true if the game is won, returns false in any other state.
	 * @return true if the game is won
	 */
	public boolean isWon() {
		boolean won = true;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if(viewMatrix[r][c] == HIDDEN && board[r][c] != MINE)
					won = false;
			}
		}
		return won;
	}

}
