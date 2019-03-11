package com.oado.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oado.R;
import com.oado.utils.PrefManager;
import com.oado.utils.ValidationClass;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileView extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.item_image) CircleImageView item_image;
    @BindView(R.id.tv_name) TextView tv_name;
    @BindView(R.id.tv_email) TextView tv_email;
    @BindView(R.id.tv_phone) TextView tv_phone;
    @BindView(R.id.tv_cast) TextView tv_cast;
    @BindView(R.id.tv_address) TextView tv_address;

    @BindView(R.id.rel_change_password) RelativeLayout rel_change_password;

    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_show);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Profile Details");
        }


        prefManager = new PrefManager(this);
        validationClass = new ValidationClass(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        rel_change_password.setOnClickListener(this);


        if (!prefManager.getImage().isEmpty()){

            Picasso.with(this).load(prefManager.getImage()).placeholder(R.mipmap.no_image)
                    .into(item_image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                    R.mipmap.no_image);

                            item_image.setImageBitmap(icon);
                        }
                    });
        }




        tv_name.setText(prefManager.getName());
        tv_phone.setText(prefManager.getPhone());
        tv_email.setText(prefManager.getEmail());
        tv_cast.setText(prefManager.getLoginType());
        tv_address.setText("");



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rel_change_password:

                Intent intent_cp = new Intent(ProfileView.this, ChangePassword.class);
                startActivity(intent_cp);

                break;
        }
    }


}
