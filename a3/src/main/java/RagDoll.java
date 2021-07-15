import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class RagDoll extends Application {
	final int screen_width = 500;
	final int screen_height = 500;
	double previous_x, previous_y;
	double start_angle;
	Sprite selectedSprite;
	Canvas canvas;

	double f = 2;

	final KeyCombination ctrlR = new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN);

	double headSize = 40 * f * 1.5;
	double bodyWidth = 40 * f;
	double shoulders = bodyWidth * 0.1; // smaller number == closer to neck
	double leftArm = 0 * f; // x position from body
	double rightArm = 38 * f; // x position from body

	Sprite root;
	BodyPart body;
	BodyPart head;

	BodyPart leftUpperArm;
	BodyPart rightUpperArm;

	BodyPart leftLowerArm;
	BodyPart rightLowerArm;

	BodyPart leftHand;
	BodyPart rightHand;

	BodyPart leftUpperLeg;
	BodyPart rightUpperLeg;

	BodyPart leftLowerLeg;
	BodyPart rightLowerLeg;

	BodyPart leftFoot;
	BodyPart rightFoot;

	@Override
	public void start(Stage stage) {
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> Platform.exit());
		// My code

		Menu fileMenu = new Menu("File");
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().add(fileMenu);
		SeparatorMenuItem sep1 = new SeparatorMenuItem();
		SeparatorMenuItem sep2 = new SeparatorMenuItem();

		MenuItem reset = new MenuItem("Reset (Cmd + R)");
		MenuItem open = new MenuItem("Open");
		MenuItem quit = new MenuItem("Quit (Cmd + Q)");
		MenuItem save = new MenuItem("Save");
		fileMenu.getItems().add(reset);
		fileMenu.getItems().add(1, sep1);
		fileMenu.getItems().add(quit);
		fileMenu.getItems().add(3, sep2);
		fileMenu.getItems().add(open);
		fileMenu.getItems().add(save);

		VBox layout = new VBox(menuBar);

		// setup a canvas to use as a drawing surface
		canvas = new Canvas(screen_width, screen_height);
		layout.getChildren().add(canvas);
		Scene scene = new Scene(layout, screen_width, screen_height);

		// create hierarchy of sprites
		root = createSprites();

		// add listeners
		// click selects the shape under the cursor
		// we have sprites do it since they track their own locations
		canvas.setOnMousePressed(mouseEvent -> {
			Sprite hit = root.getSpriteHit(mouseEvent.getX(), mouseEvent.getY());
			if (hit != null) {
				selectedSprite = hit;
				previous_x = mouseEvent.getX();
				previous_y = mouseEvent.getY();
			}
		});

		// un-selects any selected shape
		canvas.setOnMouseReleased( mouseEvent -> {
			selectedSprite = null;
		});

		// dragged translates the shape based on change in mouse position
		// since shapes are defined relative to one another, they will follow their parent
		canvas.setOnMouseDragged(mouseEvent -> {
			if (selectedSprite != null) {
				if (selectedSprite == body) {
					double dx = mouseEvent.getX() - previous_x;
					double dy = mouseEvent.getY() - previous_y;
					selectedSprite.translate(dx, dy);

				} else {
					double angle = Math.atan2(mouseEvent.getY() - selectedSprite.absY, mouseEvent.getX() - selectedSprite.absX) -
							Math.atan2(previous_y - selectedSprite.absY, previous_x - selectedSprite.absX);

					selectedSprite.rotate(Math.toDegrees(angle));
				}

				// handle scaling on legs
				if (selectedSprite == leftUpperLeg || selectedSprite == leftLowerLeg || selectedSprite == rightUpperLeg || selectedSprite == rightLowerLeg) {
					double distA = Math.pow(mouseEvent.getY() - selectedSprite.absY,2) + Math.pow(mouseEvent.getX() - selectedSprite.absX, 2);
					double distB = Math.pow(previous_y - selectedSprite.absY,2) + Math.pow(previous_x - selectedSprite.absX,2);

					if (distA > distB) {
						selectedSprite.scale(1, 1.005);
					} else {
						selectedSprite.scale(1, 0.995);
					}

				}

				// draw tree in new position
				draw(canvas, root);

				// save coordinates for next event
				previous_x = mouseEvent.getX();
				previous_y = mouseEvent.getY();
				start_angle = Math.atan2(mouseEvent.getY() - selectedSprite.pivotY, mouseEvent.getX() - selectedSprite.pivotX);
			}
		});

		// draw the sprites on the canvas
		draw(canvas, root);

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (ctrlR.match(e)) {
					root = createSprites();
					draw(canvas, root);
					e.consume();
				}
			}
		});

		reset.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				root = createSprites();
				draw(canvas, root);
				e.consume();
			}
		});

		quit.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				System.exit(0);
			}
		});

		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onSave(stage);
			}
		});

		open.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					onOpen(stage);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		try {
			BackgroundImage bi = new BackgroundImage(new Image(new FileInputStream("src/resources/images/background.jpg")), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					BackgroundSize.DEFAULT);
			layout.setBackground(new Background(bi));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// show the scene including the canvas
		stage.setScene(scene);
		stage.show();
	}

	private void draw(Canvas canvas, Sprite root) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		root.draw(gc);
	}

	private void onOpen(Stage stage) throws IOException {
		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showOpenDialog(stage);

		FileInputStream fstream = new FileInputStream(selectedFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		// reset character first
		root = createSprites();
		draw(canvas, root);

		String dx = br.readLine();
		String dy = br.readLine();
		System.out.println(dx + " " + dy);
		body.translate(Double.parseDouble(dx), Double.parseDouble(dy));

		String line;

		Sprite[] loop = new Sprite [] {head,leftUpperArm,rightUpperArm,leftLowerArm,rightLowerArm,leftHand,rightHand,leftUpperLeg,rightUpperLeg,leftLowerLeg,rightLowerLeg,leftFoot,rightFoot};
		for (Sprite s: loop) {
			line = br.readLine();
			s.rotate(Double.parseDouble(line));
		}

		dy = br.readLine();
		leftUpperLeg.scale(1, Double.parseDouble(dy));
		dy = br.readLine();
		rightUpperLeg.scale(1, Double.parseDouble(dy));
		dy = br.readLine();
		leftLowerLeg.scale(1, Double.parseDouble(dy));
		dy = br.readLine();
		rightLowerLeg.scale(1, Double.parseDouble(dy));

		draw(canvas,root);

		fstream.close();
	}

	private void onSave(Stage stage) {
		String content = "";
		content += ((body.dx - 180) + System.lineSeparator());
		content += ((body.dy - 100) + System.lineSeparator());

		content += (head.angle + System.lineSeparator());
		content += ((leftUpperArm.angle - 45) + System.lineSeparator());
		content += ((rightUpperArm.angle + 45) + System.lineSeparator());
		content += (leftLowerArm.angle + System.lineSeparator());
		content += (rightLowerArm.angle + System.lineSeparator());
		content += (leftHand.angle + System.lineSeparator());
		content += (rightHand.angle + System.lineSeparator());
		content += (leftUpperLeg.angle + System.lineSeparator());
		content += (rightUpperLeg.angle + System.lineSeparator());
		content += (leftLowerLeg.angle + System.lineSeparator());
		content += (rightLowerLeg.angle + System.lineSeparator());
		content += (leftFoot.angle + System.lineSeparator());
		content += (rightFoot.angle + System.lineSeparator());

		content += (leftUpperLeg.sy + System.lineSeparator());
		content += (rightUpperLeg.sy + System.lineSeparator());
		content += (leftLowerLeg.sy + System.lineSeparator());
		content += (rightLowerLeg.sy + System.lineSeparator());

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter ext = new FileChooser.ExtensionFilter("Text file", "*.doll");
		fileChooser.getExtensionFilters().add(ext);

		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			saveTextToFile(content, file);
		}
	}

	private void saveTextToFile(String content, File file) {
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.close();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(content);
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private Sprite createSprites() {
		int bodyX = 180;
		int bodyY = 100;

		body = new BodyPart("1",bodyWidth * 1.2, 2 * bodyWidth, "src/resources/images/body.png");
		head = new BodyPart(body,"2", headSize, headSize, "src/resources/images/hannah.png");
		Sprite.root = body;

		leftUpperArm = new BodyPart(body,"3", 13 * f, bodyWidth, "src/resources/images/upper_left_arm.png");
		rightUpperArm = new BodyPart(body,"4", 13 * f, bodyWidth, "src/resources/images/upper_right_arm.png");

		leftLowerArm = new BodyPart(leftUpperArm,"5", 10 * f, bodyWidth,"src/resources/images/lower_left_arm.png");
		rightLowerArm = new BodyPart(rightUpperArm,"6", 10 * f, bodyWidth, "src/resources/images/lower_right_arm.png");

		leftHand = new BodyPart(leftLowerArm, "7",10 * f, 10 * f,"src/resources/images/hand.png");
		rightHand = new BodyPart(rightLowerArm, "8",10 * f, 10 * f,"src/resources/images/hand.png");

		leftUpperLeg = new BodyPart(body, "9",25 * f, bodyWidth,"src/resources/images/upper_left_leg.png");
		rightUpperLeg = new BodyPart(body, "10",25 * f, bodyWidth,"src/resources/images/upper_right_leg.png");

		leftLowerLeg = new BodyPart(leftUpperLeg, "11",15 * f, bodyWidth, "src/resources/images/lower_left_leg.png");
		rightLowerLeg = new BodyPart(rightUpperLeg, "12",15 * f, bodyWidth, "src/resources/images/lower_right_leg.png");

		leftFoot = new BodyPart(leftLowerLeg, "13",22 * f, 16 * f, "src/resources/images/right_foot.png");
		rightFoot = new BodyPart(rightLowerLeg, "14",22 * f, 16 * f, "src/resources/images/right_foot.png");

		// translations
		body.translate(bodyX, bodyY);
		head.translate(-5 * f, - (headSize * 6/8));

		leftUpperArm.translate(leftArm, shoulders + 6*f);
		rightUpperArm.translate(rightArm, shoulders);

		leftLowerArm.translate(0, bodyWidth - 10 * f);
		rightLowerArm.translate(0, bodyWidth - 10 * f);

		leftHand.translate(0, bodyWidth - 2.5 * f);
		rightHand.translate(0, bodyWidth - 2.5 * f);

		leftUpperLeg.translate(0, 80 * f - 5 * f);
		rightUpperLeg.translate(25 * f, 80 * f - 5 * f);

		leftLowerLeg.translate(0, 40 * f - 3.5 * f);
		rightLowerLeg.translate(5 * f, 40 * f - 3.5 * f);

		leftFoot.translate(0, 40 * f - 5 * f);
		rightFoot.translate(0, 40 * f - 5 * f);

		// pivots
		head.setPivots(head.w/2 -5 * f, head.h - headSize);

		leftUpperArm.setPivots(leftUpperArm.x + leftUpperArm.w/2 + leftArm + 2 *f,leftUpperArm.y + shoulders + 8*f);
		rightUpperArm.setPivots(rightUpperArm.x + rightUpperArm.w/2 + rightArm + 2 * f, rightUpperArm.y + shoulders + 2 * f);

		leftLowerArm.setPivots(leftLowerArm.x + leftLowerArm.w / 2, leftLowerArm.y + 35 * f);
		rightLowerArm.setPivots(rightLowerArm.x + rightLowerArm.w / 2, rightLowerArm.y + 35 * f);

		rightHand.setPivots(rightHand.x + rightHand.w / 2 , rightHand.y + bodyWidth - 2.5 * f);
		leftHand.setPivots(leftHand.x + leftHand.w / 2 , leftHand.y + bodyWidth - 2.5 * f);

		leftUpperLeg.setPivots(leftUpperLeg.x + leftUpperLeg.w/2, leftUpperLeg.y + 75 * f);
		rightUpperLeg.setPivots(rightUpperLeg.x + rightUpperLeg.w/2 + 25 * f, rightUpperLeg.y + 75 * f);

		leftLowerLeg.setPivots(leftLowerLeg.x + leftLowerLeg.w/2, leftLowerLeg.y + 40 * f);
		rightLowerLeg.setPivots(rightLowerLeg.x + rightLowerLeg.w/2 + 5 * f, rightLowerLeg.y + 40 * f);

		leftFoot.setPivots(leftFoot.x + leftFoot.w/2 - 5 * f, leftFoot.y + 40 * f);
		rightFoot.setPivots(rightFoot.x + rightFoot.w/2 - 5 * f, rightFoot.y + 40 * f);

		// set rotations (or default)
		leftUpperArm.rotate(45);
//		leftLowerArm.rotate(-45);
		rightUpperArm.rotate(-45);
//		rightLowerArm.rotate(45);

		// return root of the tree
		return body;
	}
}
