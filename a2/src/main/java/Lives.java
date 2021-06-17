import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Lives {
    Image image;
    ImageView [] imageViews  = new ImageView[3];

    static int count = 3;

    public Lives() throws FileNotFoundException {
        image = new Image(new FileInputStream("src/resources/images/player.png"));

        for (int i = 0; i < 3; i ++) {
            imageViews[i] = new ImageView(image);
            imageViews[i].setX(660 + i*40);
            imageViews[i].setY(10);
            imageViews[i].setFitHeight(25);
            imageViews[i].setFitWidth(30);
        }
    }

    public static void loseLife() {
        count -= 1;
    }

    public void addToGroup(Group g) {
        Text text = new Text("Lives: ");
        text.setX(600);
        text.setY(30);
        text.setFont(Font.font("Avenir", 20));
        text.setFill(Color.WHITE);
        g.getChildren().add(text);
        for (int i = 0; i < count; i ++) {
            g.getChildren().add(imageViews[i]);
        }
    }
}
