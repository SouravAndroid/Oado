package com.oado.activity;

// develop by Sourav Roy...

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.oado.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewScreen extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView webview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        ButterKnife.bind(this);



        webview.getSettings().setJavaScriptEnabled(true);


        Bundle bundle = getIntent().getExtras();
        if (bundle!= null){

            webview.loadUrl(bundle.getString("url"));
        }


    }



}
