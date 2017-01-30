import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class NewGUI extends Application {
	
	private Stage stage;
	private BoardContainer board;
	private static final Runnable winAction = () -> {
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("You Won!");
		a.setHeaderText("You Won!");
		a.setContentText("You found all of the mines and won! Congratulations!");
		a.show();
	};
	private static final Runnable loseAction = () -> {
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("You Won!");
		a.setHeaderText("You Won!");
		a.setContentText("You found all of the mines and won! Congratulations!");
		a.show();
	};
	
	
	public void start(Stage arg0) {
		stage = arg0;
		
		BorderPane root = new BorderPane();
		
		root.setTop(new MenuBar(fileMenu(), editMenu(), viewMenu()));
		
		board = new BoardContainer(new Board(Board.MEDIUM), 20, winAction, loseAction, stage);
		
		root.setCenter(board.getBoardView());
		
		Scene scene = new Scene(root);
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
		newGame.setOnAction((event) -> {
			// TODO: Implement New Game feature.
		});
		newGame.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		
		MenuItem saveGame = new MenuItem("Save Game");
		saveGame.setOnAction((event) -> {
			// TODO: Implement Save Game feature.
		});
		saveGame.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		
		MenuItem saveGameAs = new MenuItem("Save Game As...");
		saveGame.setOnAction((event) -> {
			// TODO: Implement Save Game As feature.
		});
		saveGameAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		
		MenuItem loadGame = new MenuItem("Load Game");
		loadGame.setOnAction((event) -> {
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
			// TODO: Implement Restart Game feature.
		});
		restart.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		
		MenuItem undo = new MenuItem("Undo Move");
		undo.setOnAction((event) -> {
			// TODO: Implement Undo feature.
			board.undo();
		});
		undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		
		MenuItem redo = new MenuItem("Redo Move");
		redo.setOnAction((event) -> {
			// TODO: Implement Redo feature.
			board.redo();
		});
		redo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		
		MenuItem preferences = new MenuItem("Preferences");
		preferences.setOnAction((event) -> {
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
		fullscreen.setOnAction((event) -> {
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
