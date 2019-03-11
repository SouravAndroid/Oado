package com.oado.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.jsibbold.zoomage.ZoomageView;
import com.oado.R;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class DialogImage extends Dialog {

    private Context context;
    private String image_url;
   // private ImageView goProDialogImage;


    public DialogImage(@NonNull Context context, String image_url) {
        super(context);
        this.context = context;
        this.image_url = image_url;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image);
        setCanceledOnTouchOutside(false);
      //  goProDialogImage = findViewById(R.id.goProDialogImage);


        final ZoomageView myZoomageView = findViewById(R.id.myZoomageView);

        Picasso.with(context).load(image_url).placeholder(R.mipmap.no_image)
                .into(myZoomageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                                R.mipmap.no_image);

                        myZoomageView.setImageBitmap(icon);

                        Toasty.error(context,
                                "No image available",
                                Toast.LENGTH_LONG, true).show();

                        dismiss();
                    }
                });


    }


}
