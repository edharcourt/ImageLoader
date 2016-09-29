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

    public LoadImageFromExternalStorageTask(ImageView v) {
        this.v = v;
        this.w = v.getWidth();
        this.h = v.getHeight();
    }

    @Override
    protected Bitmap doInBackground(String... paths) {
        return Utility.decodeSampledBitmap(paths[0], w, h);
    }

    @Override

    // this is run on the UI thread
    protected void onPostExecute(Bitmap bm) {
        v.setImageBitmap(bm);
    }
}
