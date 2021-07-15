import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import java.util.Vector;

/**
 * A building block for creating your own shapes
 * These explicitly support parent-child relationships between nodes
 */

public abstract class Sprite {
    static int spriteID = 0;
    final String localID;
    static Sprite root = null;

    protected Sprite parent = null;
    public Affine matrix = new Affine();
    public Affine rotateMatrix = new Affine();
    public Affine scaleMatrix = new Affine();
    protected Vector<Sprite> children = new Vector<Sprite>();

    public double pivotX = 0;
    public double pivotY = 0;

    // absolute value of pivots
    public double absX = 0;
    public double absY = 0;

    public double angle = 0;
    public double dx = 0;
    public double dy = 0;

    public double sx = 1;
    public double sy = 1;


    public Sprite(String id) {
        localID = id;
    }

    public Sprite(Sprite parent, String id) {
//        this();
        localID = id;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    // maintain hierarchy
    public void addChild(Sprite s) {
        children.add(s);
        s.setParent(this);
    }

    public Sprite getParent() {
        return parent;
    }
    private void setParent(Sprite s) {
        this.parent = s;
    }

    // transformations
    // these will pre-concat to the sprite's affine matrix
    void translate(double dx, double dy) {
        matrix.prependTranslation(dx,dy);
        this.dx += dx;
        this.dy += dy;
        System.out.println("Translated");
    }

    void rotate(double theta) {
        if (localID.equals("2")) {
            if (angle + theta > -50 && angle + theta < 50) {
                angle += theta;
                rotateMatrix.prependRotation(theta, pivotX, pivotY);
            }
        } else if (localID.equals("5") || localID.equals("6")) {
            if (angle + theta > -135 && angle + theta < 135) {
                angle += theta;
                rotateMatrix.prependRotation(theta, pivotX, pivotY);
            }
        } else if (localID.equals("7") || localID.equals("8") || localID.equals("13") || localID.equals("14")) {
            if (angle + theta > -35 && angle + theta < 35) {
                angle += theta;
                rotateMatrix.prependRotation(theta, pivotX, pivotY);
            }
        } else if (localID.equals("9") || localID.equals("10") || localID.equals("11") || localID.equals("12")) {
            if (angle + theta > -90 && angle + theta < 90) {
                angle += theta;
                rotateMatrix.prependRotation(theta, pivotX, pivotY);
            }
        } else {
            angle += theta;
            rotateMatrix.prependRotation(theta, pivotX, pivotY);
        }

        absX = pivotX + root.getFullMatrix().getTx();
        absY = pivotY + root.getFullMatrix().getTy();
    }

    void scale(double sx, double sy) {
        scaleMatrix.prependScale(sx,sy);
        this.sx *= sx;
        this.sy *= sy;
    }

    Affine getFullMatrix() {
        Affine fullMatrix = rotateMatrix.clone();

        fullMatrix.append(matrix.clone());
        if (parent != null) {
            fullMatrix.prepend(parent.getFullMatrix());
        }
        if (!localID.equals("13") && !localID.equals("14")) {
            fullMatrix.append(scaleMatrix.clone());
        }
        return fullMatrix;
    }

    public void setPivots(double x, double y) {
        pivotX = x;
        pivotY = y;

        absX = x + root.getFullMatrix().getTx();
        absY = y + root.getFullMatrix().getTy();
    }

    // hit tests
    // these cannot be handled in the base class, since the actual hit tests are dependent on the type of shape
    protected abstract boolean contains(Point2D p);

    protected boolean contains(double x, double y) {
        return contains(new Point2D(x, y));
    }

    // we can walk the tree from the base class, since we rely on the specific sprites to check containment
    protected Sprite getSpriteHit(double x, double y) {
        // if no match above, recurse through children and return the first hit
        // assumes no overlapping shapes
        for (Sprite sprite : children) {
            Sprite hit = sprite.getSpriteHit(x, y);
            if (hit != null) return hit;
        }

        // check me first...
        if (this.contains(x, y)) {
            return this;
        }

        return null;
    }

    // drawing method
    protected abstract void draw(GraphicsContext gc);

    // debugging
    public String toString() { return "Sprite " + localID; }

}
