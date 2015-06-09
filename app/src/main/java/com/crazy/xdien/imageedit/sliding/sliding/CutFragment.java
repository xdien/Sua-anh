package com.crazy.xdien.imageedit.sliding.sliding;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crazy.xdien.imageedit.R;
import com.crazy.xdien.imageedit.sliding.CrazyActivity;
import com.crazy.xdien.imageedit.sliding.Objects_.ObjectCut;
import com.crazy.xdien.imageedit.sliding.draw_.DragRectView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;

/**
 * Created by xdien on 9/29/14.
 */
public class CutFragment extends Fragment {

    private DragRectView.OnUpCallback mCallback = null;
    //private drawRect rect;
    private Mat src,temp,matTemp;
    private Bitmap bmtemp,bmsrc;

    public static Button Button_cut;
    public static Point topLeft,diemDau,diemCuoi;
    public static int width,height;
    private ArrayList<ObjectCut>  doingCut;
    private  int idxDoing;
    private ObjectCut temObj;
    private Bitmap croppedBitmap,myThumbnail;
    private int goc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_cut, container, false);
        doingCut = new ArrayList<ObjectCut>(10);
        idxDoing =0;
        matTemp = new Mat();
        diemCuoi = new Point(0,0);
        diemDau = new Point(0,0);
        src = new Mat();
        bmsrc = Bitmap.createBitmap(CrazyActivity.bmCache.getBitmap(CrazyActivity.picturePath));
        Utils.bitmapToMat(bmsrc,src);
            myThumbnail = Bitmap.createBitmap(bmsrc);
        /**/


        /**/
        temObj = new ObjectCut();
        final Button button_undo = (Button) rootView.findViewById(R.id.button_undo);
        button_undo.setVisibility(View.INVISIBLE);
        final DragRectView drec = (DragRectView) rootView.findViewById(R.id.view_ss);
        Button_cut = (Button) rootView.findViewById(R.id.cut_button);
        topLeft = new Point(0,0);
        button_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idxDoing = idxDoing -1;
                if(idxDoing >0) {

                    try {
                        temObj = (ObjectCut) doingCut.get(idxDoing-1).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                        croppedBitmap = Bitmap.createBitmap(myThumbnail, (int) temObj.topLeft.x, (int) temObj.topLeft.y, temObj.width, temObj.height);
                    bmtemp = Bitmap.createBitmap(croppedBitmap);
                        CrazyActivity.main_ImageView.setImageBitmap(croppedBitmap);
                        CrazyActivity.main_ImageView.invalidate();
                        doingCut.remove(idxDoing-1);
                        Log.w("da remove",String.valueOf(idxDoing-1));

                }
                else{
                    button_undo.setVisibility(View.INVISIBLE);
                    CrazyActivity.main_ImageView.setImageBitmap(myThumbnail);
                    CrazyActivity.main_ImageView.invalidate();
                    doingCut.remove(0);
                }
            }
        });
        Button_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drec.setOnUpCallback(null);
                //khong su dung clone object truoc = object sautu thay doi
                topLeft.x = Math.min(drec.tstart.x, drec.tam.x);
                topLeft.y = Math.min(drec.tstart.y, drec.tam.y);
                width = (int)Math.abs(drec.tstart.x - drec.tam.x);
                height = (int)Math.abs(drec.tstart.y - drec.tam.y);
                if(width > 0 && height >0)
                {

                    Button_cut.setVisibility(View.VISIBLE);
                }
                else {
                    Button_cut.setVisibility(View.INVISIBLE);
                }
                if(idxDoing<=0) {
                    button_undo.setVisibility(View.VISIBLE);
                    temObj.addNext(new Point(0,0), topLeft,width,height);
                    Bitmap croppedBitmap = Bitmap.createBitmap(myThumbnail, (int) temObj.topLeft.x, (int) temObj.topLeft.y, temObj.width, temObj.height);
                    Utils.bitmapToMat(croppedBitmap,matTemp);
                    bmtemp = Bitmap.createBitmap(croppedBitmap);
                    CrazyActivity.main_ImageView.setImageBitmap(croppedBitmap);
                    CrazyActivity.main_ImageView.invalidate();
                    doingCut.add(0,temObj);
                    idxDoing = idxDoing +1;
                }
                else
                {
                    try {
                        temObj = (ObjectCut) doingCut.get(idxDoing-1).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    temObj.addNext(temObj.topLeft, topLeft.clone(),width,height);
                    croppedBitmap = Bitmap.createBitmap(myThumbnail, (int) temObj.topLeft.x, (int) temObj.topLeft.y, temObj.width, temObj.height);
                    Utils.bitmapToMat(croppedBitmap,matTemp);
                    bmtemp = Bitmap.createBitmap(croppedBitmap);
                    CrazyActivity.main_ImageView.setImageBitmap(croppedBitmap);
                    CrazyActivity.main_ImageView.invalidate();
                    Log.w("da add",String.valueOf(idxDoing));
                    doingCut.add(idxDoing,temObj);
                    idxDoing = idxDoing +1;
                    button_undo.setVisibility(View.VISIBLE);
                }

            }
        });
        button_undo.setVisibility(View.VISIBLE);
        Button forcus = (Button) rootView.findViewById(R.id.button_forcus);
        forcus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(drec.getVisibility() == View.VISIBLE) {
                    Log.w("Cho phep phong to","ok");
                    drec.setVisibility(View.INVISIBLE);
                }
                else {
                    Log.w("Khong cho phep","ok");
                    drec.setVisibility(View.VISIBLE);
                }
            }
        });
//here the rest of your code
        return rootView;
    }
    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(idxDoing >0 ) {
            CrazyActivity.bmCache.putBitmap(CrazyActivity.picturePath,bmtemp);
        }
    }

}
