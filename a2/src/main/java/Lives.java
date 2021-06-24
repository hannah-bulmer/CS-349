import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.util.Duration;

public class Lives {
    Image image;
    ImageView [] imageViews  = new ImageView[3];

    int count = 3;

    MediaPlayer loseSound;

    public Lives() {
        try {
            image = new Image(new FileInputStream("src/resources/images/player.png"));
            Media sound = new Media(new File("src/resources/sounds/explosion.wav").toURI().toString());
            loseSound = new MediaPlayer(sound);
        } catch (FileNotFoundException e) {
            System.exit(0);
        }

        for (int i = 0; i < 3; i ++) {
            imageViews[i] = new ImageView(image);
            imageViews[i].setX(660 + i*40);
            imageViews[i].setY(10);
            imageViews[i].setFitHeight(25);
            imageViews[i].setFitWidth(30);
        }
    }

    public void gameOver(Group root, Scene s, int highscore) {
        setUpNewGame("GAME OVER", root, s, highscore);
    }

    public boolean loseLife(Group root, Player player, Scene s, int highscore) {
        loseSound.seek(Duration.ZERO);
        loseSound.play();
        if (count == 0) {
            gameOver(root, s, highscore);
            return true;
        }
        root.getChildren().remove(imageViews[count-1]);
        int loc = (int)Math.floor(Math.random()*(800-100) + 50);
        player.setX(loc);
        count -= 1;
        return false;
    }

    public void winState(Group root, Scene s, int highscore) {
        setUpNewGame("YOU WIN", root, s, highscore);
    }

    public void setUpNewGame(String t, Group root, Scene s, int highscore) {
        VBox p = new VBox();
        Text text = new Text(t);
        p.setBackground(new Background(new BackgroundFill(Color.WHITE,
                new CornerRadii(10, false),
                new Insets(200,200,200,200))));

        styleText(text,50);
        p.setAlignment(Pos.CENTER);
        p.getChildren().add(text);

        Text score = new Text("Final score: " + highscore);
        styleText(score, 20);
        p.getChildren().add(score);

        Text [] instr = new Text[4];
        instr[0] = new Text("ENTER - start new game");
        instr[1] = new Text("I - back to instructions");
        instr[2] = new Text("Q - Quit game");
        instr[3] = new Text("1 / 2 / 3 - restart at specific level");

        for (Text i: instr) styleText(i, 20);

        StackPane pane = new StackPane();
        pane.getChildren().add(root);
        pane.getChildren().add(p);

        p.getChildren().addAll(instr);
//        p.getChildren().add(root);

        s.setRoot(pane);
    }

    public void styleText(Text text, int size) {
        text.setFont(Font.font("Avenir", size));
        text.setFill(Color.BLACK);
    }

    public void addToGroup(Group g) {
        count = 3;
        Text text = new Text("Lives: ");
        text.setX(600);
        text.setY(30);
        text.setFont(Font.font("Avenir", 20));
        text.setFill(Color.WHITE);
        g.getChildren().add(text);
        for (int i = 0; i < count; i ++) {
            g.getChildren().add(imageViews[i]);
        }
    }
}
