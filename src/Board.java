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
	 * @param row
	 * @param col
	 * @return
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

	// TODO: Flag method. HIDDEN->FLAGGED->QMARK->Hidden
	public void flag(int row, int col) {
		switch(viewMatrix[row][col]) {
		case HIDDEN: viewMatrix[row][col] = FLAGGED; flags++; break;
		case FLAGGED: viewMatrix[row][col] = QMARK; flags--; break;
		case QMARK: viewMatrix[row][col] = HIDDEN; break;
		}
	}
	
	/**
	 * This print method has two behaviors based on showAll. If showAll is true,
	 * it will print the board, ignoring the viewMatrix (all mines shown),
	 * otherwise, it prints the board as it would be displayed (hiding, showing,
	 * flags, question marks).
	 * 
	 * @param showAll whether or not to show everything
	 */
	public void printBoard(boolean showAll) {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (viewMatrix[r][c] == SHOWN) {
					if (board[r][c] < 0)
						System.out.print("M");
					else if (board[r][c] == 0)
						System.out.print(" ");
					else
						System.out.print(board[r][c]);
				} else
					System.out.print("#");
			}
			System.out.println();
		}
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

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}
	
	public boolean isVictory() {
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
