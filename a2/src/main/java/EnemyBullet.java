import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class EnemyBullet {
    Image image;
    ImageView imageView;
    int w = 10;
    int h = 20;
    float bulletSpeed = 5;

    public EnemyBullet(int img_num) throws FileNotFoundException {
        image = new Image(new FileInputStream(String.format("src/resources/images/bullet%d.png", img_num)));
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

    public ImageView getBullet() {
        return imageView;
    }

    public void handle_animation(Group root) {
        if (root.getChildren().contains(imageView)) {
            this.setY(this.getY() + bulletSpeed);

            if (imageView.getY() > 600) {
                root.getChildren().remove(imageView);
            }
        }
    }
}
