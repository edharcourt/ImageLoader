package com.example.ehar.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ehar on 9/27/16.
 */

public class ImageListViewAdapter extends ArrayAdapter<String> {

    private String [] urls;

    public ImageListViewAdapter(Context ctx, int resource, String [] urls) {
        super(ctx, resource, urls);
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.length;
    }


    @Override
    public String getItem(int i) {
        return urls[i];
    }

    //@Override
    //public long getItemId(int i) {
    //    return 0;
    //}

    @Override
    public View getView(int i, View reusedView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (reusedView ==  null) {
            reusedView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.url = (TextView) reusedView.findViewById(R.id.list_item_text);
            viewHolder.image = (ImageView) reusedView.findViewById(R.id.list_item_image);
            reusedView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) reusedView.getTag();
        }

        // TextView url = (TextView) reusedView.findViewById(R.id.list_item_text);
        // viewHolder.url.setText(getItem(i));

        // ImageView iv = (ImageView) reusedView.findViewById(R.id.list_item_image);

        DownLoadBitmapTask task = new DownLoadBitmapTask(viewHolder, getItem(i));
        task.execute(getItem(i));

        return reusedView;
    }

    class DownLoadBitmapTask extends AsyncTask<String, Void, Bitmap>{

        ViewHolder viewHolder;
        int w, h;
        String url;

        public DownLoadBitmapTask(ViewHolder viewHolder, String url) {
            this.viewHolder = viewHolder;
            this.w = viewHolder.image.getWidth();
            this.h = viewHolder.image.getHeight();
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            return Utility.downloadBitmap(url[0], w, h);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            viewHolder.image.setImageBitmap(bitmap);
            viewHolder.url.setText(url);
        }
    }

    private static class ViewHolder {
        TextView url;
        ImageView image;
    }

}  // ImageListViewAdapter
