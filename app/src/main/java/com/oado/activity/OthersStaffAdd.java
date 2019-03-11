package com.oado.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.fragments.OthersStaff;
import com.oado.models.StaffData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.PrefManager;
import com.oado.utils.ValidationClass;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class OthersStaffAdd extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.item_image) CircleImageView item_image;
    @BindView(R.id.staff_name) EditText staff_name;
    @BindView(R.id.phone_no) EditText phone_no;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.street_name) EditText street_name;
    @BindView(R.id.city_name) EditText city_name;
    @BindView(R.id.state_name) EditText state_name;
    @BindView(R.id.country_name) EditText country_name;
    @BindView(R.id.pincode) EditText pincode;
    @BindView(R.id.edt_dob) EditText edt_dob;
    @BindView(R.id.iv_calender) ImageView iv_calender;
    @BindView(R.id.radioGroup) RadioGroup radioGroup;
    @BindView(R.id.radio_male) RadioButton radio_male;
    @BindView(R.id.radio_female) RadioButton radio_female;
    @BindView(R.id.rel_create_staff) RelativeLayout rel_create_staff;
    @BindView(R.id.tv_gender_title) TextView tv_gender_title;
    @BindView(R.id.tv_button_title) TextView tv_button_title;


    private int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    File p_image = null;
    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    StaffData staffData;

    String type, gender;


    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.others_staff_add);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        globalClass = (GlobalClass) getApplicationContext();
        prefManager = new PrefManager(this);
        validationClass = new ValidationClass(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        rel_create_staff.setOnClickListener(this);
        item_image.setOnClickListener(this);


        type = "add";


        clickOnView();

        setActionOnViews();
    }

    public void clickOnView(){

        iv_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(OthersStaffAdd.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


    }



    private void setActionOnViews(){
        type = "add";
        tv_button_title.setText(getResources().getString(R.string.add_staff));
        getSupportActionBar().setTitle(getResources().getString(R.string.add_staff));
        phone_no.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            type = bundle.getString("type");

            if (type.matches("edit")){

                tv_button_title.setText(getResources().getString(R.string.edit_staff));

                getSupportActionBar().setTitle(getResources().getString(R.string.edit_staff));

                staffData = (StaffData) bundle.getSerializable("user_data");

                phone_no.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });

                Picasso.with(this).load(staffData.getImage()).placeholder(R.mipmap.no_image)
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


                staff_name.setText(staffData.getName());
                staff_name.setSelection(staff_name.length());

                phone_no.setText(staffData.getMobile());
                phone_no.setSelection(phone_no.length());

                email.setText(staffData.getEmail());
                email.setSelection(email.length());

                street_name.setText(staffData.getStreet());
                street_name.setSelection(street_name.length());

                city_name.setText(staffData.getCity());
                city_name.setSelection(city_name.length());

                state_name.setText(staffData.getState());
                state_name.setSelection(state_name.length());

                country_name.setText(staffData.getCountry());
                country_name.setSelection(country_name.length());

                pincode.setText(staffData.getPincode());
                pincode.setSelection(pincode.length());


                edt_dob.setText(staffData.getDob());


                if (staffData.getGender().toLowerCase()
                        .matches(radio_male.getText().toString().toLowerCase())){

                    radio_male.setChecked(true);

                }else if (staffData.getGender().toLowerCase()
                        .matches(radio_female.getText().toString().toLowerCase())){

                    radio_female.setChecked(true);
                }



            }else if (type.matches("view")){

                tv_button_title.setText(getResources().getString(R.string.view_staff));
                rel_create_staff.setVisibility(View.GONE);
                getSupportActionBar().setTitle(getResources().getString(R.string.view_staff));

                staffData = (StaffData) bundle.getSerializable("user_data");

                phone_no.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });

                Picasso.with(this).load(staffData.getImage()).placeholder(R.mipmap.no_image)
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


                item_image.setOnClickListener(null);


                staff_name.setText("Name: "+staffData.getName());
                staff_name.setEnabled(false);

                phone_no.setText("Mobile: "+staffData.getMobile());
                phone_no.setEnabled(false);

                email.setText("Email: "+staffData.getEmail());
                email.setEnabled(false);

                street_name.setText("Street: "+staffData.getStreet());
                street_name.setEnabled(false);

                city_name.setText("City: "+staffData.getCity());
                city_name.setEnabled(false);

                state_name.setText("State: "+staffData.getState());
                state_name.setEnabled(false);

                country_name.setText("Country: "+staffData.getCountry());
                country_name.setEnabled(false);

                pincode.setText("Pincode: "+staffData.getPincode());
                pincode.setEnabled(false);


                edt_dob.setText("DOB: "+staffData.getDob());
                iv_calender.setVisibility(View.INVISIBLE);


                radioGroup.setVisibility(View.INVISIBLE);
                if (staffData.getGender().toLowerCase()
                        .matches(radio_male.getText().toString().toLowerCase())){

                    radio_male.setChecked(true);

                    tv_gender_title.setText("Gender: Male");

                }else if (staffData.getGender().toLowerCase()
                        .matches(radio_female.getText().toString().toLowerCase())){

                    radio_female.setChecked(true);

                    tv_gender_title.setText("Gender: Female");
                }



            }else if (type.matches("add")) {

                tv_button_title.setText(getResources().getString(R.string.add_staff));
                getSupportActionBar().setTitle(getResources().getString(R.string.add_staff));


            }



        }



    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edt_dob.setText(sdf.format(myCalendar.getTime()));
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
        switch (v.getId()){
            case R.id.rel_create_staff:

                checkValidation();

                break;

            case R.id.item_image:

                checkPermission();

                break;

        }
    }


    private void checkValidation(){

        if (!validationClass.validateIsEmpty(staff_name, "Enter staff name")){
            return;
        }


        if (!validationClass.validateIsEmpty(edt_dob, "Set date of birth")){
            return;
        }

        if (!validationClass.validateMobileNo(phone_no)){
            return;
        }

        if (!validationClass.validateEmail(email)){
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

        if (radio_male.isChecked()){
            gender = radio_male.getText().toString().toLowerCase();
        }

        if (radio_female.isChecked()){
            gender = radio_female.getText().toString().toLowerCase();
        }


        if (type.matches("add")){

            createStaff();

        }else if (type.matches("edit")){

            editStaff();

        }

    }


    public void createStaff(){

        progressDialog.show();

        String url = ApiClient.add_staff;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.staff_name, staff_name.getText().toString());
        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.gender, gender);
        params.put(ApiClient.dob, edt_dob.getText().toString());
        params.put(ApiClient.mobile_no, phone_no.getText().toString().trim());
        params.put(ApiClient.email_id, email.getText().toString().trim());
        params.put(ApiClient.address, street_name.getText().toString());
        params.put(ApiClient.city, city_name.getText().toString());
        params.put(ApiClient.state, state_name.getText().toString());
        params.put(ApiClient.country, country_name.getText().toString());
        params.put(ApiClient.pincode, pincode.getText().toString());
        params.put(ApiClient.login_type, ApiClient.other_staff);

        try{

            params.put(ApiClient.image, p_image);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(Constants.TAG , "add_staff - " + url);
        Log.d(Constants.TAG , "add_staff - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_staff- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                           // Commons.restartActivity(OthersStaffAdd.this);

                            finish();

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
                Log.d(Commons.TAG, "add_staff- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(OthersStaffAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }


    public void editStaff(){

        progressDialog.show();

        String url = ApiClient.update_staff;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.staff_id, staffData.getId());
        params.put(ApiClient.staff_name, staff_name.getText().toString());
        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.gender, gender);
        params.put(ApiClient.dob, edt_dob.getText().toString());
        params.put(ApiClient.mobile_no, phone_no.getText().toString().trim());
        params.put(ApiClient.email_id, email.getText().toString().trim());
        params.put(ApiClient.address, street_name.getText().toString());
        params.put(ApiClient.city, city_name.getText().toString());
        params.put(ApiClient.state, state_name.getText().toString());
        params.put(ApiClient.country, country_name.getText().toString());
        params.put(ApiClient.pincode, pincode.getText().toString());

        try{

            params.put(ApiClient.image, p_image);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(Constants.TAG , "update_staff - " + url);
        Log.d(Constants.TAG , "update_staff - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "update_staff- " + response.toString());

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
                Log.d(Commons.TAG, "update_staff- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(OthersStaffAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }








    public void selectImage() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OthersStaffAdd.this);
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

        if (ContextCompat.checkSelfPermission(OthersStaffAdd.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(OthersStaffAdd.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(OthersStaffAdd.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) OthersStaffAdd.this, permissionsList.toArray(new String[permissionsList.size()]),
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
