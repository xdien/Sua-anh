package com.crazy.xdien.imageedit.sliding.Objects_;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.crazy.xdien.imageedit.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.FileOutputStream;
import java.util.ArrayList;

public class SaveToFilePdf extends Activity {

    private String duongdan;
    private Intent intent;
    private Image img;
    private int soluonganh;
    private ArrayList<String> danhsachmoi;
    private ArrayList<ObjectImage> mylist;
    private ObjectImage objectImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tung);
        intent = getIntent();
        //duongdan = intent.getStringExtra("duongdan");
        danhsachmoi = new ArrayList<String>();
        danhsachmoi = EportPdfFragment.danhsach;
        mylist = (ArrayList<ObjectImage>) intent.getSerializableExtra("mylist");
        objectImage = new ObjectImage();
        main();
    }

    public void main() {
        Document document = new Document();

        try {

            PdfWriter.getInstance(document,
                    new FileOutputStream("/sdcard/picture.pdf"));
            document.open();

            if (!document.isOpen()) {
                Toast.makeText(SaveToFilePdf.this, "khong the mo file", Toast.LENGTH_LONG).show();
            }
            soluonganh = EportPdfFragment.danhsach.size();
            Log.w("soluong",String.valueOf(soluonganh));
            if(EportPdfFragment.danhsach.isEmpty())
            {
                Toast.makeText(this,"so luong anh == 0",Toast.LENGTH_SHORT);
            }
            else {
                for (int i = 0; i<soluonganh; i++) {
                    img = Image.getInstance(EportPdfFragment.danhsach.get(i));
                    objectImage = mylist.get(i);
                    //Image img = Image.getInstance("stelvio.jpg");
                    img.scalePercent(objectImage.getScale()*45);//"PHÓNG TO THU NHỎ"
                    img.setAlignment(img.ALIGN_CENTER);
                    img.setRotationDegrees(objectImage.getRotation());//"XOAY BỨC ẢNH TRÁI HOẶC PHẢI(-)"
                    document.add(img);
                    Log.w("gia tri cua i",String.valueOf(i));
                }
            }


            /*String imageUrl = "http://jenkov.com/images/" +
                    "20081123-20081123-3E1W7902-small-portrait.jpg";

            Image image2 = Image.getInstance(new URL(imageUrl));
            document.add(image2);*/


            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}