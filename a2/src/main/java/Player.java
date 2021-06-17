import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {
    Image image;
    ImageView imageView;
    int w = 40;
    int h = 30;

    int speed = 10;

    public Player(float screen_width, float screen_height) throws FileNotFoundException {
        image = new Image(new FileInputStream("src/resources/images/player.png"));
        imageView = new ImageView(image);
        imageView.setFitHeight(h);
        imageView.setFitWidth(w);

        float x = screen_width / 2;
        float y = screen_height - 50;
        imageView.setX(x);
        imageView.setY(y);
    }

    public ImageView getPlayer() {
        return imageView;
    }

    /**
     * @param x
     */
    public void moveLeft() {
        float x = (float)(imageView.getX());
        imageView.setX(x - speed);
    }

    /**
     * @param y:
     */
    public void moveRight() {
        float x = (float)(imageView.getX());
        imageView.setX(x + speed);
    }

}
