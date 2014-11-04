package com.crazy.xdien.imageedit.sliding.sliding;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.crazy.xdien.imageedit.R;
import com.crazy.xdien.imageedit.sliding.ImageCache.GridViewAdapter;
import com.crazy.xdien.imageedit.sliding.CrazyActivity;
import com.crazy.xdien.imageedit.sliding.process.jniMatEffects;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.cvtColor;

/**
 * Created by xdien on 10/2/14.
 */
public class EffectFragment extends Fragment implements View.OnTouchListener {

   //public void HistogramFragment(){};
    Matrix invertMatrix;
    private GridView gridView;
    private Mat src,ra;
    private GridViewAdapter mygridViewAdapter;

    private Bitmap tbm,luutru;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //((ViewGroup)scrollChildLayout.getParent()).removeView(scrollChildLayout);
        final View rootView = inflater.inflate(R.layout.fragment_effects, container,false);
        tbm = Bitmap.createBitmap(CrazyActivity.bmCache.getBitmap(CrazyActivity.picturePath));
        src = new Mat();
        Utils.bitmapToMat(tbm,src);
        invertMatrix = new Matrix();
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        return rootView;
    }
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        //Log.i("Touch main_ImageView coordinates", String.valueOf(MainActivity.main_ImageView.getTop()));
        switch (action) {
            default:
                break;
        }
        return true;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public  void onResume(){
        super.onResume();
        mygridViewAdapter = new GridViewAdapter(getActivity());
        mygridViewAdapter.AddImages(tbm,0);
        mygridViewAdapter.AddImages(tbm,1);
        mygridViewAdapter.AddImages(tbm,2);
        mygridViewAdapter.AddImages(tbm,3);
        gridView.setAdapter(mygridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        ra = src.clone();
                        Log.i("info idx:",String.valueOf(i));
                        jniMatEffects.calculator_HSV(src.getNativeObjAddr(), ra.getNativeObjAddr(), 0, 8);
                        setNewImage(ra);
                        break;
                    case 1:
                        ra = src.clone();
                        Log.i("info idx:",String.valueOf(i));
                        jniMatEffects.calculator_HSV(src.getNativeObjAddr(), ra.getNativeObjAddr(), 1, 10);
                        //MainActivity.thumbnail = null;
                        setNewImage(ra);
                        break;
                    case 2:
                        ra = src.clone();
                        jniMatEffects.calculator_HSV(src.getNativeObjAddr(), ra.getNativeObjAddr(), 2, 5);
                        setNewImage(ra);
                        break;
                    case 3:
                        ra = src.clone();
                        jniMatEffects.equalizeIntensity(src.getNativeObjAddr(), ra.getNativeObjAddr());
                        setNewImage(ra);
                        break;
                    default:
                        break;
                }
            }
        });
    }
    @Override
    public void onStop(){
        super.onStop();
        //luu lai bitmap truoc khi roi khoi
        if(luutru != null) {
            CrazyActivity.bmCache.putBitmap(CrazyActivity.picturePath, luutru);
        }
    }

    public void setNewImage(Mat Src)
    {
        Utils.matToBitmap(Src,tbm);
        luutru = Bitmap.createBitmap(tbm);
        CrazyActivity.main_ImageView.setImageBitmap(tbm);
        CrazyActivity.main_ImageView.invalidate();
    }
}
