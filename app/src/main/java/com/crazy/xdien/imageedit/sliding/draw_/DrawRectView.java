package com.crazy.xdien.imageedit.sliding.draw_;

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

import com.crazy.xdien.imageedit.sliding.CrazyActivity;
import com.crazy.xdien.imageedit.sliding.Objects_.ObjectCut;
import com.crazy.xdien.imageedit.sliding.Objects_.ObjectDraw;
import com.crazy.xdien.imageedit.sliding.process.jniMatEffects;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by xdien on 11/4/14.
 */
public class DrawRectView extends View {

    private Paint mRectPaint;
    //khai bao cho do phan giai bit map
    private int mStartX = 0;
    private int mStartY = 0;
    private int mEndX = 0;
    private int mEndY = 0;
    private boolean mDrawRect = false;
    public boolean mMask = false;
    private TextPaint mTextPaint = null;
    public Point tam,tstart;
    Matrix matrix;
    private Bitmap bttemp,myThumbnail;
    public static Mat src,dst;

    private OnUpCallback mCallback = null;
    private Point topLeft;
    private int width;
    private int height;
    /*Dung chung*/
    public static int idx;
    public static ArrayList<ObjectDraw> thaotac;
    public static ObjectDraw objectDraw;

    public interface OnUpCallback {
        void onRectFinished(Rect rect);
    }

    public DrawRectView(final Context context) {
        super(context);
        init();
    }

    public DrawRectView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawRectView(final Context context, final AttributeSet attrs, final int defStyle) {
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
        mRectPaint.setColor(getContext().getResources().getColor(android.R.color.holo_green_light));
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(5); // TODO: should take from resources
        mTextPaint = new TextPaint();
        mTextPaint.setColor(getContext().getResources().getColor(android.R.color.holo_green_light));
        mTextPaint.setTextSize(20);
        tam = new Point(0,0);
        tstart = new Point(0,0);
        matrix = new Matrix();
        src = new Mat();
        dst = new Mat();
        topLeft = new Point(0,0);
        objectDraw = new ObjectDraw();
        thaotac = new ArrayList<ObjectDraw>(10);
        idx = 0;
        myThumbnail = Bitmap.createBitmap(CrazyActivity.bmCache.getBitmap(CrazyActivity.picturePath));
        Utils.bitmapToMat(myThumbnail,src);
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
                tstart = CrazyActivity.getCoordinate(mStartX, mStartY).clone();
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
                Log.w("debug", "sdasd");
                if (mCallback != null) {
                    mCallback.onRectFinished(new Rect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
                            Math.max(mEndX, mStartX), Math.max(mEndY, mStartX)));
                }
                topLeft.x = Math.min(tstart.x, tam.x);
                topLeft.y = Math.min(tstart.y, tam.y);
                width = (int)Math.abs(tstart.x - tam.x);
                height = (int)Math.abs(tstart.y - tam.y);
                Log.w("Rong va dai", String.valueOf(width) +"x"+ String.valueOf(height) + topLeft.toString());
                if(!mMask && width >0 && height > 0){
                    Log.w("Mask","ok");
                    jniMatEffects.addMask(src.getNativeObjAddr(),dst.getNativeObjAddr(),(int)topLeft.x,(int)topLeft.y,width,height,jniMatEffects.TYPE_MORPH_RECT);
                    bttemp = Bitmap.createBitmap(dst.width(),dst.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(dst,bttemp);
                    objectDraw.set((int)topLeft.x, (int)topLeft.y,width,height,jniMatEffects.TYPE_MORPH_RECT);
                    thaotac.add(objectDraw);
                    CrazyActivity.main_ImageView.setImageBitmap(bttemp);
                    CrazyActivity.main_ImageView.invalidate();
                    idx += 1;
                    Log.w("index vua tang", String.valueOf(idx));
                }
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

