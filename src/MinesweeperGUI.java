import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MinesweeperGUI extends Application {
	
	private Stage stage;
	private BoardContainer board;
	private static final Runnable winAction = () -> { // public void run()
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("You Won!");
		a.setHeaderText("You Won!");
		a.setContentText("You found all of the mines and won! Congratulations!");
		a.show();
	};
	private static final Runnable loseAction = () -> { // public void run()
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("You Lost!");
		a.setHeaderText("You Lost!");
		a.setContentText("You hit a mine and lost. Try again.");
		a.show();
	};
	
	
	public void start(Stage arg0) {
		stage = arg0;
		
		BorderPane root = new BorderPane();
		
		root.setTop(new MenuBar(fileMenu(), editMenu(), viewMenu()));
		
		board = new BoardContainer(new Board(Board.MEDIUM), 20, winAction, loseAction, stage);
		
		root.setCenter(board.getBoardView());
		
		Scene scene = new Scene(root);
		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				board.resize(scene.getWidth(), scene.getHeight() - root.getTop().minHeight(-1));
			}
		});
		scene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				board.resize(scene.getWidth(), scene.getHeight() - root.getTop().minHeight(-1));
			}
		});
		stage.setScene(scene);
		
		stage.setTitle("Minesweeper");
		stage.setMinHeight(400);
		stage.setMinWidth(400);
		stage.show();
		stage.centerOnScreen();
	}
	
	private Menu fileMenu() {
		Menu file = new Menu("File");
		
		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction((event) -> { // public void handle(ActionEvent event)
			// TODO: Implement New Game feature.
		});
		newGame.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		
		MenuItem saveGame = new MenuItem("Save Game");
		saveGame.setOnAction((event) -> { // public void handle(ActionEvent event)
			// TODO: Implement Save Game feature.
		});
		saveGame.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		
		MenuItem saveGameAs = new MenuItem("Save Game As...");
		saveGame.setOnAction((event) -> { // public void handle(ActionEvent event)
			// TODO: Implement Save Game As feature.
		});
		saveGameAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		
		MenuItem loadGame = new MenuItem("Load Game");
		loadGame.setOnAction((event) -> { // public void handle(ActionEvent event)
			// TODO: Implement Load Game feature.
		});
		loadGame.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
		
		file.getItems().addAll(newGame, saveGame, saveGameAs, loadGame);
		
		return file;
	}
	
	private Menu editMenu() {
		Menu edit = new Menu("Edit");
		
		MenuItem restart = new MenuItem("Restart Game");
		restart.setOnAction((event) -> {
			board.restart();
		});
		restart.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		
		MenuItem undo = new MenuItem("Undo Move");
		undo.setOnAction((event) -> { // public void handle(ActionEvent event)
			board.undo();
		});
		undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		
		MenuItem redo = new MenuItem("Redo Move");
		redo.setOnAction((event) -> { // public void handle(ActionEvent event)
			board.redo();
		});
		redo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		
		MenuItem preferences = new MenuItem("Preferences");
		preferences.setOnAction((event) -> { // public void handle(ActionEvent event)
			// TODO: Implement Preferences menu.
		});
		preferences.setAccelerator(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.ALT_DOWN));
		
		edit.getItems().addAll(restart, undo, redo, preferences);
		
		return edit;
	}
	
	private Menu viewMenu() {
		Menu view = new Menu("View");
		
		stage.setFullScreenExitHint("Press F12 to exit full-screen mode.");
		stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.F12));
		
		CheckMenuItem fullscreen = new CheckMenuItem("Fullscreen");
		fullscreen.setOnAction((event) -> { // public void handle(ActionEvent event)
			if(fullscreen.isSelected())
				stage.setFullScreen(true);
			else
				stage.setFullScreen(false);
		});
		fullscreen.setAccelerator(new KeyCodeCombination(KeyCode.F12));
		
		view.getItems().addAll(fullscreen);
		
		return view;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
