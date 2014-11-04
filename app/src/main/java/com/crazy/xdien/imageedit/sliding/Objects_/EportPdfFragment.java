package com.crazy.xdien.imageedit.sliding.Objects_;

/**
 * Created by xdien on 11/4/14.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.crazy.xdien.imageedit.R;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


public class EportPdfFragment extends Activity {
    public final int REQUEST_CODE_PATH =110;
    public final int REQUEST_CODE_LINKS =112;
    public final int REQUEST_ROTATION =113;
    public static String picturePath;
    public  static Bitmap thumbnail;
    public static ImageView viewImage;
    public static ArrayList<String> danhsach;
    private int soluonganh;
    private ImageAdapter myImageAdapter;
    private  GridView gridview;
    private static ArrayList<ObjectImage> danhsachgiatri;
    private ObjectImage objectImage;
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tung);
        b = (Button) findViewById(R.id.SelectPhoTo);
        viewImage = (ImageView) findViewById(R.id.ViewImage);
        //
        danhsachgiatri =new ArrayList<ObjectImage>(30);
        objectImage = new ObjectImage();

        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(EportPdfFragment.this,Rotation.class);
                //Log.w("so luong hinh:",String.valueOf(myImageAdapter.getCount()));
                //myImageAdapter.itemList.get(i);
                //gui doi tuong wa

                if(!danhsachgiatri.isEmpty()) {

                    intent.putExtra("soluonganh",myImageAdapter.getCount());
                    intent.putExtra("myclass", danhsachgiatri.get(i));
                    intent.putExtra("idx",i);
                    intent.putExtra("duongdan", myImageAdapter.itemList.get(i));
                    startActivityForResult(intent,REQUEST_ROTATION);
                }
            }
        });
        Button Show = (Button) findViewById(R.id.buttonShow);
        Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.adobe.reader");//.AdobeReader
                if(intent==null)
                {
                    Toast.makeText(getApplicationContext(),"Xin hay cai dat adobe truoc",Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(intent);
                }
            }
        });
        danhsach = new ArrayList<String>();
        b.setOnClickListener(new View.OnClickListener() {
            public void onCreate(Bundle savedInstanceState) {
                //super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main_tung);//`enter code here`
                PdfReader reader;

                File file = new File("/mnt/sdcard/Download/Adobe Reader");
                try{
                    reader = new PdfReader(file.getAbsolutePath());

                    for (int i = 0; i < reader.getXrefSize(); i++) {
                        PdfObject pdfobj= reader.getPdfObject(i);
                        if (pdfobj == null || !pdfobj.isStream()) {
                            continue;
                        }

                        PdfStream stream = (PdfStream) pdfobj;
                        PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);

                        if (pdfsubtype != null && pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
                            byte[] img = PdfReader.getStreamBytesRaw((PRStream) stream);
                            FileOutputStream out = new FileOutputStream(new
                                    File(file.getParentFile(),String.format("%1$05d", i) + ".jpg"));
                            out.write(img); out.flush(); out.close();
                        }
                    }
                } catch (Exception e) { }
            }
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



        Button Save = (Button) findViewById(R.id.button2);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MyImageExample = new Intent(EportPdfFragment.this, SaveToFilePdf.class);
                MyImageExample.putExtra("duongdan", danhsach);
                MyImageExample.putExtra("soluong",soluonganh);
                MyImageExample.putExtra("mylist",danhsachgiatri);
                Log.w("soluong",String.valueOf(soluonganh));
                startActivity(MyImageExample);

            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //T000 Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "you selected the camera option", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item2:
                Toast.makeText(this, "PDF Camera", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);


    }

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EportPdfFragment.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    //Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //startActivityForResult(intent, 2);
                    Intent intent = new Intent(EportPdfFragment.this,AndroidCustomGalleryActivity.class);
                    startActivityForResult(intent,REQUEST_CODE_LINKS);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_LINKS:
                if (resultCode == RESULT_OK)
                {
                    myImageAdapter = new ImageAdapter(this);
                    gridview.setAdapter(myImageAdapter);
                    danhsach = data.getStringArrayListExtra("danhsach");
                    soluonganh = Integer.valueOf(data.getStringExtra("soluonganh"));
                    for (int i=0;i<soluonganh;i++)
                    {
                        Log.w("duong dan la",danhsach.get(i));
                        myImageAdapter.add(danhsach.get(i));
                        danhsachgiatri.add(objectImage);//gia tri mac dinh
                    }
                    gridview.invalidate();

                }
                break;
            case REQUEST_ROTATION:
                if (resultCode == RESULT_OK) {
                    danhsachgiatri.set(data.getIntExtra("idx", 0), (ObjectImage) data.getSerializableExtra("myclass"));
                    Log.w("gffgfg","fggfg");
                }
                break;
            default:
                break;
        }
        /*if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    viewImage.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();
                thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image from gallery......******************.........", picturePath + "");
                viewImage.setImageBitmap(thumbnail);
            }
        }*/
    }
}

