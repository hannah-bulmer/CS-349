import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Enemy {
    Image image;
    ImageView imageView;
    int w = 40;
    int h = 30;
    static int rows;
    static int cols;

    static boolean[][] map;
    static float speed = 0.5F;
    static float speedInc = 0.05F;
    static int prob = 8000; // 1 in 10 of shooting
    int direction = 1;
    int changeY = 20;

    EnemyBullet bullet;

    static int count = 0;
    static int enemiesDestroyed = 0;

    static MediaPlayer killedSound;
    MediaPlayer shootSound;

    /**
     * @throws FileNotFoundException
     */
    public Enemy(int img_num, int x, int y) {
        try {
            image = new Image(new FileInputStream(String.format("src/resources/images/enemy%d.png", img_num)));
            imageView = new ImageView(image);
            imageView.setFitHeight(h);
            imageView.setFitWidth(w);
            bullet = new EnemyBullet(img_num);
            Media sound = new Media(new File("src/resources/sounds/invaderkilled.wav").toURI().toString());
            killedSound = new MediaPlayer(sound);

            Media sound2 = new Media(new File(String.format("src/resources/sounds/fastinvader%d.wav", img_num)).toURI().toString());
            shootSound = new MediaPlayer(sound2);
        } catch (FileNotFoundException e) {
            System.exit(0);
        }

        count += 1;
        map[x][y] = true;
    }

    public static void reset() {
        count = 0;
        enemiesDestroyed = 0;
        speed = 0.5F;
        speedInc = 0.05F;
        prob = 8000;
    }

    public EnemyBullet getBullet() {
        return bullet;
    }

    public static void setConstants(int level) {
        if (level == 1) return;
        if (level == 2) {
            speed = 1F;
            speedInc = 0.08F;
            prob = 6000;
        }
        if (level == 3) {
            speed = 1.5F;
            speedInc = 0.1F;
            prob = 5000;
        }
    }

    public ImageView getEnemy() {
        return imageView;
    }

    public static void setup(int rows, int cols) {
        Enemy.rows = rows;
        Enemy.cols = cols;
        map = new boolean[rows][cols];
    }

    public void delete(Group root) {
//        root.getChildren().remove(this.bullet.getBullet());
        root.getChildren().remove(imageView);
    }

    public static void destroyEnemy(int x, int y) {
        killedSound.seek(Duration.ZERO);
        killedSound.play();
        count -= 1;
        enemiesDestroyed += 1;
        speed += speedInc;
        map[x][y] = false;
    }

    public static boolean[][] getMap() {
        return map;
    }

    public int getY() {
        return (int)imageView.getY();
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
    public void setCenterX(double x) {
        imageView.setX(x);
    }

    /**
     * @param y:
     */
    public void setCenterY(float y) {
        imageView.setY(y);
    }

    public void handle_animation(Group root, float left, float right, boolean shouldShoot) {
        float x = (float)(imageView.getX());
        float y = (float)(imageView.getY());
        int val = (int)Math.floor(Math.random()*(prob-1));
        if ((x < left || x > right) && shouldShoot) shoot(root);
        else if (val == 7) shoot(root);
        if (x < left) {
            // random enemy fire
            direction *= -1;
            this.setCenterY(y + changeY);
        } else if (x > right) {
            // random enemy fire
            direction *= -1;
            this.setCenterY(y + changeY);
        }
        double velocity = (double)(speed*direction);
        this.setCenterX(x + velocity);

        bullet.handle_animation(root);
    }

    public void shoot(Group root) {
//        System.out.println("Shooting");
        shootSound.seek(Duration.ZERO);
        if (!root.getChildren().contains(imageView)) return;
        ImageView pb = bullet.getBullet();
        if (!root.getChildren().contains(pb)) {
            bullet.setX((float)(imageView.getX() + imageView.getFitWidth()/2 - pb.getFitWidth()/2));
            bullet.setY((float)(imageView.getY() + 20));
            root.getChildren().add(pb);
            shootSound.play();
        }
    }
}
