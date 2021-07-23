package net.codebot.pdfviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class PDFimage extends ImageView {

    final String LOGNAME = "pdf_image";

    // drawing path
    Path path = null;
    ArrayList<Path> drawPaths = new ArrayList();
    ArrayList<Path> highlightPaths = new ArrayList();

    // image to display
    Bitmap bitmap;
    Paint paint = new Paint(Color.BLUE);

    // is highlight
    boolean isHighlight = false;

    // constructor
    public PDFimage(Context context) {
        super(context);
    }

    // capture touch events (down/move/up) to create a path
    // and use that to create a stroke that we can draw
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(LOGNAME, "Action down");
                path = new Path();
                path.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(LOGNAME, "Action move");
                path.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                Log.d(LOGNAME, "Action up");
                if (isHighlight) {
                    highlightPaths.add(path);
                } else {
                    drawPaths.add(path);
                }

                break;
        }
        return true;
    }

    public void turnOnHighlight() {
        isHighlight = true;
    }

    public void turnOffHighlight() {
        isHighlight = false;
    }

    // set image as background
    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    // set brush characteristics
    // e.g. color, thickness, alpha
    public void setBrush(Paint paint, boolean highlighting) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        if (highlighting) {
            paint.setStrokeWidth(30);
            paint.setARGB(70,255,255,0);
        } else {
            paint.setStrokeWidth(10);
            paint.setColor(Color.BLUE);
        }
        this.paint = paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
        if (bitmap != null) {
            this.setImageBitmap(bitmap);
        }
        // draw lines over it
        setBrush(paint, false);
        for (Path path : drawPaths) {
            canvas.drawPath(path, paint);
        }

        setBrush(paint, true);
        for (Path path : highlightPaths) {
            canvas.drawPath(path, paint);
        }

        super.onDraw(canvas);
    }
}
