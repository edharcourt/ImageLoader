package com.example.ehar.imageloader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * We need to process bitmaps off the UI thread
 *
 *     https://developer.android.com/training/displaying-bitmaps/process-bitmap.html
 *
 * Designing for responsiveness
 *
 *     https://developer.android.com/training/articles/perf-anr.html
 *
 * Caching Bitmaps
 *
 *     https://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
 *
 * Managing Bitmap Mempry
 *
 *     https://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
 *
 * Displaying Bitmaps in UI using background thread, ViewPager, cache, ...
 *
 *     https://developer.android.com/training/displaying-bitmaps/display-bitmap.html
 *
 * Understanding Weak References
 *
 *     https://community.oracle.com/blogs/enicholas/2006/05/04/understanding-weak-references
 */

public class ImagesOnExternalStorageActivity extends Activity {

    public static final int EXT_STORAGE_READ_REQUEST = 999;

    boolean sd_card_read_permission = false;
    BitmapFactory.Options bmOptions = null;
    String sdPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_on_external_storage);

        bmOptions = new BitmapFactory.Options();

        // each pixel is 4 bytes and includes alpha channel
        // default is ARGB_565
        // https://developer.android.com/reference/android/graphics/Bitmap.Config.html
        bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        sd_card_read_permission = false;

        handle_permissions();

        if (sd_card_read_permission)
            init2();
        else {
            // Toast
            // go back to previous activity
        }

        return;
    }

    public void handle_permissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String []
                    {Manifest.permission.READ_EXTERNAL_STORAGE}, EXT_STORAGE_READ_REQUEST);
            return;
        }
        else {
            sd_card_read_permission  = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == EXT_STORAGE_READ_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sd_card_read_permission = true;
                init2();
                return;
            }
            else {
                sd_card_read_permission = false;
                Log.e("PERM:", "Access to external storage denied");
                Toast.makeText(this, R.string.NO_EXTRNL_STRG_TST_MSG, Toast.LENGTH_LONG);
            }
        }
    }


    /**
     * precondition: sd_card_read_permission
     */
    protected void init() {

        if (!sd_card_read_permission) return;

        final ArrayList<String> pics = Utility.getFiles();

        final Resources r = getResources();

        // set up the click listeners for each image
        for (int i = 0; i < 4; i++) {
            final int id = r.getIdentifier("i" + (i+4), "id", getPackageName());
            final ImageButton ib = (ImageButton) findViewById(id);
            final int tmp_i = i;
            int red = (int) (Math.random()*256);
            int green = (int) (Math.random()*256);
            int blue = (int) (Math.random()*256);
            int c = 0xFF000000 | (red << 16) | (green << 8) | blue;
            ib.setBackgroundColor(c);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // should we check that it might already be loaded?
                    // Bitmap bm1 = decodeSampledBitmapFromResource(r,id,120,120);
                    new Thread(new Runnable() {
                        public void run() {

                            final Bitmap bm = BitmapFactory.decodeFile(pics.get(tmp_i));

                            // Why do we have to do this? Because we're not on the UI thread.
                            ib.post(new Runnable() {
                                @Override
                                public void run() {
                                    ib.setImageBitmap(bm);
                                }
                            });
                        }
                    }).start();
                }
            });
        }
    }

    /**
     * precondition: sd_card_read_permission
     */
    protected void init2() {

        if (!sd_card_read_permission) return;

        final ArrayList<String> pics = Utility.getFiles();

        final Resources r = getResources();

        // set up the click listeners for each image
        for (int i = 0; i < 4; i++) {
            final int id = r.getIdentifier("i" + (i+4), "id", getPackageName());
            final ImageButton ib = (ImageButton) findViewById(id);
            final int tmp_i = i;
            int red = (int) (Math.random()*256);
            int green = (int) (Math.random()*256);
            int blue = (int) (Math.random()*256);
            int c = 0xFF000000 | (red << 16) | (green << 8) | (blue);
            ib.setBackgroundColor(c);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new LoadImageFromExternalStorageTask(ib, ib.getWidth(), ib.getHeight()).execute(pics.get(tmp_i));
                }
            });
        }

        Button next_button = (Button) findViewById(R.id.next);
        next_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        ImagesOnExternalStorageActivity.this,
                        LoadImagesFromNetwork.class);
                startActivity(intent);
            }
        });

    }



    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}