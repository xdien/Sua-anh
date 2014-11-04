package com.crazy.xdien.imageedit.sliding.Objects_;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by TungNguyen on 10/30/2014.
 */
public class ObjectImage implements Serializable {
    ObjectImage()
    {
        X= 0;
        Y= 0;
        scale = 1;
        Rotationi = 0;
    }
    public  float X;
    public float Y;
    private float Rotationi;
    public float scale;
    public float getX(){
        return X;
    }
    public float getY(){
       return Y;
    }
    public float getRotation(){
        return Rotationi;
    }
    public float getScale()
    {
        return scale;
    }
    public void copyFrom(float rotation, float x, float y,float sc)
    {
        X =x;
        Y = y;
        Rotationi = rotation;
        scale =sc;
    }
}
