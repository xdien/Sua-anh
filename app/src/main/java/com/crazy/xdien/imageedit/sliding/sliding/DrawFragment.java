package com.crazy.xdien.imageedit.sliding.sliding;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.crazy.xdien.imageedit.R;
import com.crazy.xdien.imageedit.sliding.CrazyActivity;
import com.crazy.xdien.imageedit.sliding.Objects_.ObjectDraw;
import com.crazy.xdien.imageedit.sliding.draw_.DrawRectView;
import com.crazy.xdien.imageedit.sliding.process.jniMatEffects;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * Created by xdien on 10/7/14.
 */
public class DrawFragment extends Fragment {

    //khai bao lay toa do 1 diem
    private Mat src, ra,thisSrc;
    private String inputText;
    private int r,b,g,temp,oldColor;
    private Bitmap myThumbnail, bmtemp;
    private int width, height;
    private Point topLeft;
    private ObjectDraw objectDraw;
    private Bitmap bttemp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_draw, container, false);
        src = new Mat();
        ra = new Mat();
        topLeft = new Point();
        objectDraw = new ObjectDraw();
        thisSrc = DrawRectView.src.clone();
        myThumbnail = Bitmap.createBitmap(CrazyActivity.bmCache.getBitmap(CrazyActivity.picturePath));
        Utils.bitmapToMat(myThumbnail,src);
        final DrawRectView dragRectView = (DrawRectView) rootView.findViewById(R.id.view_fragmentDraw);

        ImageButton imageButton_forcus = (ImageButton) rootView.findViewById(R.id.imageButton_colorPick);
        imageButton_forcus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dragRectView.getVisibility() == View.VISIBLE)
                {
                    dragRectView.setVisibility(View.INVISIBLE);
                }
                else{
                    dragRectView.setVisibility(View.VISIBLE);
                }
            }
        });
        Button undo = (Button) rootView.findViewById(R.id.button_undoDraw);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawRectView.idx -= 1;
                Log.w("index truoc khi undo:", String.valueOf(DrawRectView.idx));
                if(DrawRectView.idx >= 0)
                {
                    for(int i=0; i<=DrawRectView.idx;i++) {
                        Log.w("So lan chay: ",String.valueOf(i));
                        try {
                            objectDraw = (ObjectDraw) DrawRectView.thaotac.get(i).clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                        jniMatEffects.addMask(thisSrc.getNativeObjAddr(),ra.getNativeObjAddr(),objectDraw.topLeftX,objectDraw.topLeftY,objectDraw.width,objectDraw.height,objectDraw.type);
                        thisSrc = ra.clone();
                    }
                    bttemp = Bitmap.createBitmap(thisSrc.width(),thisSrc.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(ra, bmtemp);
                    CrazyActivity.main_ImageView.setImageBitmap(bmtemp);
                    CrazyActivity.main_ImageView.invalidate();
                }
            }
        });
        return rootView;
    }

    private void openColorPicker(final  Activity context, int oldcolor)
    {
        /*final Dialog dialog = new Dialog(context);
        dialog.setTitle("Chon mau");
        dialog.setContentView(R.layout.dialog_color_pick);
        final Button ok = (Button) dialog.findViewById(R.id.button_okDialogPick);
        final ColorPicker colorPicker = (ColorPicker) dialog.findViewById(R.id.view_color_picker);
        //set lai mau cu da chon;
        if(oldcolor != 0) {
            colorPicker.setColor(oldcolor);
        }
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //luu gia tri mau duoc chon thanh gia tri int mau rgb
                    oldColor = colorPicker.getColor();
                    r = Color.red(oldColor);
                    g = Color.green(oldColor);
                    b = Color.blue(oldColor);
                    Log.i("New color: ",String.valueOf(oldColor));
                    dialog.dismiss();
            }
        });
        Button huy = (Button) dialog.findViewById(R.id.buttonCenDialogPick);
        huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();*/
    }

    // The method that displays the  for text input.
    private void showPopup(final Activity context) {
        // Created a new Dialog
        final Dialog dialog = new Dialog(context);

        // Set the title
        dialog.setTitle("Dialog Title");

        // inflate the layout
        dialog.setContentView(R.layout.popup);

        Button buttonOK = (Button) dialog.findViewById(R.id.buttonOk);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) dialog.findViewById(R.id.editText_input);
                inputText = input.getText().toString();
                dialog.dismiss();
            }
        });
        // Display the dialog
        dialog.show();
    }
}
