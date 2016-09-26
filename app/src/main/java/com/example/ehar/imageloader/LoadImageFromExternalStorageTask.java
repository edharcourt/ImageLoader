package com.example.ehar.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by edharcourt on 9/25/16.
 */

public class LoadImageFromExternalStorageTask extends AsyncTask<String, Void, Bitmap> {

    ImageView v = null;

    public LoadImageFromExternalStorageTask(ImageView v) {
         this.v = v;
    }

    @Override
    protected Bitmap doInBackground(String... paths) {
        //return BitmapFactory.decodeFile(paths[0]);
        return Utility.decodeSampledBitmapFromPath(paths[0], 120, 120);
    }

    @Override

    // this is run on the UI thread
    protected void onPostExecute(Bitmap bm) {
        v.setImageBitmap(bm);
    }
}
