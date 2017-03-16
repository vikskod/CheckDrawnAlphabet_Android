package np.com.vikashparajuli.checkdrawnalphabet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viks on 12/6/16.
 */

public class DrawingImageView extends ImageView {

    private Bitmap bitmap;
    private Paint mPaint;
    private boolean canDraw = false;
    private boolean drawAgain = true;
    private float drawingArea;
    private float drawnArea;

    private List<Point> blackPoints;
    private List<Point> dotPoints;

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;

    public DrawingImageView(Context c, Bitmap bitmap) {
        super(c);
        context = c;
        this.bitmap = bitmap;

        dotedPart(bitmap);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(25);

        drawingArea = percentInnerPart(bitmap);
        Log.e("% ===", "" + drawingArea);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(8f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if ((dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();

                DrawFinishListener callback = (DrawFinishListener) context;
                callback.onDrawStart();
                break;
            case MotionEvent.ACTION_MOVE:
                check((int) event.getX(), (int) event.getY());
                if (drawAgain)
                    if (canDraw) {
                        touchMove(x, y);
                    } else {
                        touchUp();
                        touchStart(x, y);
                    }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();

                if (drawAgain) {
                    areaBlack(mBitmap);
                    checkResult();
                }

                break;
        }
        return true;
    }

    private void checkResult() {
        int count = 0;
        for (Point blackPoint : blackPoints) {
            if (dotPoints.contains(blackPoint)) {
                count++;
            }
        }
        Log.e("Result ===>>", count + "");
        DrawFinishListener mCallback = (DrawFinishListener) context;
        if (dotPoints.size() == count) {

            //compromise 3% area
            if (drawnArea - 3 < drawingArea) {
                canDraw = false;
                drawAgain = false;
                mCallback.onDrawFinish();
            } else
                mCallback.onDrawStop();
        } else
            mCallback.onDrawStop();
    }

    private float percentInnerPart(Bitmap bm) {
        final int width = bm.getWidth();
        final int height = bm.getHeight();

        int myColor = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = bitmap.getPixel(x, y);
                if (Color.red(pixel) == 201 &&
                        Color.red(pixel) == Color.blue(pixel) &&
                        Color.red(pixel) == Color.green(pixel)) {
                    myColor++;
                }
            }
        }
        return ((float) myColor * 100) / (width * height);
    }

    private void dotedPart(Bitmap bm) {
        final int width = bm.getWidth();
        final int height = bm.getHeight();

        dotPoints = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = bm.getPixel(x, y);

                if (Color.red(pixel) == 240 &&
                        Color.red(pixel) == Color.blue(pixel) &&
                        Color.red(pixel) == Color.green(pixel)) {
                    dotPoints.add(new Point(x, y));
                }
            }
        }
    }

    private void check(int x, int y) {
        if (bitmap != null && x >= 0 && x < bitmap.getWidth() && y >= 0 && y < bitmap.getHeight()) {
            int pixel = bitmap.getPixel(x, y);
            if (Color.red(pixel) == 201 && Color.red(pixel) == Color.blue(pixel) && Color.red(pixel) == Color.green(pixel)) {
                Log.e("Touch ===", "You're in");
                canDraw = true;
            } else {
                Log.e("Touch ===", "You're out");
                canDraw = false;
            }
        }
    }

    private void areaBlack(Bitmap bm) {
        final int width = bm.getWidth();
        final int height = bm.getHeight();

        int myColor = 0;
        blackPoints = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bm.getPixel(x, y) == Color.BLACK) {
                    blackPoints.add(new Point(x, y));
                    myColor++;
                }
            }
        }
        drawnArea = ((float) myColor * 100) / (width * height);
    }

    public interface DrawFinishListener {
        void onDrawFinish();

        void onDrawStop();

        void onDrawStart();
    }
}
