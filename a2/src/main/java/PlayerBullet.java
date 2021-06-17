import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlayerBullet {
    Image image;
    ImageView imageView;
    int w = 5;
    int h = 10;
    float bulletSpeed = 15;

    public PlayerBullet() throws FileNotFoundException {
        image = new Image(new FileInputStream("src/resources/images/player_bullet.png"));
        imageView = new ImageView(image);
        imageView.setFitHeight(h);
        imageView.setFitWidth(w);
    }

    public void setX(float x) {
        imageView.setX(x);
    }

    public void setY(float y) {
        imageView.setY(y);
    }

    public float getX() {
        return (float)(imageView.getX());
    }

    public float getY() {
        return (float)(imageView.getY());
    }

    public ImageView getPlayerBullet() {
        return imageView;
    }

    public void handle_animation() {
        this.setY(this.getY() - bulletSpeed);
    }
}
