package com.crazy.xdien.imageedit.sliding.sliding;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.crazy.xdien.imageedit.R;
import com.crazy.xdien.imageedit.sliding.CrazyActivity;
import com.crazy.xdien.imageedit.sliding.process.jniMatEffects;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.Stack;

/**
 * Created by xdien on 11/4/14.
 */
public class RotateFragment extends Fragment {
    public static Mat src,dst;
    private Bitmap bttemp, myThumbnail, luutru;
    public RotateFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_rotate, container, false);
        myThumbnail = Bitmap.createBitmap(CrazyActivity.bmCache.getBitmap(CrazyActivity.picturePath));
        src = new Mat();

        Utils.bitmapToMat(myThumbnail, src);
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.seekBar_rotate);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                dst = jniMatEffects.RotateImage(src,i);
                bttemp = Bitmap.createBitmap(dst.width(),dst.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(dst,bttemp);
                luutru = Bitmap.createBitmap(bttemp);
                CrazyActivity.main_ImageView.setImageBitmap(bttemp);
                CrazyActivity.main_ImageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(luutru != null)
                {
                    CrazyActivity.bmCache.putBitmap(CrazyActivity.picturePath,luutru);
                }
            }
        });
        //imageView = (ImageViewTouch) rootView.findViewById(R.id.main_imageView);
        return rootView;
    }
}
