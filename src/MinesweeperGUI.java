import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooserBuilder;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MinesweeperGUI extends Application {
	
	private Stage stage;
	private Board current;

	@Override
	public void start(Stage arg0) throws Exception {
		
		this.stage = arg0;
		
		VBox root = new VBox();
		
		Menu edit = new Menu("Edit");
		edit.getItems().add(new MenuItem("Restart Game"));
		edit.getItems().add(new MenuItem("Undo Move"));
		edit.getItems().add(new MenuItem("Redo Move"));
		edit.getItems().add(new MenuItem("Settings"));
		
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
		
		MenuBar bar = new MenuBar(getFileMenu(), edit, view);
		
		
		root.getChildren().add(bar);
		
		Scene scene = new Scene(root);
		
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}
	
	public void createNewGame() {
		
	}
	
	private Menu getFileMenu() {
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
	
	public static void main(String[] args) {
		launch(args);
	}

}
