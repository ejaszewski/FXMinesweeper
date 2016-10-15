import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Preferences extends Stage {
	
	public Preferences(Window owner) {
		super();
		this.initOwner(owner);
		this.setTitle("Preferences");
		
		VBox root = new VBox();
		
		Button cancel = new Button("Cancel");
		cancel.setMinWidth(80);
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		
		Button ok = new Button("OK");
		ok.setMinWidth(80);
		ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});

		HBox buttons = new HBox();
		buttons.setSpacing(8);
		buttons.getChildren().add(cancel);
		buttons.getChildren().add(ok);
		
		root.getChildren().add(buttons);
		
		Scene scene = new Scene(root);
		
		this.setScene(scene);
		this.show();
		this.centerOnScreen();
	}
	
}
