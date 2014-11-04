package com.crazy.xdien.imageedit.sliding.Objects_;

/**
 * Created by xdien on 11/3/14.
 */
public class ObjectDraw implements Cloneable {
    public  String inputText;
    public int topLeftX;
    public int topLeftY;
    public int width;
    public int height;
    public int type;
    public void set(int TopleftX,int TopleftY,int Width, int Heigh, int kieu){
        topLeftX = TopleftX;
        topLeftY = TopleftY;
        width = Width;
        height = Heigh;
        type = kieu;
    }
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
