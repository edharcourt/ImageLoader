package com.example.ehar.imageloader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity
        extends Activity
        implements MainFragment.OnFragmentInteractionListener {

    ImageButton main_image_view = null;
    Handler h = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        h = new Handler();

        main_image_view = (ImageButton) findViewById(R.id.main_image);

        main_image_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        main_image_view.setVisibility(View.GONE);
                    }
                });
            }
        });

        Button next_button = (Button) findViewById(R.id.next);
        next_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this,
                        ImagesOnExternalStorageActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    void showMainImage(final Drawable d) {
        h.post(new Runnable() {
            @Override
            public void run() {
                main_image_view.setImageDrawable(d);
                main_image_view.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
