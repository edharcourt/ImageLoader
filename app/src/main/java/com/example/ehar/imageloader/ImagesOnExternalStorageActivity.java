package com.example.ehar.imageloader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ImagesOnExternalStorageActivity extends AppCompatActivity {

    ArrayList<String> pics = null;
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

        handle_permissions();


        if (sd_card_read_permission)
            init();


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
                init();
            }
        }

        sd_card_read_permission = false;
        Log.e("PERM:", "Access to external storage denied");
        Toast.makeText(this, R.string.NO_EXTNL_STRG_TST_MSG, Toast.LENGTH_LONG);
    }

    protected void loadImage(String path, ImageView v) {
        if (!sd_card_read_permission) return;

    }


    /**
     * precondition: sd_card_read_permission
     */
    protected void init() {

        if (!sd_card_read_permission) return;

        getFiles();

        Resources r = getResources();

        // set up the click listeners for each image
        for (int i = 0; i < pics.size(); i++) {
            int id = r.getIdentifier("i" + i, "id", getPackageName());
            ImageButton ib = (ImageButton) findViewById(id);

            ib.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }
            });

        }

    }

    /**
     * Get a list of file names we are going to load in.
     */
    protected void getFiles() {

        pics = new ArrayList<>();

        File sdCardRoot = Environment.getExternalStorageDirectory();
        File pictures = new File(sdCardRoot, "Pictures");
        sdPath = pictures.getPath() + '/';

        //boolean r = pictures.canRead();
        //boolean d = pictures.isDirectory();
        //boolean er = isExternalStorageReadable();

        for (File f : pictures.listFiles()) {
            if (f.isFile())
                pics.add(sdPath + f.getName());
        }

        return;
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