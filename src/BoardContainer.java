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
	
	public boolean reveal(int row, int col) {
		redoBuffer.clear();
		undoBuffer.push(copyArr(board.getViewMatrix()));
		boolean result = board.reveal(row, col);
		update();
		return result;
	}
	
	public void flag(int row, int col) {
		redoBuffer.clear();
		undoBuffer.push(copyArr(board.getViewMatrix()));
		board.flag(row, col);
		update();
	}
	
	public void undo() {
		if(undoBuffer.isEmpty())
			return;
		redoBuffer.push(board.getViewMatrix());
		board.setViewMatrix(undoBuffer.pop());
		update();
	}
	
	public void redo() {
		if(redoBuffer.isEmpty())
			return;
		undoBuffer.push(board.getViewMatrix());
		board.setViewMatrix(redoBuffer.pop());
		update();
	}
	
	public void restart() {
		undoBuffer.clear();
		redoBuffer.clear();
		board.setViewMatrix(new int[board.getRows()][board.getCols()]);
		update();
	}
	
	public void resize(double width, double height) {
		int size = (int)Math.min(width / board.getCols(), height / board.getRows());
		for(Node n : boardView.getChildren()) {
			((Cell)n).resize(size);
		}
	}
	
	public boolean save() {
	    return saveToFile(saveFile);
	}
	
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
	
	private void parseSaveLine(String line, int rows, int cols, int value, int[][] arr) {
	    for(int i = 0; i < line.length(); i++) {
            int coord = line.charAt(i);
            arr[coord / cols][coord % rows] = value;
        }
	}
	
	private int[][] copyArr(int[][] arr) {
		int[][] copy = new int[arr.length][];
		for(int i = 0; i < copy.length; i++)
			copy[i] = arr[i].clone();
		return copy;
	}
	
	private void update() {
		for(Node n : boardView.getChildren()) {
			((Cell)n).update();
		}
	}
	
	private void disableAll() {
		for(Node n : boardView.getChildren()) {
			((Cell)n).disable();
		}
	}
	
	public GridPane getBoardView() {
		return boardView;
	}
	
	class Cell extends StackPane {
		
		private int row, col;
		private boolean disable;
		private Button button;
		private Rectangle rect;
		private Text text;
		
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
		
		public void disable() {
			button.setDisable(true);
		}
		
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
	
	public Board getBoard() {
		return board;
	}
}
