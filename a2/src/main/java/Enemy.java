import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Enemy {
    Image image;
    ImageView imageView;
    int size = 40;

    int CHANGE = 2;

    /**
     * @param path
     * @param x
     * @param y
     * @throws FileNotFoundException
     */
    public Enemy(String path, float x, float y) throws FileNotFoundException {
        image = new Image(new FileInputStream(path));
        imageView = new ImageView(image);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
    }

    /**
     * @param path
     * @throws FileNotFoundException
     */
    public Enemy(String path) throws FileNotFoundException {
        image = new Image(new FileInputStream(path));
        imageView = new ImageView(image);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
    }

    public ImageView getEnemy() {
        return imageView;
    }

    /**
     * @param x
     */
    public void setCenterX(float x) {
        imageView.setX(x);
    }

    /**
     * @param y:
     */
    public void setCenterY(float y) {
        imageView.setY(y);
    }

    public void handle_animation(float left, float right) {
        float x = (float)(imageView.getX());
        if (x < left) CHANGE *= -1;
        if (x > right) CHANGE *= -1;
        this.setCenterX(x + CHANGE);
    }
}
