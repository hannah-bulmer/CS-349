import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class BodyPart extends Sprite {
    public double x, y, w, h;
    Image image;
    ImageView imageView;

    // Creates a rectangle based at the origin with the specified width and height
    public BodyPart(String id, double width, double height) {
        super(id);
        this.initialize(width, height);
    }

    public BodyPart(String id, double width, double height, String fileName) {
        super(id);
        this.initialize(width, height, fileName);
    }

    // Creates a rectangle based at the origin with the specified parent, width, and height
    public BodyPart(Sprite parent, String id, double width, double height) {
        super(parent, id);
        this.initialize(width, height);
    }

    public BodyPart(Sprite parent, String id, double width, double height, String fileName) {
        super(parent, id);
        this.initialize(width, height, fileName);
    }

    // Initialize the rect at the origin
    private void initialize(double width, double height) {
        try{
            image = new Image(new FileInputStream("src/resources/images/circle.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        x = 0;
        y = 0;
        w = width;
        h = height;
    }

    private void initialize(double width, double height, String fileName) {
        try{
            image = new Image(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        x = 0;
        y = 0;
        w = width;
        h = height;
    }

    // Draw on the supplied canvas
    protected void draw(GraphicsContext gc) {
        // save the current graphics context so that we can restore later
        Affine oldMatrix = gc.getTransform();

        // make sure we have the correct transformations for this shape
        gc.setTransform(getFullMatrix());
        gc.setStroke(Color.DARKRED);
        gc.drawImage(image, x, y, w,h);
//        gc.setStroke(Color.BLUE);
//        gc.strokeRect(x,y,w,h);

        // draw children
        for (Sprite child : children) {
            child.draw(gc);
        }

        // set back to original value since we're done with this branch of the scene graph
        gc.setTransform(oldMatrix);
    }

    // Check if the point is contained by this shape
    // This cannot be abstract, since it relies on knowledge of the
    // specific type of shape for the hit test.
    @Override
    protected boolean contains(javafx.geometry.Point2D p) {
        try {
            // Use inverted matrix to move the mouse click so that it's
            // relative to the shape model at the origin.
            Point2D pointAtOrigin = getFullMatrix().createInverse().transform(p);

            // Perform the hit test relative to the shape model's
            // untranslated coordinates at the origin
            return new Rectangle(x, y, w, h).contains(pointAtOrigin);

        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
        return false;
    }
}

