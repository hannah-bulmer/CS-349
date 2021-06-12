import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SpaceInvaders extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // executed after init() method
        Label label = new Label("Hello JavaFX");
        Scene scene = new Scene(new StackPane(label), 320, 240);

        stage.setTitle("HelloFX");
        stage.setScene(scene);
        stage.show();
        System.out.println("Start");
    }
}

