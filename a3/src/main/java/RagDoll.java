import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

public class RagDoll extends Application {
	final int screen_width = 500;
	final int screen_height = 500;
	double previous_x, previous_y;
	Sprite selectedSprite;

	enum OPERATION {TRANSLATE, SCALE_UP, SCALE_DOWN, ROTATE}
	OPERATION operation = OPERATION.TRANSLATE;

	@Override
	public void start(Stage stage) {
		// setup toggle buttons along the top
		ToggleGroup buttonGroup = new ToggleGroup();
		ToggleButton translateButton = new ToggleButton("Translate");
		translateButton.setOnAction( event -> { operation = OPERATION.TRANSLATE; System.out.println("TRANSLATE"); } );
		translateButton.setToggleGroup(buttonGroup);

		ToggleButton scaleUpButton = new ToggleButton("ScaleUp");
		scaleUpButton.setOnAction( event -> { operation = OPERATION.SCALE_UP; System.out.println("SCALE UP"); } );
		scaleUpButton.setToggleGroup(buttonGroup);

		ToggleButton scaleDownButton = new ToggleButton("ScaleDown");
		scaleDownButton.setOnAction( event -> { operation = OPERATION.SCALE_DOWN; System.out.println("SCALE DOWN"); } );
		scaleDownButton.setToggleGroup(buttonGroup);

		ToggleButton rotateButton = new ToggleButton("Rotate");
		rotateButton.setOnAction( event -> { operation = OPERATION.ROTATE; System.out.println("ROTATE"); } );
		rotateButton.setToggleGroup(buttonGroup);

		// setup a canvas to use as a drawing surface
		Canvas canvas = new Canvas(screen_width, screen_height);
		Scene scene = new Scene(new VBox(new HBox(translateButton, scaleUpButton, scaleDownButton, rotateButton), canvas), screen_width, screen_height);

		// create hierarchy of sprites
		Sprite root = createSprites();

		// add listeners
		// click selects the shape under the cursor
		// we have sprites do it since they track their own locations
		canvas.setOnMousePressed(mouseEvent -> {
			Sprite hit = root.getSpriteHit(mouseEvent.getX(), mouseEvent.getY());
			if (hit != null) {
				selectedSprite = hit;
				System.out.println("Selected " + selectedSprite.toString());
				previous_x = mouseEvent.getX();
				previous_y = mouseEvent.getY();
			}
		});

		// un-selects any selected shape
		canvas.setOnMouseReleased( mouseEvent -> {
			selectedSprite = null;
			System.out.println("Unselected");
		});

		// dragged translates the shape based on change in mouse position
		// since shapes are defined relative to one another, they will follow their parent
		canvas.setOnMouseDragged(mouseEvent -> {
			if (selectedSprite != null) {
				switch(operation) {
					case TRANSLATE:
						// translate shape to follow the mouse cursor
						double dx = mouseEvent.getX() - previous_x;
						double dy = mouseEvent.getY() - previous_y;
						selectedSprite.translate(dx, dy);
						System.out.println(".. moved "
								+ selectedSprite.toString()
								+ " from (" + previous_x + "," + previous_y + ")"
								+ " to (" + mouseEvent.getX() + "," + mouseEvent.getY() + ")"
								+ " -- dx: " + dx + ", dy: " + dy);
						break;
					case SCALE_UP:
						try {
							selectedSprite.scale(1.01, 1.01);
						} catch (NonInvertibleTransformException e) {
							e.printStackTrace();
						}
						break;
					case SCALE_DOWN:
						try {
							selectedSprite.scale(0.99, 0.99);
						} catch (NonInvertibleTransformException e) {
							e.printStackTrace();
						}
						break;
					case ROTATE:
						double distance = Math.sqrt(Math.pow(mouseEvent.getX() - previous_x, 2) + Math.pow(mouseEvent.getY() - previous_y, 2));
						double theta = Math.atan(distance);
						try {
							selectedSprite.rotate(theta);
						} catch (NonInvertibleTransformException e) {
							e.printStackTrace();
						}
						break;
				}

				// draw tree in new position
				draw(canvas, root);

				// save coordinates for next event
				previous_x = mouseEvent.getX();
				previous_y = mouseEvent.getY();
			}
		});

		// draw the sprites on the canvas
		draw(canvas, root);

		// show the scene including the canvas
		stage.setScene(scene);
		stage.show();
	}

	private void draw(Canvas canvas, Sprite root) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		root.draw(gc);
	}
	
	private Sprite createSprites() {
		// create a bunch of different sprites at the origin
		Sprite sprite1 = new RectangleSprite(80, 50);
		Sprite sprite2 = new RectangleSprite(50, 40);
		Sprite sprite3 = new RectangleSprite(70, 30);

		 // build scene graph aka tree from them
		sprite1.addChild(sprite2);
		sprite2.addChild(sprite3);

		// translate them to a starting position
		// this also places them beside one another
		sprite1.translate(10, 20);
		sprite2.translate(80, 5);
		sprite3.translate(50, 5);

		// return root of the tree
		return sprite1;
	}
}
