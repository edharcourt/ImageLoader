package com.example.ehar.imageloader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_on_external_storage);

        Resources r = getResources();

        // get list of file names in Pictures folder.
        getFiles();

        /*
        // set up the click listeners for each image
        for (int i = 0; i < 4; i++) {
            int id = r.getIdentifier("i" + i, "id", getPackageName());
            ImageButton ib = (ImageButton) findViewById(id);

            ib.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }
            });

        }*/

        return;
    }


    protected void getFiles() {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String []
                    {Manifest.permission.READ_EXTERNAL_STORAGE}, EXT_STORAGE_READ_REQUEST);
            return;
        }

        pics = new ArrayList<>();

        File sdCardRoot = Environment.getExternalStorageDirectory();
        File pictures = new File(sdCardRoot, "Pictures");
        boolean r = pictures.canRead();
        boolean d = pictures.isDirectory();
        boolean er = isExternalStorageReadable();

        for (File f : pictures.listFiles()) {
            if (f.isFile())
                pics.add(f.getName());
        }

        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == EXT_STORAGE_READ_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFiles();
            }
        }

        Log.e("PERM:", "Access to external storage denied");
        Toast.makeText(this, R.string.NO_EXTNL_STRG_TST_MSG, Toast.LENGTH_LONG);
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