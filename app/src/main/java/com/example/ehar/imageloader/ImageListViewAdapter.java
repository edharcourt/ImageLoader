package com.example.ehar.imageloader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by ehar on 9/27/16.
 *
 * https://developer.android.com/training/displaying-bitmaps/process-bitmap.html
 *
 * which is based off the blog post at ...
 *
 * http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html
 *
 */

public class ImageListViewAdapter extends ArrayAdapter<String> {

    private String [] urls;
    private Context ctx;
    private Bitmap placeholder;

    public ImageListViewAdapter(
            Context ctx, int resource,
            String [] urls, Bitmap placeholder) {

        super(ctx, resource, urls);

        this.urls = urls;
        this.ctx = ctx;
        this.placeholder = placeholder;
    }

    @Override
    public int getCount() {
        return urls.length;
    }

    @Override
    public String getItem(int i) {
        return urls[i];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View reusedView, ViewGroup parent) {

        ViewHolder viewHolder;

        // see if we are reusing a view
        if (reusedView == null) {
            reusedView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.url = (TextView) reusedView.findViewById(R.id.list_item_text);
            viewHolder.image = (ImageView) reusedView.findViewById(R.id.list_item_image);
            reusedView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) reusedView.getTag();
        }

        /*
         * Start an AsyncTask to start downloading an image
         * but you might need to first cancel one that is currently
         * in progress for the same ImageView.
         */
        if (cancelPotentialWork(getItem(i), viewHolder.image)) {
            final DownloadBitmapTask task = new DownloadBitmapTask(viewHolder, getItem(i), i);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(ctx.getResources(), placeholder, task);

            viewHolder.image.setImageDrawable(asyncDrawable);
            task.execute();
        }

        return reusedView;
    }


    /**
     * Cancel a task for an image view since a new one is about to start.
     *
     * @param url - the url of the image we need to download
     * @param imageView - the ImageView that is coming in to view
     * @return returns true most of the time but occasionally false
     *         when the same work is already scheduled
     */
    public static boolean cancelPotentialWork(String url, ImageView imageView) {

        // get the task for the imageview, if it exists
        final DownloadBitmapTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapUrl = bitmapWorkerTask.url;

            // If bitmapUrl is not yet set or it differs from the new url
            if (bitmapUrl == null || (!bitmapUrl.equals(url))) {
                bitmapWorkerTask.cancel(true); // Cancel previous task
            } else {
                return false; // The same work is already in progress
            }
        }

        // No task associated with the ImageView,
        // or an existing task was cancelled
        return true;
    }

    /**
     *
     * Get the reference to the bitmap task from an ImageView but
     * only if it contains an AsyncDrawable.
     *
     * @param imageView
     * @return
     */
    private static DownloadBitmapTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * Worker to download a bitmap impage over the network.
     */
    class DownloadBitmapTask extends AsyncTask<Void, Void, Bitmap>{

        ViewHolder viewHolder;
        int w, h;
        String url;
        int id;

        public DownloadBitmapTask(ViewHolder viewHolder, String url, int id) {
            this.viewHolder = viewHolder;
            //this.w = viewHolder.image.getWidth();
            //this.h = viewHolder.image.getHeight();
            this.w = 120;
            this.h = 120;
            this.url = url;
            this.id = id;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return Utility.downloadBitmap(url, w, h);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (isCancelled())
                bitmap = null;

            // We only display the image if the current task
            // is the one that was fired off to start with
            if (viewHolder.image != null && bitmap != null) {
                final ImageView imageView = viewHolder.image;
                final DownloadBitmapTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    viewHolder.url.setText(url);
                }
            }
        }
    }

    /**
     * For use in the ViewHolder pattern.
     *
     * https://developer.android.com/training/improving-layouts/smooth-scrolling.html
     *
     */
    private static class ViewHolder {
        TextView url;
        ImageView image;
    }


    /**
     * This AsyncDrawable serves two purposes:
     *
     * 1) It contains a link back to the AsyncTask that is supposed to load it.
     *
     * 2) It contains a placeholder image to show while the real image is being
     *    downloaded.
     *
     */
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<DownloadBitmapTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             DownloadBitmapTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<>(bitmapWorkerTask);
        }

        public DownloadBitmapTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

}  // ImageListViewAdapter