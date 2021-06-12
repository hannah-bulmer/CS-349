import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class SpaceInvaders extends Application {
    float screen_width = 800;
    float screen_height = 600;
    float left_edge = 20;
    float right_edge = screen_width - 3 * left_edge;

    int num_balls = 10;
    int rows = 5;
    Enemy [][] balls = new Enemy [rows][num_balls];
    float left_ball_x = left_edge + 15;
    float ball_x_dist = 60;
    float ball_y = 25;
    float ball_y_dist = 50;

    @Override
    public void start(Stage stage) throws Exception {
        // executed after init() method
        stage.setResizable(false);
        stage.setTitle("Space Invaders");

        float x = left_ball_x;
        float y = ball_y;
        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < num_balls; j ++) {
                balls[i][j] = new Enemy("src/resources/images/enemy1.png");
                balls[i][j].setCenterX(x);
                balls[i][j].setCenterY(y);
                x += ball_x_dist;
            }
            x = left_ball_x;
            y += ball_y_dist;
        }

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                handle_animation();
            }
        };

        timer.start();

        Group root = new Group();

        for (Enemy[] row:balls) {
            for (Enemy e: row) {
                root.getChildren().add(e.getEnemy());
            }
        }

        Label label = new Label("Hello World!!");
        Scene intro = new Scene(new StackPane(label), screen_width, screen_height);
        Scene level1 = new Scene(root, screen_width, screen_height);
        Scene level2 = new Scene(new StackPane(), screen_width, screen_height);
        Scene level3 = new Scene(new StackPane(), screen_width, screen_height);


        level1.setFill(Color.BLACK);
        stage.setScene(level1);
        stage.show();
    }

    void handle_animation() {
        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < num_balls; j ++) {
                balls[i][j].handle_animation(left_edge + j * ball_x_dist,
                        right_edge - (num_balls - j - 1) * ball_x_dist);
            }
        }
    }
}

