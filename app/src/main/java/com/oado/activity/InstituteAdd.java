package com.oado.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.models.InstituteData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.ValidationClass;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class InstituteAdd extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.item_image) CircleImageView item_image;
    @BindView(R.id.institute_name) EditText institute_name;
    @BindView(R.id.institute_code) EditText institute_code;
    @BindView(R.id.board_name) EditText board_name;
    @BindView(R.id.institute_email) EditText institute_email;
    @BindView(R.id.institute_phone) EditText institute_phone;
    @BindView(R.id.street_name) EditText street_name;
    @BindView(R.id.city_name) EditText city_name;
    @BindView(R.id.state_name) EditText state_name;
    @BindView(R.id.country_name) EditText country_name;
    @BindView(R.id.pincode) EditText pincode;
    @BindView(R.id.institute_type) EditText institute_type;
    @BindView(R.id.radioGroup) RadioGroup radioGroup;
    @BindView(R.id.radioSchool) RadioButton radioSchool;
    @BindView(R.id.radioCoaching) RadioButton radioCoaching;
    @BindView(R.id.cb_sms_subscription) CheckBox cb_sms_subscription;
    @BindView(R.id.rel_create_institute) RelativeLayout rel_create_institute;
    @BindView(R.id.tv_button_title) TextView tv_button_title;



    private int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    File p_image = null;
    String type_institute = null;
    String sms_subcription = null;
    ValidationClass validationClass;
    ProgressDialog progressDialog;
    InstituteData instituteData;
    String type;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.institute_add);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.add_institute));
        }

        validationClass = new ValidationClass(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        item_image.setOnClickListener(this);
        rel_create_institute.setOnClickListener(this);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioSchool){

                    type_institute = radioSchool.getText().toString();

                }else if (checkedId == R.id.radioCoaching){

                    type_institute = radioCoaching.getText().toString();
                }
            }
        });





        setActionOnViews();

    }


    private void setActionOnViews(){
        type = "add";
        tv_button_title.setText(getResources().getString(R.string.add_institute));
        getSupportActionBar().setTitle(getResources().getString(R.string.add_institute));
        institute_type.setVisibility(View.GONE);
        institute_phone.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            type = bundle.getString("type");

            if (type == null){
                return;
            }

            if (type.equals("edit")){

                tv_button_title.setText(getResources().getString(R.string.edit_institute));

                getSupportActionBar().setTitle(getResources().getString(R.string.edit_institute));

                instituteData = (InstituteData) bundle.getSerializable("user_data");

                institute_phone.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });

                Picasso.with(this).load(instituteData.getImage()).placeholder(R.mipmap.no_image)
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


                institute_name.setText(instituteData.getName());
                institute_name.setSelection(institute_name.length());

                institute_code.setText(instituteData.getCode());
                institute_code.setSelection(institute_code.length());

                board_name.setText(instituteData.getBoard_name());
                board_name.setSelection(board_name.length());

                institute_phone.setText(instituteData.getMobile());
                institute_phone.setSelection(institute_phone.length());

                institute_email.setText(instituteData.getEmail());
                institute_email.setSelection(institute_email.length());

                street_name.setText(instituteData.getStreet());
                street_name.setSelection(street_name.length());

                city_name.setText(instituteData.getCity());
                city_name.setSelection(city_name.length());

                state_name.setText(instituteData.getState());
                state_name.setSelection(state_name.length());

                country_name.setText(instituteData.getCountry());
                country_name.setSelection(country_name.length());

                pincode.setText(instituteData.getPincode());
                pincode.setSelection(pincode.length());


                institute_type.setVisibility(View.GONE);

                if (instituteData.getInstitute_type().toLowerCase()
                        .matches(ApiClient.school.toLowerCase())){

                    radioSchool.setChecked(true);

                }else if (instituteData.getInstitute_type().toLowerCase()
                        .matches(ApiClient.coaching_center.toLowerCase())){

                    radioCoaching.setChecked(true);
                }


                if (instituteData.getSms_subcription().matches("Y")){
                    cb_sms_subscription.setChecked(true);
                }else {
                    cb_sms_subscription.setChecked(false);
                }


            }else if (type.matches("view")){

                tv_button_title.setText(getResources().getString(R.string.view_institute));
                rel_create_institute.setVisibility(View.GONE);
                getSupportActionBar().setTitle(getResources().getString(R.string.view_institute));

                institute_phone.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });


                instituteData = (InstituteData) bundle.getSerializable("user_data");

                Picasso.with(this).load(instituteData.getImage()).placeholder(R.mipmap.no_image)
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

                item_image.setOnClickListener(null);


                institute_name.setText("Name: "+instituteData.getName());
                institute_name.setEnabled(false);

                institute_code.setText("Code: "+instituteData.getCode());
                institute_code.setEnabled(false);

                board_name.setText("Board name: "+instituteData.getBoard_name());
                board_name.setEnabled(false);

                institute_phone.setText("Mobile: "+instituteData.getMobile());
                institute_phone.setEnabled(false);

                institute_email.setText("Email: "+instituteData.getEmail());
                institute_email.setEnabled(false);

                street_name.setText("Street: "+instituteData.getStreet());
                street_name.setEnabled(false);

                city_name.setText("City: "+instituteData.getCity());
                city_name.setEnabled(false);

                state_name.setText("State: "+instituteData.getState());
                state_name.setEnabled(false);

                country_name.setText("Country: "+instituteData.getCountry());
                country_name.setEnabled(false);

                pincode.setText("Pincode: "+instituteData.getPincode());
                pincode.setEnabled(false);


                institute_type.setVisibility(View.VISIBLE);
                institute_type.setEnabled(false);

                radioGroup.setVisibility(View.GONE);

                if (instituteData.getInstitute_type().toLowerCase()
                        .matches(ApiClient.school.toLowerCase())){

                    institute_type.setText("Type: "+
                            getResources().getString(R.string.institute_type_school));

                }else if (instituteData.getInstitute_type().toLowerCase()
                        .matches(ApiClient.coaching_center.toLowerCase())){

                    institute_type.setText("Type: "+
                            getResources().getString(R.string.institute_type_coaching));
                }



                if (instituteData.getSms_subcription().matches("Y")){
                    cb_sms_subscription.setChecked(true);
                }else {
                    cb_sms_subscription.setChecked(false);
                }

                cb_sms_subscription.setEnabled(false);


            }else if (type.matches("add")) {

                tv_button_title.setText(getResources().getString(R.string.add_institute));
                getSupportActionBar().setTitle(getResources().getString(R.string.add_institute));
                institute_type.setVisibility(View.GONE);


            }



        }



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
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.item_image:

                selectImage();

                break;

            case R.id.rel_create_institute:

                checkValidation();

                break;


        }

    }

    public void checkValidation(){

        if (!validationClass.validateIsEmpty(institute_name, "Enter institute name")){
            return;
        }

        if (!validationClass.validateIsEmpty(institute_code, "Enter institute code")){
            return;
        }

        if (!validationClass.validateIsEmpty(board_name, "Enter board name")){
            return;
        }

        if (!validationClass.validateMobileNo(institute_phone)){
            return;
        }


        if (!validationClass.validateEmail(institute_email)){
            return;
        }

        if (!validationClass.validateIsEmpty(street_name, "Enter street name")){
            return;
        }

        if (!validationClass.validateIsEmpty(city_name, "Enter city name")){
            return;
        }

        if (!validationClass.validateIsEmpty(state_name, "Enter state name")){
            return;
        }

        if (!validationClass.validateIsEmpty(country_name, "Enter country name")){
            return;
        }

        if (!validationClass.validateIsEmpty(pincode, "Enter pincode")){
            return;
        }


        if (radioSchool.isChecked()){
            type_institute = ApiClient.school;
        }else if (radioCoaching.isChecked()){
            type_institute = ApiClient.coaching_center;
        }


        if (type_institute == null){
            Toasty.info(getApplicationContext(),
                    "Select type of your institute",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }


        sms_subcription = "N";
        if (cb_sms_subscription.isChecked()){
            sms_subcription = "Y";
        }else {
            sms_subcription = "N";
        }


        if (type.equals("add")){

            createInstitute();

        }else if (type.equals("edit")){

            editInstitute();

        }


    }



    public void createInstitute(){

        progressDialog.show();

        String url = ApiClient.add_institute;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_name, institute_name.getText().toString());
        params.put(ApiClient.institute_code, institute_code.getText().toString());
        params.put(ApiClient.mobile_no, institute_phone.getText().toString().trim());
        params.put(ApiClient.email_id, institute_email.getText().toString().trim());
        params.put(ApiClient.type_of_center, type_institute);
        params.put(ApiClient.boardname, board_name.getText().toString());
        params.put(ApiClient.address, street_name.getText().toString());
        params.put(ApiClient.city, city_name.getText().toString());
        params.put(ApiClient.state, state_name.getText().toString());
        params.put(ApiClient.country, country_name.getText().toString());
        params.put(ApiClient.pincode, pincode.getText().toString());
        params.put(ApiClient.login_type, ApiClient.institute);
        params.put(ApiClient.sms_subscription, sms_subcription);

        try{

            params.put(ApiClient.image, p_image);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(Constants.TAG , "add_institute - " + url);
        Log.d(Constants.TAG , "add_institute - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_institute- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                            Commons.restartActivity(InstituteAdd.this);

                        }else {

                            Toasty.error(getApplicationContext(),
                                    "Some error occurred. Try Again",
                                    Toast.LENGTH_SHORT, true).show();

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "add_institute- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(InstituteAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }



    private void editInstitute(){

        progressDialog.show();

        String url = ApiClient.update_institute;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_id, instituteData.getId());
        params.put(ApiClient.institute_name, institute_name.getText().toString());
        params.put(ApiClient.institute_code, institute_code.getText().toString());
        params.put(ApiClient.mobile_no, institute_phone.getText().toString().trim());
        params.put(ApiClient.email_id, institute_email.getText().toString().trim());
        params.put(ApiClient.type_of_center, type_institute);
        params.put(ApiClient.boardname, board_name.getText().toString());
        params.put(ApiClient.address, street_name.getText().toString());
        params.put(ApiClient.city, city_name.getText().toString());
        params.put(ApiClient.state, state_name.getText().toString());
        params.put(ApiClient.country, country_name.getText().toString());
        params.put(ApiClient.pincode, pincode.getText().toString());
        params.put(ApiClient.login_type, ApiClient.institute);
        params.put(ApiClient.sms_subscription, sms_subcription);

        try{

            params.put(ApiClient.image, p_image);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(Constants.TAG , "update_institute - " + url);
        Log.d(Constants.TAG , "update_institute - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "update_institute- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                        }else {

                            Toasty.error(getApplicationContext(),
                                    "Some error occurred. Try Again",
                                    Toast.LENGTH_SHORT, true).show();

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "update_institute- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(InstituteAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });






    }








    public void selectImage() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(InstituteAdd.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_picture_select, null);
        dialogBuilder.setView(dialogView);

        ImageView iv_gallery = (ImageView) dialogView.findViewById(R.id.iv_gallery);
        ImageView iv_camera = (ImageView) dialogView.findViewById(R.id.iv_camera);

        final AlertDialog alertDialog = dialogBuilder.create();

        iv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        PICK_IMAGE_REQUEST);

                alertDialog.dismiss();

            }
        });


        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

                alertDialog.dismiss();

            }
        });


        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();
            //p_image = new File(getRealPathFromURI(uri));

            Bitmap bitmap = null;

            Log.d(Constants.TAG , "PICK_IMAGE_REQUEST - "+uri);
            Log.d(Constants.TAG , "p_image - "+p_image);



            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                item_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }




            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            try {

                String path = Environment.getExternalStorageDirectory()+File.separator;

                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image = file;

                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            Log.d(Constants.TAG , "PICK_IMAGE_REQUEST - "+data.getExtras().get("data"));

            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            try {

                item_image.setImageBitmap(photo);

                String path = Environment.getExternalStorageDirectory()+File.separator;

                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image = file;

                    outFile = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 80, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        try {
            Cursor cursor = getApplicationContext().getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx); // Exception raised HERE
                cursor.close(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(InstituteAdd.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(InstituteAdd.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(InstituteAdd.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) InstituteAdd.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        } else {

            selectImage();

        }


        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                    //list is still empty

                    selectImage();

                } else {

                    checkPermission();
                    // Permission Denied

                }
                break;
        }
    }



}
