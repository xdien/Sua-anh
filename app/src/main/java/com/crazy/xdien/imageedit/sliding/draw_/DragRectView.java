package com.crazy.xdien.imageedit.sliding.draw_;

/**
 * Created by xdien on 9/29/14.
 */

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Matrix;
        import android.graphics.Paint;
        import android.graphics.Rect;
        import android.text.TextPaint;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.view.MotionEvent;
        import android.view.View;
        import android.R;

        import com.crazy.xdien.imageedit.sliding.CrazyActivity;
        import com.crazy.xdien.imageedit.sliding.sliding.CutFragment;

        import org.opencv.core.Point;


public class DragRectView extends View {

    private Paint mRectPaint;
    //khai bao cho do phan giai bit map
    private int mStartX = 0;
    private int mStartY = 0;
    private int mEndX = 0;
    private int mEndY = 0;
    private Bitmap bitmap1;
    private boolean mDrawRect = false;
    private TextPaint mTextPaint = null;
    public Point tam,tstart;
    Matrix matrix;

    private OnUpCallback mCallback = null;

    public interface OnUpCallback {
        void onRectFinished(Rect rect);
    }

    public DragRectView(final Context context) {
        super(context);
        init();
    }

    public DragRectView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragRectView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Sets callback for up
     *
     * @param callback {@link OnUpCallback}
     */
    public void setOnUpCallback(OnUpCallback callback) {
        mCallback = callback;
    }

    /**
     * Inits internal data
     */

    private void init() {
        mRectPaint = new Paint();
        mRectPaint.setColor(getContext().getResources().getColor(R.color.holo_green_light));
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(5); // TODO: should take from resources

        mTextPaint = new TextPaint();
        mTextPaint.setColor(getContext().getResources().getColor(R.color.holo_green_light));
        mTextPaint.setTextSize(20);
        tam = new Point(0,0);
        tstart = new Point(0,0);
        matrix = new Matrix();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        int action = event.getAction();
        // TODO: be aware of multi-touches
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDrawRect = false;

                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                tstart = CrazyActivity.getCoordinate(mStartX,mStartY).clone();
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                tam = CrazyActivity.getCoordinate(x,y);
                if(tam.y >= 0 && tam.y <= CrazyActivity.thumbnail.getHeight()) {
                    if(tstart.y >=0) {
                        if (!mDrawRect || Math.abs(x - mEndX) > 5 || Math.abs(y - mEndY) > 5) {
                            mEndX = x;
                            mEndY = y;
                            invalidate();
                        }
                        mDrawRect = true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mCallback != null) {
                    mCallback.onRectFinished(new Rect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
                            Math.max(mEndX, mStartX), Math.max(mEndY, mStartX)));
                }

                /*CutFragment.diemDau = CrazyActivity.getCoordinate(mStartX,mStartY).clone();
                CutFragment.diemCuoi = CrazyActivity.getCoordinate(mEndX,mEndY).clone();
                //lay diem dau va cuoi trong an
                CutFragment.width = (int) Math.abs(CutFragment.diemDau.x - CutFragment.diemCuoi.x);
                CutFragment.height = (int)Math.abs(CutFragment.diemDau.y - CutFragment.diemCuoi.y);*/

                invalidate();
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawRect) {
            canvas.drawRect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
                    Math.max(mEndX, mStartX), Math.max(mEndY, mStartY), mRectPaint);
        }
    }
}

