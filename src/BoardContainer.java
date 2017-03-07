import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A class that serves as an intermediary between the Board class
 * and the GUI. It contains an internal class which defines a Cell in the
 * GridPane view of the board.
 * 
 * @author Ethan Jaszewski
 */
public class BoardContainer {
	
	private static final Color[] cellTextColor = {
			Color.ORANGERED, Color.AZURE, Color.DARKGRAY.darker(), Color.DARKRED.darker(), Color.DARKSALMON,
			Color.CORAL, Color.FIREBRICK, Color.FIREBRICK, Color.FIREBRICK	
		};
	
	private Board board;
	private Stack<int[][]> redoBuffer, undoBuffer;
	private GridPane boardView;
	private Runnable winAction, loseAction;
	private File saveFile;
	
	/**
	 * Creates a new BoardContainer using the specified board.
	 * @param board board to use
	 * @param cellSize size of the cells
     * @param winAction action to run on a victory
     * @param loseAction action to run on a loss
     * @param stage main JavaFX stage
	 */
	public BoardContainer(Board board, int cellSize, Runnable winAction, Runnable loseAction, Stage stage) {
		this.board = board;
		this.undoBuffer = new Stack<int[][]>();
		this.redoBuffer = new Stack<int[][]>();
		this.winAction  = winAction;
		this.loseAction = loseAction;
		boardView = new GridPane();
		boardView.setAlignment(Pos.CENTER);
		
		int[][] boardValues = board.getBoard();
		for(int r = 0; r < board.getRows(); r++)
			for(int c = 0; c < board.getCols(); c++) {
				Cell cell = new Cell(boardValues[r][c], r, c, cellSize);
				boardView.add(cell, c, r);
			}
	}
	
	/**
	 * Creates a new BoardContainer, loading the board from a file.
	 * @param loadFile file to load from
	 * @param cellSize size of the cells
	 * @param winAction action to run on a victory
	 * @param loseAction action to run on a loss
	 * @param stage main JavaFX stage
	 */
	public BoardContainer(File loadFile, int cellSize, Runnable winAction, Runnable loseAction, Stage stage) {
	    loadFrom(loadFile);
	    this.undoBuffer = new Stack<int[][]>();
        this.redoBuffer = new Stack<int[][]>();
        this.winAction  = winAction;
        this.loseAction = loseAction;
        boardView = new GridPane();
        boardView.setAlignment(Pos.CENTER);
        
        board.printBoard();
        
        int[][] boardValues = board.getBoard();
        for(int r = 0; r < board.getRows(); r++)
            for(int c = 0; c < board.getCols(); c++) {
                Cell cell = new Cell(boardValues[r][c], r, c, cellSize);
                boardView.add(cell, c, r);
            }
	}
	
	/**
     * Recursively reveals the cell in the board at the given row and column.
     * calls {@link Board#reveal(int, int) reveal(row, col)} in the internal Board.
     * @param row row of cell to reveal
     * @param col col of cell to reveal
     */
	public boolean reveal(int row, int col) {
		redoBuffer.clear();
		undoBuffer.push(copyArr(board.getViewMatrix()));
		boolean result = board.reveal(row, col);
		update();
		return result;
	}
	
	/**
	 * Flags the cell in the board at the given row and column.
	 * calls {@link Board#flag(int, int) flag(row, col)} in the internal Board.
	 * @param row row of cell to flag
	 * @param col col of cell to flag
	 */
	public void flag(int row, int col) {
		redoBuffer.clear();
		undoBuffer.push(copyArr(board.getViewMatrix()));
		board.flag(row, col);
		update();
	}
	
	/**
	 * Undoes the most recent action.
	 */
	public void undo() {
		if(undoBuffer.isEmpty())
			return;
		redoBuffer.push(board.getViewMatrix());
		board.setViewMatrix(undoBuffer.pop());
		update();
	}
	
	/**
	 * Redoes an undone action.
	 */
	public void redo() {
		if(redoBuffer.isEmpty())
			return;
		undoBuffer.push(board.getViewMatrix());
		board.setViewMatrix(redoBuffer.pop());
		update();
	}
	
	/**
	 * Restarts the game by reseting the view matrix.
	 */
	public void restart() {
		undoBuffer.clear();
		redoBuffer.clear();
		board.setViewMatrix(new int[board.getRows()][board.getCols()]);
		update();
	}
	
	/**
	 * Resizes the board view to the specified width and height, in pixels.
	 * @param width new width of board view in px
	 * @param height new height of board view in px
	 */
	public void resize(double width, double height) {
		int size = (int)Math.min(width / board.getCols(), height / board.getRows());
		for(Node n : boardView.getChildren()) {
			((Cell)n).resize(size);
		}
	}
	
	/**
	 * Saves the game to the current save file, if possible.
	 * @return false if the save could not be created, true otherwise
	 */
	public boolean save() {
	    return saveToFile(saveFile);
	}
	
	/**
	 * Save the current board to a FX Minesweeper save game file.
	 * @param saveFile file to save to
	 * @return false if the save could not be created, true otherwise
	 */
	public boolean saveToFile(File saveFile) {
	    if(saveFile == null)
	        return false;
	    
	    if(saveFile.exists())
	        saveFile.delete();
	    try {
            saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
	    
	    try(BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
	        int rows = board.getRows(), cols = board.getCols();
	        
	        writer.write("FX Minesweeper Save Game");
	        writer.newLine();
	        writer.write(rows);
	        writer.write(cols);
	        writer.newLine();
	        
	        String mines = "", shown = "", flags = "", qmark = "";
	        
	        for(int i = 0; i < rows * cols; i++) {
	            if(board.getBoard()[i / cols][i % rows] == Board.MINE)
	                mines += (char)i;
	            switch(board.getViewMatrix()[i / cols][i % rows]) {
	            case Board.SHOWN:
	                shown += (char)i;
	                break;
	            case Board.FLAGGED:
                    flags += (char)i;
                    break;
	            case Board.QMARK:
                    qmark += (char)i;
                    break;
	            }
	        }
	        
	        writer.write(mines);
	        writer.newLine();
	        writer.write(shown);
            writer.newLine();
            writer.write(flags);
            writer.newLine();
            writer.write(qmark);
            
	    } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
	    
	    return true;
	}
	
	/**
	 * Loads a FX Minesweeper save game from the specified file.
	 * @param loadFile file to load from
	 * @return false if file can't be loaded, true otherwise
	 */
	public boolean loadFrom(File loadFile) {
	    try(BufferedReader reader = new BufferedReader(new FileReader(loadFile))) {
	        if(!reader.readLine().equals("FX Minesweeper Save Game")) {
	            System.err.println("Invalid FX Minesweeper Save Game: Incorrect file header.");
	            return false;
	        }
	        String size = reader.readLine();
	        String mines = reader.readLine();
	        String shown = reader.readLine();
	        String flags = reader.readLine();
	        String qmark = reader.readLine();
	        
	        if(mines == null || shown == null || flags == null || qmark == null) {
	            System.err.println("Invalid FX Minesweeper Save Game: One or more missing lines.");
                return false;
	        }
	        
	        int rows = size.charAt(0), cols = size.charAt(1);
	        
	        int[][] boardArr = new int[rows][cols];
	        parseSaveLine(mines, rows, cols, Board.MINE, boardArr);
	        
	        int[][] viewMatrix = new int[rows][cols];
	        parseSaveLine(shown, rows, cols, Board.SHOWN, viewMatrix);
            parseSaveLine(flags, rows, cols, Board.FLAGGED, viewMatrix);
            parseSaveLine(qmark, rows, cols, Board.QMARK, viewMatrix);
            
            board = new Board(boardArr, viewMatrix, mines.length());
	        
	        saveFile = loadFile;
	        
	        System.out.println("FX Minesweeper game loaded from " + loadFile.getAbsolutePath());
	    } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
	    return true;
	}
	
	/**
	 * Parses a line in the save file, copying the appropriate values into the specified array.
	 * @param line line to parse
	 * @param rows number of rows in the array
	 * @param cols number of cols in the array
	 * @param value value to copy into the array
	 * @param arr array to copy into
	 */
	private void parseSaveLine(String line, int rows, int cols, int value, int[][] arr) {
	    for(int i = 0; i < line.length(); i++) {
            int coord = line.charAt(i);
            arr[coord / cols][coord % rows] = value;
        }
	}
	
	/**
	 * Returns a copy of a 2D array.
	 * @param arr array to copy
	 * @return copy of the array
	 */
	private int[][] copyArr(int[][] arr) {
		int[][] copy = new int[arr.length][];
		for(int i = 0; i < copy.length; i++)
			copy[i] = arr[i].clone();
		return copy;
	}
	
	/**
	 * Updates all Cells in the board view.
	 */
	private void update() {
		for(Node n : boardView.getChildren()) {
			((Cell)n).update();
		}
	}
	
	/**
	 * Disables all Cells in the board view.
	 */
	private void disableAll() {
		for(Node n : boardView.getChildren()) {
			((Cell)n).disable();
		}
	}
	
	/**
	 * Returns the board view GridPane
	 * @return the board view
	 */
	public GridPane getBoardView() {
		return boardView;
	}
	
	/**
	 * A class that defines the cells used for the board view.
	 * Each Cell is a StackPane containing a Rectangle, a Button, and Text object.
	 * This class interacts with the Board using the methods in the BoardContainer class.
	 * 
	 * @author Ethan Jaszewski
	 */
	class Cell extends StackPane {
		
		private int row, col;
		private boolean disable;
		private Button button;
		private Rectangle rect;
		private Text text;
		
		/**
		 * Creates a new Cell of the given size.
		 * @param value mine value of the Cell
		 * @param row row of the Cell
		 * @param col col of the Cell
		 * @param size size of the Cell in px
		 */
		public Cell(int value, int row, int col, double size) {
			this.row = row;
			this.col = col;
			
			Font font = Font.font("Arial", FontWeight.BOLD, (int)(0.75 * size));
			
			text = new Text("" + value);
			text.setFill(cellTextColor[value + 1]);
			text.setFont(font);
			text.setTextOrigin(VPos.BASELINE);
			
			rect = new Rectangle(size, size, Color.AZURE);
			
			button = new Button();
			button.setMinSize(size, size);
			button.setMaxSize(size, size);
			button.setPadding(new Insets(0));
			button.setFont(font);
			button.setOpacity(0.8);
			button.setOnMouseClicked((mouseEvent) -> { // public void handle(MouseEvent mouseEvent)
				if(mouseEvent.getButton() == MouseButton.PRIMARY) {
					if(!disable) {
						boolean success = reveal(row, col);
						if(!success) {
							loseAction.run();
							disableAll();
						} else if(board.isWon()) {
							winAction.run();
							disableAll();
						}
					}
				} else {
					flag(row, col);
				}
			});
			
			this.getChildren().addAll(rect, text, button);
		}
		
		/**
		 * Refreshes the view status of the Cell (Shown, Hidden, etc).
		 */
		public void update() {
			switch(board.getViewMatrix()[row][col]) {
			case Board.HIDDEN: 
				button.setText("");
				disable = false;
				button.setVisible(true);
				break;
			case Board.SHOWN:
				button.setVisible(false);
				break;
			case Board.FLAGGED:
				button.setText("F");
				disable = true;
				button.setVisible(true);
				break;
			case Board.QMARK:
				button.setText("?");
				disable = true;
				button.setVisible(true);
				break;
			}
		}
		
		/**
		 * Disables the Cell.
		 */
		public void disable() {
			button.setDisable(true);
		}
		
		/**
		 * Sets the new size of the Cell. Resizes all elements.
		 * @param size new size of the Cell in px
		 */
		public void resize(int size) {
			button.setMinSize(size, size);
			button.setMaxSize(size, size);
			rect.setWidth(size);
			rect.setHeight(size);
			
			Font font = Font.font("Arial", FontWeight.BOLD, (int)(0.75 * size));
			text.setFont(font);
			button.setFont(font);
		}
		
	}
	
	/**
	 * Gets the board
	 * @return the board
	 */
	public Board getBoard() {
		return board;
	}
}
