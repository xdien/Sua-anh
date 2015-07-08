package com.crazy.xdien.imageedit.sliding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crazy.xdien.imageedit.R;
import com.crazy.xdien.imageedit.sliding.ImageCache.BitmapLruCache;
import com.crazy.xdien.imageedit.sliding.Objects_.EportPdfFragment;
import com.crazy.xdien.imageedit.sliding.Objects_.SaveToFile;
import com.crazy.xdien.imageedit.sliding.sliding.CutFragment;
import com.crazy.xdien.imageedit.sliding.sliding.DrawFragment;
import com.crazy.xdien.imageedit.sliding.sliding.EffectFragment;
import com.crazy.xdien.imageedit.sliding.sliding.HomeFragment;
import com.crazy.xdien.imageedit.sliding.sliding.NavDrawerItem;
import com.crazy.xdien.imageedit.sliding.sliding.NavDrawerListAdapter;
import com.crazy.xdien.imageedit.sliding.sliding.RotateFragment;
import com.crazy.xdien.imageedit.sliding.sliding.SmoothingFragment;
import com.crazy.xdien.imageedit.sliding.sliding.ThresholdFragment;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Point;
//import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class CrazyActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    //Bien dung chung cho chuong trinh
    public final static int PICKFILE_RESULT_CODE = 100;
    public static Bitmap thumbnail;
    public static BitmapLruCache bmCache;
    public static String picturePath;
    public static ImageView main_ImageView;
    private Button main_openfileImage;
    public  static float eventX,eventY;
    public static int x,y;
    public static Point kq;
    public static float[] eventXY;
    public static Matrix invertMatrix;
    public static String fileName;
    public boolean chooseIamge;//kiem tra xem da chon file anh chua

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    //khai bao ham cache
    static {
        System.loadLibrary("jniEffects");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //khoi dong ham cache trong
        invertMatrix =new Matrix();
        kq = new Point(0,0);
        //listen button openimgae
        main_openfileImage = (Button) findViewById(R.id.button_openImage);
        main_openfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile();
            }
        });
        //ActionBar actionBar = (ActionBar) findViewById(R.id.button);
        //khoi dong cho main_ImageView
        //Log.w("lay tu jni",getStringFromNative());
        main_ImageView = (ImageView) findViewById(R.id.main_imageView);
        bmCache = new BitmapLruCache();
        mTitle = mDrawerTitle = getTitle();
        chooseIamge = false;

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6],navMenuIcons.getResourceId(6, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7],navMenuIcons.getResourceId(7, -1)));


        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }
    static {
        System.loadLibrary("jniEffects");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {

                // now we can call opencv code !
                //oworld();
            } else {
                super.onManagerConnected(status);
            }
        }
    };
    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.getPath:
                openFile();
                return true;
            case R.id.save:
                //saveImage(thumbnail,picturePath);
                openFolder();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*Ham nhan ket qua tra ve*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode == RESULT_OK)
                {Uri selectedImage = data.getData();
                    String[] filePath = { MediaStore.Images.Media.DATA };
                    Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    picturePath = c.getString(columnIndex);
                    c.close();
                    thumbnail = (BitmapFactory.decodeFile(picturePath));
                    //save image to cache
                    if(thumbnail == null){
                        Toast.makeText(this,"Canh bao: khong the giai ma file chuong trinh co the dong!",Toast.LENGTH_LONG);
                        Log.e("MainActivity open fail:","Chuong trinh se dong!!");
                    }
                    else {
                        bmCache.putBitmap(picturePath, thumbnail);
                        Log.w("path of image from gallery......******************.........", picturePath + "");
                        chooseIamge = true;
                        main_openfileImage.setVisibility(View.INVISIBLE);
                        main_ImageView.setImageBitmap(thumbnail);
                        displayView(0);
                    }
                }
                break;
            default:
                break;
        }

    }
    /*Ham mo cac phan tu khac va save*/

    private void openFile(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICKFILE_RESULT_CODE);
    }
    public void openFolder()
    {
        final Context context = CrazyActivity.this;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Dat ten");
        alert.setMessage("Enter The Name");

        final EditText input = new EditText(context);
        alert.setView(input);


        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String srt = input.getEditableText().toString();
                fileName = picturePath.substring(picturePath.lastIndexOf("/") + 1);
                SaveToFile.SaveBitmap("/mnt/sdcard/"+srt+".bmp", bmCache.getBitmap(picturePath));
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
    private void saveImage(Bitmap inputToSave, String name)
    {
          if(inputToSave != null)
          {
              FileOutputStream out = null;
              try {
                  out = new FileOutputStream(name.substring(0,name.length()-4)+"ed"+".jpg");
                  inputToSave.compress(Bitmap.CompressFormat.JPEG,90, out);
              } catch (Exception e) {
                  e.printStackTrace();
              } finally {
                  try {
                      if (out != null) {
                          out.close();
                      }
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
          }
    }
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    if(!chooseIamge) {

                        Toast.makeText(this, "Ban hay chon mot anh truoc khi dung chuc nang nay.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        fragment = new EffectFragment();
                        //main_ImageView.setImageResource(android.R.color.transparent);
                    }
                    break;
                case 2:
                    if(!chooseIamge) {

                        Toast.makeText(this, "Ban hay chon mot anh truoc khi dung chuc nang nay.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        fragment = new SmoothingFragment();
                    }
                    break;
                case 3:

                    if(!chooseIamge) {

                        Toast.makeText(this, "Ban hay chon mot anh truoc khi dung chuc nang nay.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        fragment = new ThresholdFragment();
                    }
                    break;
                case 4:
                    if(!chooseIamge) {

                        Toast.makeText(this, "Ban hay chon mot anh truoc khi dung chuc nang nay.", Toast.LENGTH_LONG).show();
                    }
                    else {
                //        fragment = new DrawFragment();
                    }
                    break;
                case 5:
                    if(!chooseIamge) {

                        Toast.makeText(this, "Ban hay chon mot anh truoc khi dung chuc nang nay.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        fragment = new CutFragment();
                    }
                    break;

                case 6:
                    fragment = new RotateFragment();
                    break;
                case 7:
                    Intent intent = new Intent(this, EportPdfFragment.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, mLoaderCallback);
        // you may be tempted, to do something here, but it's *async*, and may take some time,
        // so any opencv call here will lead to unresolved native errors.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    //cap nhat lai imageView sau khi su ly, duoc cac ham xu ly khac goi khi can

    public static void upDisplayImageView(String id)
    {
        main_ImageView.setImageBitmap(bmCache.getBitmap(id));
    }
    final float[] getPointerCoords(MotionEvent e)
    {
        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();
        main_ImageView.getImageMatrix().invert(matrix);
        matrix.postTranslate(main_ImageView.getScrollX(), main_ImageView.getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }
    public static Point getCoordinate(MotionEvent motionEvent)
    {

        eventX = motionEvent.getX();
        eventY = motionEvent.getY();
        eventXY = new float[]{eventX, eventY};
        CrazyActivity.main_ImageView.getImageMatrix().invert(invertMatrix);
        invertMatrix.mapPoints(eventXY);
        x = Integer.valueOf((int) eventXY[0]);
        y = Integer.valueOf((int) eventXY[1]);
        kq.x = x;
        kq.y = y;
        return kq;

    }
    public static Point getCoordinate(float x, float y)
    {
        eventXY = new float[]{x, y};
        CrazyActivity.main_ImageView.getImageMatrix().invert(invertMatrix);
        invertMatrix.mapPoints(eventXY);
        x = Integer.valueOf((int) eventXY[0]);
        y = Integer.valueOf((int) eventXY[1]);
        kq.x = x;
        kq.y = y;
        return kq;

    }
}