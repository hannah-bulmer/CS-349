import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.util.Duration;

public class Player {
    Image image;
    ImageView imageView;
    int w = 40;
    int h = 30;
    float screenWidth;

    int speed = 10;
    int velocity = 0;
    int MAX_BULLETS = 3;

    MediaPlayer bulletSound;

    ArrayList<PlayerBullet> bullets = new ArrayList<>();

    public Player(float screen_width, float screen_height) {
        try {
            image = new Image(new FileInputStream("src/resources/images/player.png"));
            imageView = new ImageView(image);
        } catch (FileNotFoundException e) {
            System.exit(0);
        }
        imageView.setFitHeight(h);
        imageView.setFitWidth(w);

        float x = screen_width / 2;
        float y = screen_height - 50;

        this.screenWidth = screen_width;
        imageView.setX(x);
        imageView.setY(y);
        Media sound = new Media(new File("src/resources/sounds/shoot.wav").toURI().toString());
        bulletSound = new MediaPlayer(sound);
    }

    public void shootBullets(Group root) {
        if (bullets.size() > MAX_BULLETS - 1) return;
        System.out.println("Play sound");
        bulletSound.seek(Duration.ZERO);
        bulletSound.play();
        PlayerBullet bullet = new PlayerBullet();
        ImageView pb = bullet.getPlayerBullet();
        bullets.add(bullet);

        bullet.setX((float)(imageView.getX() + imageView.getFitWidth()/2 - pb.getFitWidth()/2));
        bullet.setY((float)(imageView.getY()));
        root.getChildren().add(pb);
    }

    public ImageView getPlayer() {
        return imageView;
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

    public void setX(int x) {
        imageView.setX(x);
    }

    public ArrayList<PlayerBullet> getBullets() {
        return bullets;
    }

    public ArrayList<PlayerBullet> removeBullet(int idx) {
        bullets.remove(idx);
        return bullets;
    }

    public void handle_animation(Group root) {
        if (imageView.getX() < 0 && velocity < 0) stop();
        if (imageView.getX() > screenWidth - w && velocity > 0) stop();

        imageView.setX(imageView.getX() + velocity);

        int size = bullets.size();

        for (int i = 0; i < size; i ++) {
            PlayerBullet b = bullets.get(i);
            b.handle_animation(root);
            if (b.getY() < 0) {
                bullets.remove(b);
                size = bullets.size();
            }
        }
    }
}
