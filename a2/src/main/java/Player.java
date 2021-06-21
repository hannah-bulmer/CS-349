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
    int velocity = 0;

    PlayerBullet bullet;

    public Player(float screen_width, float screen_height) throws FileNotFoundException {
        image = new Image(new FileInputStream("src/resources/images/player.png"));
        imageView = new ImageView(image);
        imageView.setFitHeight(h);
        imageView.setFitWidth(w);

        float x = screen_width / 2;
        float y = screen_height - 50;
        imageView.setX(x);
        imageView.setY(y);
        bullet = new PlayerBullet();
    }

    public void shootBullet(Group root) {
        ImageView pb = bullet.getPlayerBullet();
        if (!root.getChildren().contains(pb)) {
            bullet.setX((float)(imageView.getX() + imageView.getFitWidth()/2 - pb.getFitWidth()/2));
            bullet.setY((float)(imageView.getY()));
            root.getChildren().add(pb);
        }
    }

    public ImageView getPlayer() {
        return imageView;
    }

    public PlayerBullet getBullet() {
        return bullet;
    }

    public void moveLeft() {
        velocity = speed * -1;
    }

    public void moveRight() {
        velocity = speed;
    }

    public void stop() {
        velocity = 0;
    }

    public void handle_animation() {
        imageView.setX(imageView.getX() + velocity);
    }
}
