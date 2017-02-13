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
	
}
