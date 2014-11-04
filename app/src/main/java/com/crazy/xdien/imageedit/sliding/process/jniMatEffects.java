package com.crazy.xdien.imageedit.sliding.process;

import android.app.Activity;
import android.util.Log;


import org.opencv.core.Mat;
import org.opencv.core.Point;

import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;
import static org.opencv.imgproc.Imgproc.warpAffine;

/**
 * Created by xdien on 10/25/14.
 */

public class jniMatEffects{
    public static final int TYPE_MORPH_RECT = 3;
    public static final int TYPE_MORPH_NULL = 4;

    //su dung .getNativeObjAddr() truyen va nhan doi tuong
    public static native void Smoothing(long addrMatSrc, long addrMatDst);
    public static native void equalizeIntensity(long addrMatsrc, long addrMatdst);
    public static native void getStringFromNative();
    public static native void rgbEqualizeHist(long addrMatSrc, long addrMatDst);
    //public native void storeMat(long addrMatSrc);
    public static native void calculator_HSV(long addMatSrc, long addMatDst,int type_HSV, int value);
    public static native void img_smoothing(long src,long dst,int MAX_KERNEL_LENGTH);
    public static native void addMask(long addrMatSrc, long addrMatDst, int topLeftX, int topLeftY, int width, int height, int type);

    public static Mat RotateImage(Mat src,int iAngle)
    {
        Mat res = new Mat();
        int iImageHieght = src.rows() / 2;
        int iImageWidth = src.cols() / 2;
        Mat matRotation = getRotationMatrix2D( new Point(iImageWidth, iImageHieght), (iAngle - 180), 1 );
        warpAffine(src, res, matRotation, src.size());
        return res;
    }
}
