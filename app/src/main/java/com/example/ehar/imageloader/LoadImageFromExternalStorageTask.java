package com.example.ehar.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by edharcourt on 9/25/16.
 */

public class LoadImageFromExternalStorageTask extends AsyncTask<String, Void, Bitmap> {

    // Void is the type of publishProgress and onProgressUpdate methods.
    // Often an Integer as Percenrtage but Void if not implemented.

    ImageView v = null;  // Should we make this a WeakReference?
    int w, h;

    public LoadImageFromExternalStorageTask(ImageView v, int w, int h) {
        this.v = v;
        this.w = w;
        this.h = h;
    }

    @Override
    protected Bitmap doInBackground(String... paths) {
        //return BitmapFactory.decodeFile(paths[0]);
        return Utility.decodeSampledBitmap(paths[0], w, h);
    }

    @Override

    // this is run on the UI thread
    protected void onPostExecute(Bitmap bm) {
        v.setImageBitmap(bm);
    }
}
