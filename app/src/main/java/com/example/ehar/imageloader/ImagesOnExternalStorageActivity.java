package com.example.ehar.imageloader;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
                sd_card_read_permission = true;
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

        getFiles();

        final Resources r = getResources();

        // set up the click listeners for each image
        for (int i = 0; i < pics.size(); i++) {
            final int id = r.getIdentifier("i" + (i+4), "id", getPackageName());
            final ImageButton ib = (ImageButton) findViewById(id);
            final int tmp_i = i;
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // should we check that it might already be loaded?
                    Bitmap bm1 = decodeSampledBitmapFromResource(r,id,120,120);
                    Bitmap bm = BitmapFactory.decodeFile(pics.get(tmp_i));
                    ib.setImageBitmap(bm);
                }
            });
        }
    }

    /**
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
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