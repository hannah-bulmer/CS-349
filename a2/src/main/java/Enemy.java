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
    int w = 40;
    int h = 30;

    static float speed = 0.5F;
    float direction = 1;
    int changeY = 20;

    static int count = 0;
    static int enemiesDestroyed = 0;

    /**
     * @param path
     * @param x
     * @param y
     * @throws FileNotFoundException
     */
    public Enemy(int img_num, float x, float y) throws FileNotFoundException {
        image = new Image(new FileInputStream(String.format("src/resources/images/enemy%d.png", img_num)));
        imageView = new ImageView(image);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitHeight(h);
        imageView.setFitWidth(w);
        count += 1;
    }

    /**
     * @param path
     * @throws FileNotFoundException
     */
    public Enemy(int img_num) throws FileNotFoundException {
        image = new Image(new FileInputStream(String.format("src/resources/images/enemy%d.png", img_num)));
        imageView = new ImageView(image);
        imageView.setFitHeight(h);
        imageView.setFitWidth(w);
        count += 1;
    }

    public ImageView getEnemy() {
        return imageView;
    }

    public static void destroyEnemy() {
        count -= 1;
        enemiesDestroyed += 1;
        speed += 0.05F;
    }

    public static int getEnemyCount() {
        return count;
    }

    public static int getEnemiesDestroyed() {
        return enemiesDestroyed;
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
        float y = (float)(imageView.getY());
        if (x < left) {
            direction *= -1;
            this.setCenterY(y + changeY);
        }
        if (x > right) {
            direction *= -1;
            this.setCenterY(y + changeY);
        }
        this.setCenterX(x + speed*direction);
    }
}
