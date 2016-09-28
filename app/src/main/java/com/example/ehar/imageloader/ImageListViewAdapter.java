package com.example.ehar.imageloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

        if (reusedView ==  null) {
            reusedView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView url = (TextView) reusedView.findViewById(R.id.list_item);

        url.setText(getItem(i));

        return reusedView;
    }
}
