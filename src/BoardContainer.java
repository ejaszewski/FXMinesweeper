import java.util.Arrays;
import java.util.Stack;

import javafx.beans.value.ChangeListener;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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
		
		ChangeListener<Number> resizeListener = (observable, oldVal, newVal) -> {
//			if()
			int size = (int)Math.min(boardView.getWidth() / board.getCols(), boardView.getHeight() / board.getRows());
			System.out.println(newVal);
		};
		boardView.widthProperty().addListener(resizeListener);
		boardView.heightProperty().addListener(resizeListener);
		int[][] boardValues = board.getBoard();
		for(int r = 0; r < board.getRows(); r++)
			for(int c = 0; c < board.getCols(); c++) {
				Cell cell = new Cell(boardValues[r][c], r, c, cellSize);
				boardView.add(cell, c, r);
				GridPane.setVgrow(cell, Priority.ALWAYS);
				GridPane.setHgrow(cell, Priority.ALWAYS);
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
		redoBuffer.push(copyArr(undoBuffer.peek()));
		board.setViewMatrix(undoBuffer.pop());
		update();
	}
	
	public void redo() {
		if(redoBuffer.isEmpty())
			return;
		undoBuffer.push(copyArr(redoBuffer.peek()));
		board.setViewMatrix(redoBuffer.pop());
		update();
	}
	
	private int[][] copyArr(int[][] arr) {
		int[][] copy = new int[arr.length][];
		for(int i = 0; i < copy.length; i++)
			copy[i] = Arrays.copyOf(arr[i], arr[i].length);
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
			
			text = new Text("" + value);
			text.setFill(cellTextColor[value + 1]);
			text.setFont(Font.font("Arial", FontWeight.BOLD, (int)(0.75 * size)));
			text.setTextOrigin(VPos.BASELINE);
			rect = new Rectangle(size, size, Color.AZURE);
			button = new Button();
			button.setMinSize(size, size);
			button.setMaxSize(size, size);
			button.setOpacity(0.9);
			button.setOnMouseClicked((mouseEvent) -> {
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
			text.setFont(Font.font("Arial", FontWeight.BOLD, (int)(0.75 * size)));
		}
		
	}
	
}
