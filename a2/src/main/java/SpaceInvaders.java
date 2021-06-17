import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.input.KeyCode;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import java.io.FileNotFoundException;

public class SpaceInvaders extends Application {
    float screen_width = 800;
    float screen_height = 600;
    float left_edge = 20;
    float right_edge = screen_width - 3 * left_edge;

    int cols = 10;
    int rows = 5;
    Enemy [][] aliens = new Enemy [rows][cols];
    float left_alien_x = left_edge + 15;
    float alien_x_dist = 53;
    float alien_y = 50;
    float alien_y_dist = 50;

    Text highScore = new Text("High score: 0");
    Text levelNum = new Text("Level: 1");

    PlayerBullet playerBullet;
    Player player;

    Group root = new Group();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        stage.setTitle("Space Invaders");

        // setup Player
        player = new Player(screen_width,screen_height);
        playerBullet = new PlayerBullet();
        root.getChildren().add(player.getPlayer());

        // Setup aliens
        placeAliens();

        // setup HUD
        setTextStyles(highScore, 40);
        setTextStyles(levelNum, 500);
        Lives l = new Lives();
        l.addToGroup(root);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                handle_animation();
            }
        };
        timer.start();

        Label label = new Label("Hello World!!");
        Scene intro = new Scene(new StackPane(label), screen_width, screen_height);
        Scene level = new Scene(root, screen_width, screen_height);



        setupLevel(level);

        stage.setScene(level);
        stage.show();
    }

    void handle_animation() {
        ImageView pb = playerBullet.getPlayerBullet();
        if (root.getChildren().contains(pb)) {
            playerBullet.handle_animation();

            if (playerBullet.getY() < 0) {
                root.getChildren().remove(pb);
                System.out.println("Removed bullet");
            }
        }
        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < cols; j ++) {
                if (root.getChildren().contains(pb)) {
                    Point2D point = new Point2D(playerBullet.getX(), playerBullet.getY());
                    if (aliens[i][j].getEnemy().contains(point) && root.getChildren().contains(aliens[i][j].getEnemy())) {
                        root.getChildren().remove(aliens[i][j].getEnemy());
                        Enemy.destroyEnemy();
                        highScore.setText("High score: " + String.valueOf(Enemy.getEnemiesDestroyed()));
                        root.getChildren().remove(pb);
                    }
                }
                aliens[i][j].handle_animation(left_edge + j * alien_x_dist,
                        right_edge - (cols - j - 1) * alien_x_dist);
            }
        }
    }

    void setupLevel(Scene level) {
        level.setFill(Color.BLACK);

        level.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.A || keyEvent.getCode() == KeyCode.LEFT) {
                player.moveLeft();
            }
            if(keyEvent.getCode() == KeyCode.D || keyEvent.getCode() == KeyCode.RIGHT) {
                player.moveRight();
            }
            if(keyEvent.getCode() == KeyCode.SPACE || keyEvent.getCode() == KeyCode.ENTER) {
                // create bullet that moves
                System.out.println("New bullet");
                if (!root.getChildren().contains(playerBullet.getPlayerBullet())) {
                    playerBullet.setX((float)(player.getPlayer().getX() + player.getPlayer().getFitWidth()/2 - playerBullet.getPlayerBullet().getFitWidth()/2));
                    playerBullet.setY((float)(player.getPlayer().getY()));
                    root.getChildren().add(playerBullet.getPlayerBullet());
                }
            }
        });
    }

    void placeAliens() throws FileNotFoundException {
        float x = left_alien_x;
        float y = alien_y;
        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < cols; j ++) {
                int num = i % 3 + 1;
                aliens[i][j] = new Enemy(num);
                aliens[i][j].setCenterX(x);
                aliens[i][j].setCenterY(y);
                x += alien_x_dist;
            }
            x = left_alien_x;
            y += alien_y_dist;
        }

        for (Enemy[] row:aliens) {
            for (Enemy e: row) {
                root.getChildren().add(e.getEnemy());
            }
        }
    }

    void setTextStyles(Text text, int x) {
        text.setX(x);
        text.setY(30);
        text.setFont(Font.font("Avenir", 20));
        text.setFill(Color.WHITE);
        root.getChildren().add(text);
    }
}

