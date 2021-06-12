import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class SpaceInvaders extends Application {
    float screen_width = 800;
    float screen_height = 600;
    float left_edge = 20;
    float right_edge = screen_width - left_edge;

    int num_balls = 8;
    Circle [] balls = new Circle [num_balls];
    float change = 5;
    float left_ball_x = left_edge + 15;
    float ball_dist_apart = 60;
    float ball_y = 50;

    @Override
    public void start(Stage stage) throws Exception {
        // executed after init() method
        stage.setResizable(false);
        stage.setTitle("Space Invaders");

        float loc = left_ball_x;
        for (int i = 0; i < 8; i ++) {
            balls[i] = new Circle(15);
            balls[i].setCenterX(loc);
            balls[i].setCenterY(ball_y);
            balls[i].setFill(Color.RED);
            loc += ball_dist_apart;
        }

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                handle_animation();
            }
        };

        timer.start();

        Group root = new Group();

        for (Circle ball:balls) {
            root.getChildren().add(ball);
        }

        Label label = new Label("Hello World!!");
        Scene intro = new Scene(new StackPane(label), screen_width, screen_height);
        Scene level1 = new Scene(root, screen_width, screen_height);
        Scene level2 = new Scene(new StackPane(), screen_width, screen_height);
        Scene level3 = new Scene(new StackPane(), screen_width, screen_height);


        stage.setScene(level1);
        stage.show();
    }

    void handle_animation() {
        if (left_ball_x < left_edge + 15) change *= -1;
        if (left_ball_x + (num_balls-1) * ball_dist_apart > right_edge-15) change *= -1;
        left_ball_x += change;
        float tmp = left_ball_x;
        for (Circle ball: balls) {
            System.out.println(tmp);
            ball.setCenterX(tmp);
            tmp += ball_dist_apart;
        }
    }
}

