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

    protected Sprite parent = null;
    public Affine matrix = new Affine();
    protected Vector<Sprite> children = new Vector<Sprite>();

    public Sprite() {
        localID = String.valueOf(++spriteID);
    }

    public Sprite(Sprite parent) {
        this();
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
        matrix.prependTranslation(dx, dy);
    }

    void rotate(double theta) throws NonInvertibleTransformException {
        Affine fullMatrix = getFullMatrix();
        Affine inverse = fullMatrix.createInverse();

        // move to the origin, rotate and move back
        matrix.prepend(inverse);
        matrix.prependRotation(theta);
        matrix.prepend(fullMatrix);
    }

    void scale(double sx, double sy) throws NonInvertibleTransformException {
        Affine fullMatrix = getFullMatrix();
        Affine inverse = fullMatrix.createInverse();

        // move to the origin, rotate and move back
        matrix.prepend(inverse);
        matrix.prependScale(sx, sy);
        matrix.prepend(fullMatrix);
    }

    Affine getLocalMatrix() { return matrix; }
    Affine getFullMatrix() {
        Affine fullMatrix = getLocalMatrix().clone();
        if (parent != null) {
            fullMatrix.append(parent.getFullMatrix());
        }
        return fullMatrix;
    }

    // hit tests
    // these cannot be handled in the base class, since the actual hit tests are dependend on the type of shape
    protected abstract boolean contains(Point2D p);
    protected boolean contains(double x, double y) {
        return contains(new Point2D(x, y));
    }

    // we can walk the tree from the base class, since we rely on the specific sprites to check containment
    protected Sprite getSpriteHit(double x, double y) {
        // check me first...
        if (this.contains(x, y)) {
            return this;
        }

        // if no match above, recurse through children and return the first hit
        // assumes no overlapping shapes
        for (Sprite sprite : children) {
            Sprite hit = sprite.getSpriteHit(x, y);
            if (hit != null) return hit;
        }

        return null;
    }

    // drawing method
    protected abstract void draw(GraphicsContext gc);

    // debugging
    public String toString() { return "Sprite " + localID; }
}
