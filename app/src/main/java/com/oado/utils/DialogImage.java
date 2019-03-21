package com.oado.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.jsibbold.zoomage.ZoomageView;
import com.oado.R;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class DialogImage extends AppCompatActivity {

    String image_url;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image);

        final ZoomageView myZoomageView = findViewById(R.id.myZoomageView);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            image_url = bundle.getString("url");


            Picasso.with(this).load(image_url).placeholder(R.mipmap.no_image)
                    .into(myZoomageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                    R.mipmap.no_image);

                            myZoomageView.setImageBitmap(icon);

                            Toasty.error(getApplicationContext(),
                                    "No image available",
                                    Toast.LENGTH_LONG, true).show();

                        }
                    });


        }





    }


}
