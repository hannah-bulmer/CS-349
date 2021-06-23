import java.io.FileInputStream;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.geometry.Point2D;
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
    ArrayList<ArrayList<Enemy>> aliens;
    float left_alien_x = left_edge + 15;
    float alien_x_dist = 53;
    float alien_y = 50;
    float alien_y_dist = 50;

    Text highScore = new Text("High score: 0");
    int highScoreCount = 0;
    Text levelNum;
    int curLevel = 1;

    Player player;

    Group root = new Group();
    Group start = new Group();
    Lives l;

    AnimationTimer timer;
    Scene level;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        stage.setTitle("Space Invaders");

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                handle_animation();
            }
        };
        timer.start();

        level = new Scene(root, screen_width, screen_height);

        setupStart(level);
        level.setRoot(start);

        stage.setScene(level);
        stage.show();
    }

    void handle_animation() {
        if (level.getRoot() != root) return;
        if (Enemy.getEnemyCount() == 0) {
            if (curLevel == 3) {
                l.winState(root,level, Enemy.getEnemiesDestroyed());
                setupChangeLevel(level);
            } else {
                Enemy.reset();
                root = new Group();
                setupLevel(level, curLevel + 1);
                level.setRoot(root);
                highScoreCount += 50;
            }
        }
        player.handle_animation(root);

        ArrayList<PlayerBullet> bullets = player.getBullets();

//        boolean[][] map = Enemy.getMap();
        int x = (int)Math.floor(Math.random()*(rows-1));
        int y = (int)Math.floor(Math.random()*(aliens.get(0).size()-1));

//        while(true) {
//            if (Enemy.getEnemyCount() == 0) break;
//            if (map[x][y]) break;
//            x = (int)Math.floor(Math.random()*(rows-1));
//            y = (int)Math.floor(Math.random()*(aliens.get(0).size()-1));
//        }

        for (int i = 0; i < aliens.size(); i ++) {
            for (int j = 0; j < aliens.get(0).size(); j ++) {
                int size = bullets.size();
                for (int k = 0; k < size; k ++) {
                    ImageView bullet = bullets.get(k).getPlayerBullet();
                    Point2D point = new Point2D(bullet.getX(),bullet.getY());
                    ImageView alien = aliens.get(i).get(j).getEnemy();
                    if (alien.contains(point) && root.getChildren().contains(alien)) {
                        aliens.get(i).get(j).delete(root);
                        Enemy.destroyEnemy(i,j);
                        highScore.setText("High score: " + String.valueOf(Enemy.getEnemiesDestroyed() + highScoreCount));
                        root.getChildren().remove(bullet);
                        bullets = player.removeBullet(k);
                        size = bullets.size();
                    }
                }

                ImageView bullet = aliens.get(i).get(j).bullet.getBullet();
                Point2D point = new Point2D(bullet.getX(),bullet.getY());
                if (player.getPlayer().contains(point) && root.getChildren().contains(bullet)) {
                    root.getChildren().remove(bullet);
                    boolean go = l.loseLife(root, player, level, highScoreCount + Enemy.getEnemiesDestroyed());
                    if (go) setupChangeLevel(level);
                }

                Enemy alien = aliens.get(i).get(j);

                if (alien.getY() > screen_height - 60 && root.getChildren().contains(alien.getEnemy())) {
                    l.gameOver(root, level, highScoreCount + Enemy.getEnemiesDestroyed());
                    setupChangeLevel(level);
                }

                aliens.get(i).get(j).handle_animation(root,left_edge + j * alien_x_dist,
                        right_edge - (cols - j - 1) * alien_x_dist, i == x && j == y);
            }
        }
    }

    void setupLevel(Scene level, int num) {
        // setup Player
        player = new Player(screen_width,screen_height);
        root.getChildren().add(player.getPlayer());

        // Setup aliens
        placeAliens();

        // setup HUD
        l = new Lives();
        l.addToGroup(root);

        level.setFill(Color.BLACK);

        levelNum = new Text("Level: "+ num);
        curLevel = num;
        setTextStyles(highScore, 40);
        setTextStyles(levelNum, 500);

        Enemy.setConstants(num);

        level.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.A || keyEvent.getCode() == KeyCode.LEFT) {
                player.moveLeft();
            }
            if(keyEvent.getCode() == KeyCode.D || keyEvent.getCode() == KeyCode.RIGHT) {
                player.moveRight();
            }
            if(keyEvent.getCode() == KeyCode.SPACE || keyEvent.getCode() == KeyCode.ENTER) {
                player.shootBullets(root);
            }
        });

        level.setOnKeyReleased(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.A || keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.D || keyEvent.getCode() == KeyCode.RIGHT) {
                player.stop();
            }
        });
    }

    void placeAliens() {
        aliens = new ArrayList<>();
        Enemy.setup(rows,cols);
        float x = left_alien_x;
        float y = alien_y;
        for (int i = 0; i < rows; i ++) {
            aliens.add(new ArrayList<>());
            for (int j = 0; j < cols; j ++) {
                int num = i % 3 + 1;
                aliens.get(i).add(new Enemy(num,i,j));
                aliens.get(i).get(j).setCenterX(x);
                aliens.get(i).get(j).setCenterY(y);
                x += alien_x_dist;
            }
            x = left_alien_x;
            y += alien_y_dist;
        }

        for (ArrayList<Enemy> row:aliens) {
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

    void setTextStyles(Text text, int x, int y, int font) {
        text.setX(x);
        text.setY(y);
        text.setFont(Font.font("Avenir", font));
        text.setFill(Color.WHITE);
        root.getChildren().add(text);
    }

    void setupStart(Scene level) {
        level.setFill(Color.BLACK);
        try {
            Image logo = new Image(new FileInputStream("src/resources/images/logo.png"));
            ImageView imageView = new ImageView(logo);
            start.getChildren().add(imageView);
        } catch (FileNotFoundException e) {
            System.exit(0);
        }

        Text [] texts = new Text[7];

        texts[0] = new Text("Instructions");
        texts[1] = new Text("ENTER - Start Game");
        texts[2] = new Text("A or ←, D or → - Move ship left or right");
        texts[3] = new Text("SPACE or ENTER - Fire!");
        texts[4] = new Text("Q - Quit Game");
        texts[5] = new Text("1 or 2 or 3 - Start Game at specific level");
        texts[6] = new Text("Implemented by Hannah Bulmer for CS349, University of Waterloo, S21");

        setTextStyles(texts[0], 400, 300, 30);
        setTextStyles(texts[1], 400, 340, 20);
        setTextStyles(texts[2], 400, 380, 20);
        setTextStyles(texts[3], 400, 420, 20);
        setTextStyles(texts[4], 400, 460, 20);
        setTextStyles(texts[5], 400, 500, 20);
        setTextStyles(texts[6], 400, 550, 10);

        start.getChildren().addAll(texts);

        level.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.DIGIT1) {
                setupLevel(level, 1);
                level.setRoot(root);
            } else if (keyEvent.getCode() == KeyCode.DIGIT2) {
                setupLevel(level, 2);
                level.setRoot(root);
            } else if (keyEvent.getCode() == KeyCode.DIGIT3) {
                setupLevel(level, 3);
                level.setRoot(root);
            }
        });

    }

    void setupChangeLevel(Scene s) {
        highScoreCount = 0;
        timer.stop();
        s.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.DIGIT1) {
                timer.start();
                Enemy.reset();
                root = new Group();
                highScore.setText("High score: 0");
                setupLevel(level, 1);
                s.setRoot(root);
            } else if (keyEvent.getCode() == KeyCode.DIGIT2) {
                timer.start();
                Enemy.reset();
                root = new Group();
                highScore.setText("High score: 0");
                setupLevel(level, 2);
                s.setRoot(root);
            } else if (keyEvent.getCode() == KeyCode.DIGIT3) {
                timer.start();
                Enemy.reset();
                root = new Group();
                highScore.setText("High score: 0");
                setupLevel(level, 3);
                s.setRoot(root);
            } else if (keyEvent.getCode() == KeyCode.Q) {
                System.exit(0);
            } else if (keyEvent.getCode() == KeyCode.I) {
                timer.start();
                Enemy.reset();
                highScore.setText("High score: 0");
                root = new Group();
                setupStart(level);
                level.setRoot(start);
            }
        });
    }
}

