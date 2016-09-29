package com.example.ehar.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadImagesFromNetwork extends Activity {

    private ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_images_from_network);

        lv = (ListView) findViewById(R.id.image_listview);

        //ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this,
        //        R.layout.list_item, Images.imageUrls);


        Bitmap bitmap =
                BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);

        ImageListViewAdapter ilva =
            new ImageListViewAdapter(
                    this, R.id.list_item, Images.imageUrls, bitmap);

        lv.setAdapter(ilva);
    }
}
