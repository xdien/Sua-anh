package com.crazy.xdien.imageedit.sliding.Objects_;

import org.apache.http.HeaderIterator;
import org.opencv.core.Point;

/**
 * Created by xdien on 11/1/14.
 */
public class ObjectCut implements Cloneable {
    public int width;
    public int height;
    public  Point topLeft;
    public int angle;

    public void ObjectCut()
    {
        topLeft = new Point(0,0);
        width = 0;
        height = 0;
    }

    public int getWidth()
    {
        return width;

    }
    public int getHeight()
    {
        return height;
    }

    public void addNext(Point preTopleft, Point TopLeft, int Width, int Height)
    {
        TopLeft.x += preTopleft.x;//tinh toa do hien tai so voi anh goc
        TopLeft.y +=preTopleft.y;
        topLeft = TopLeft.clone();
        width = Width;
        height = Height;
    }
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
