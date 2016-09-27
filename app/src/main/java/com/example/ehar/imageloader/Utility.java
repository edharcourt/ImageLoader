package com.example.ehar.imageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by ehar on 9/26/16.
 */

public class Utility {

    /**
     *
     * @param options
     * @param reqWidth
     *
     * @param reqHeight
     *
     * https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     *
     * @return sampling size that is a power of two.
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

    public static Bitmap decodeSampledBitmap(
            Resources res, int resId,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        // This does not load the image, just fills in dimensions.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static Bitmap decodeSampledBitmap(
            String path,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Get a list of file names we are going to load in.
     *
     * precond: must have read permission on external storage.
     */
    public static ArrayList<String> getFiles() {

        ArrayList<String> pics = new ArrayList<>();

        // Read doc on this function. Lots of bad stuff can happen.
        // might not be mounted orreadable if it is.
        //File sdCardRoot = Environment.getExternalStorageDirectory();
        //File pictures = new File(sdCardRoot, "Pictures");
        File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String sdPath = pictures.getPath() + '/';

        for (File f : pictures.listFiles()) {
            if (f.isFile())
                pics.add(sdPath + f.getName());
        }

        return pics;
    }


} // Utility
