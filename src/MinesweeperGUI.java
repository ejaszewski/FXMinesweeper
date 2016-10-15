import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MinesweeperGUI extends Application {
	
	private Stage stage;
	private GridPane boardView;
	private Board current;

	@Override
	public void start(Stage arg0) throws Exception {
		
		this.stage = arg0;
		
		VBox root = new VBox();
		
		Menu view = new Menu("View");
		CheckMenuItem fullscreen = new CheckMenuItem("Fullscreen");
		fullscreen.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(fullscreen.isSelected())
					stage.setFullScreen(true);
				else
					stage.setFullScreen(false);
			}
		});
		fullscreen.setAccelerator(new KeyCodeCombination(KeyCode.F12));
		view.getItems().add(fullscreen);
		
		MenuBar bar = new MenuBar(fileMenu(), editMenu(), view);
		
		
		root.getChildren().add(bar);
		
		Scene scene = new Scene(root);
		
		current = new Board();
		createBoardView();
		root.getChildren().add(boardView);
		
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}
	
	private Menu fileMenu() {
		Menu file = new Menu("File");
		
		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		newGame.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		
		MenuItem saveGame = new MenuItem("Save Game");
		saveGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		saveGame.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		
		MenuItem saveGameAs = new MenuItem("Save Game As...");
		saveGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		saveGameAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		
		MenuItem loadGame = new MenuItem("Load Game");
		loadGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Load Minesweeper Game");
				chooser.showOpenDialog(stage);
			}
		});
		loadGame.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
		
		file.getItems().add(newGame);
		file.getItems().add(saveGame);
		file.getItems().add(saveGameAs);
		file.getItems().add(loadGame);
		return file;
	}
	
	public Menu editMenu() {
		Menu edit = new Menu("Edit");
		
		MenuItem restart = new MenuItem("Restart Game");
		restart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		restart.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		
		MenuItem undo = new MenuItem("Undo Move");
		undo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		
		MenuItem redo = new MenuItem("Redo Move");
		redo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		redo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		
		MenuItem preferences = new MenuItem("Preferences");
		preferences.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		preferences.setAccelerator(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.ALT_DOWN));
		
		edit.getItems().add(restart);
		edit.getItems().add(undo);
		edit.getItems().add(redo);
		edit.getItems().add(preferences);
		return edit;
	}
	
	public void createBoardView() {
		boardView = new GridPane();
		int[][] board = current.getBoard();
		int[][] viewM = current.getViewMatrix();
		for(int i = 0; i < current.getRows(); i++) {
			for(int j = 0; j < current.getCols(); j++) {
				boardView.add(new Cell(board[i][j], i, j, 40), i, j);
			}
		}
	}
	
	public void updateBoardView() {
		for(Node node : boardView.getChildren()) {
			Cell cell = (Cell)node;
			cell.update();
		}
	}
	
	public void disableAll() {
		for(Node node : boardView.getChildren()) {
			Cell cell = (Cell)node;
			cell.disable();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	class Cell extends StackPane {
		
		private int value, row, col;
		private boolean disable;
		private Button button;
		
		public Cell(int value, int row, int col, double size) {
			this.value = value;
			this.row = row;
			this.col = col;
			
			this.getChildren().add(new Rectangle(size, size, Color.ALICEBLUE));
			this.getChildren().add(new Text("" + value));
			button = new Button();
			button.setMinSize(size, size);
			button.setMaxSize(size, size);
			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if(event.getButton() == MouseButton.PRIMARY) {
						if(!disable) {
							boolean success = current.reveal(row, col);
							if(!success) {
								Alert a = new Alert(AlertType.INFORMATION);
								a.setTitle("You Lost!");
								a.setHeaderText("You Lost!");
								a.setContentText("You hit a mine and lost. Try again.");
								a.show();
								disableAll();
							} else if(current.isWon()) {
								Alert a = new Alert(AlertType.INFORMATION);
								a.setTitle("You Won!");
								a.setHeaderText("You Won!");
								a.setContentText("You found all of the mines and won! Congratulations!");
								a.show();
								disableAll();
							}
						}
					} else {
						current.flag(row, col);
					}

					updateBoardView();
				}
			});
			this.getChildren().add(button);
		}
		
		public void update() {
			switch(current.getViewMatrix()[row][col]) {
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
		
	}

}
