import java.io.File;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MinesweeperGUI extends Application {
	
	private Stage stage;
	private Stage newGameStage;
	private Scene scene;
	private BorderPane root;
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
		
		root = new BorderPane();
		
		root.setTop(new MenuBar(fileMenu(), editMenu(), viewMenu()));
		
		board = new BoardContainer(new Board(Board.MEDIUM), 20, winAction, loseAction, stage);
		
		root.setCenter(board.getBoardView());
		
		scene = new Scene(root);
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
		
		newGameStage();
		
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
			newGameStage.show();
		});
		newGame.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		
		MenuItem quickGame = new MenuItem("Quick Game");
		quickGame.setOnAction((event) -> {
			board = new BoardContainer(new Board(board.getBoard().getSize()), 20, winAction, loseAction, stage);
			board.resize(scene.getWidth(), scene.getHeight() - root.getTop().minHeight(-1));
            root.setCenter(board.getBoardView());
		});
		quickGame.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
		
		MenuItem saveGame = new MenuItem("Save Game");
		saveGame.setOnAction((event) -> { // public void handle(ActionEvent event)
			if(!board.save()) {
			    FileChooser chooser = new FileChooser();
	            chooser.setTitle("Save Minesweeper Game As...");
	            chooser.getExtensionFilters().add(new ExtensionFilter("FX Minesweeer Saves", "*.fxms"));
	            File saveAs = chooser.showSaveDialog(stage);
	            if(saveAs == null)
	                return;
	            if(!saveAs.getName().endsWith(".fxms"))
	                saveAs = new File(saveAs.getAbsolutePath() + ".fxms");
	            board.saveToFile(saveAs);
			}
		});
		saveGame.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		
		MenuItem saveGameAs = new MenuItem("Save Game As...");
		saveGameAs.setOnAction((event) -> { // public void handle(ActionEvent event)
		    FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Minesweeper Game As...");
            chooser.getExtensionFilters().add(new ExtensionFilter("FX Minesweeer Saves", "*.fxms"));
            File saveAs = chooser.showSaveDialog(stage);
            if(saveAs == null)
                return;
            if(!saveAs.getName().endsWith(".fxms"))
                saveAs = new File(saveAs.getAbsolutePath() + ".fxms");
            board.saveToFile(saveAs);
		});
		saveGameAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		
		MenuItem loadGame = new MenuItem("Load Game");
		loadGame.setOnAction((event) -> { // public void handle(ActionEvent event)
		    FileChooser chooser = new FileChooser();
            chooser.setTitle("Load Minesweeper Game");
            chooser.getExtensionFilters().add(new ExtensionFilter("FX Minesweeer Saves", "*.fxms"));
            File load = chooser.showOpenDialog(stage);
            if(load == null)
                return;
            board = new BoardContainer(load, 20, winAction, loseAction, stage);
            root.setCenter(board.getBoardView());
            board.resize(scene.getWidth(), scene.getHeight() - root.getTop().minHeight(-1));
		});
		loadGame.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
		
		file.getItems().addAll(newGame, quickGame, saveGame, saveGameAs, loadGame);
		
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
	
	private void newGameStage() {
	    newGameStage = new Stage();
        newGameStage.setTitle("New Game");
        
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(12);
        gridPane.setAlignment(Pos.CENTER);
        
        Label newGameLabel = new Label("New Game");     
        newGameLabel.setStyle("-fx-font-size: 16pt;");
        gridPane.add(newGameLabel, 0, 0, 2, 1);
        GridPane.setHalignment(newGameLabel, HPos.CENTER);
        
        ComboBox<String> comboBox = new ComboBox<String>();
        comboBox.getItems().addAll("Small", "Medium", "Large", "Humongous");
        comboBox.setValue("Small");
        gridPane.add(comboBox, 0, 1, 2, 1);
        GridPane.setHalignment(comboBox, HPos.CENTER);
        
        Button startButton = new Button("Start");
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch(comboBox.getSelectionModel().getSelectedItem()) {
                case "Small":
                    board = new BoardContainer(new Board(Board.SMALL), 20, winAction, loseAction, stage);
                    break;
                    
                case "Medium":
                    board = new BoardContainer(new Board(Board.MEDIUM), 20, winAction, loseAction, stage);
                    break;
                    
                case "Large":
                    board = new BoardContainer(new Board(Board.LARGE), 20, winAction, loseAction, stage);
                    break;
                    
                case "Humongous":
                    board = new BoardContainer(new Board(Board.HUMONGOUS), 20, winAction, loseAction, stage);
                    break;
                }
                
                board.resize(scene.getWidth(), scene.getHeight() - root.getTop().minHeight(-1));
                
                root.setCenter(board.getBoardView());
                newGameStage.hide();
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newGameStage.hide();
            }
        });
        
        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.getChildren().addAll(startButton, cancelButton);
        gridPane.add(buttons, 0, 2, 2, 1);
        gridPane.setPadding(new Insets(10, 70, 20, 70));
        
        Scene newGameScene = new Scene(gridPane);
        newGameStage.setScene(newGameScene);
        newGameStage.centerOnScreen();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
