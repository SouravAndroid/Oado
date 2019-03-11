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
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.oado.adapters.MySpinnerAdapter;
import com.oado.adapters.SubjectSpinnerAdapter;
import com.oado.models.GuardianData;
import com.oado.models.StudentData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.PrefManager;
import com.oado.utils.ValidationClass;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class GuardianView extends AppCompatActivity implements View.OnClickListener {

    // guardian
    @BindView(R.id.item_image_guardian) CircleImageView item_image_guardian;
    @BindView(R.id.guardian_name) EditText guardian_name;
    @BindView(R.id.guardian_relation) EditText guardian_relation;
    @BindView(R.id.spinner_relation_guardian) Spinner spinner_relation_guardian;
    @BindView(R.id.phone_no_guardian) EditText phone_no_guardian;
    @BindView(R.id.email_guardian) EditText email_guardian;
    @BindView(R.id.street_name_guardian) EditText street_name_guardian;
    @BindView(R.id.city_name_guardian) EditText city_name_guardian;
    @BindView(R.id.state_name_guardian) EditText state_name_guardian;
    @BindView(R.id.country_name_guardian) EditText country_name_guardian;
    @BindView(R.id.pincode_guardian) EditText pincode_guardian;
    @BindView(R.id.card_view_guardian) CardView card_view_guardian;
    @BindView(R.id.tv_button_title) TextView tv_button_title;
    @BindView(R.id.rel_action_guardian) RelativeLayout rel_action_guardian;
    @BindView(R.id.rel_relation) RelativeLayout rel_relation;


    private int PICK_IMAGE_REQUEST_GUARDIAN = 2;
    private static final int CAMERA_REQUEST_GUARDIAN = 44;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    File p_image_guardian = null;
    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;

    String type, s_guardian_relation;
    ArrayList<HashMap<String, String>> list_Relation;
    MySpinnerAdapter spinnerAdapter;
    String click_on_image;
    GuardianData guardianData;

    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_view);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        validationClass = new ValidationClass(this);
        prefManager = new PrefManager(this);
        globalClass = (GlobalClass) getApplicationContext();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);



        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        rel_action_guardian.setOnClickListener(this);
        item_image_guardian.setOnClickListener(this);

        type = "add";

        setActionOnViews();

        setGuardianRelations();

        clickOnViews();


    }

    public void clickOnViews(){




        spinner_relation_guardian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    s_guardian_relation = list_Relation.get(position).get("name");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void setActionOnViews(){
        type = "add";
        tv_button_title.setText(getResources().getString(R.string.add_student));
        getSupportActionBar().setTitle(getResources().getString(R.string.add_student));
        phone_no_guardian.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            type = bundle.getString("type");

            if (type.matches("edit")){

                tv_button_title.setText(getResources().getString(R.string.edit_guardian));
                getSupportActionBar().setTitle(getResources().getString(R.string.edit_guardian));

                rel_relation.setVisibility(View.VISIBLE);
                guardian_relation.setVisibility(View.GONE);

                guardianData = (GuardianData) bundle.getSerializable("user_data");

                Picasso.with(this).load(guardianData.getImage()).placeholder(R.mipmap.no_image)
                        .into(item_image_guardian, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                        R.mipmap.no_image);

                                item_image_guardian.setImageBitmap(icon);
                            }
                        });


                guardian_name.setText(guardianData.getName());
                guardian_name.setSelection(guardian_name.length());

                phone_no_guardian.setText(guardianData.getMobile());
                phone_no_guardian.setSelection(phone_no_guardian.length());

                email_guardian.setText(guardianData.getEmail());
                email_guardian.setSelection(email_guardian.length());

                street_name_guardian.setText(guardianData.getStreet());
                street_name_guardian.setSelection(street_name_guardian.length());

                city_name_guardian.setText(guardianData.getCity());
                city_name_guardian.setSelection(city_name_guardian.length());

                state_name_guardian.setText(guardianData.getState());
                state_name_guardian.setSelection(state_name_guardian.length());

                country_name_guardian.setText(guardianData.getCountry());
                country_name_guardian.setSelection(country_name_guardian.length());

                pincode_guardian.setText(guardianData.getPincode());
                pincode_guardian.setSelection(pincode_guardian.length());




            }else if (type.matches("view")){

                tv_button_title.setText(getResources().getString(R.string.view_guardian));
                rel_action_guardian.setVisibility(View.GONE);
                getSupportActionBar().setTitle(getResources().getString(R.string.view_guardian));

                rel_relation.setVisibility(View.GONE);
                guardian_relation.setVisibility(View.VISIBLE);


                phone_no_guardian.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });

                guardianData = (GuardianData) bundle.getSerializable("user_data");

                Picasso.with(this).load(guardianData.getImage()).placeholder(R.mipmap.no_image)
                        .into(item_image_guardian, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                        R.mipmap.no_image);

                                item_image_guardian.setImageBitmap(icon);
                            }
                        });

                item_image_guardian.setOnClickListener(null);



                guardian_name.setText("Name: "+guardianData.getName());
                guardian_name.setEnabled(false);

                guardian_relation.setText("Relation: "+guardianData.getRelation());
                guardian_relation.setEnabled(false);

                phone_no_guardian.setText("Mobile: "+guardianData.getMobile());
                phone_no_guardian.setEnabled(false);

                email_guardian.setText("Email: "+guardianData.getEmail());
                email_guardian.setEnabled(false);

                street_name_guardian.setText("Street: "+guardianData.getStreet());
                street_name_guardian.setEnabled(false);

                city_name_guardian.setText("City: "+guardianData.getCity());
                city_name_guardian.setEnabled(false);

                state_name_guardian.setText("State: "+guardianData.getState());
                state_name_guardian.setEnabled(false);

                country_name_guardian.setText("Country: "+guardianData.getCountry());
                country_name_guardian.setEnabled(false);

                pincode_guardian.setText("Pincode: "+guardianData.getPincode());
                pincode_guardian.setEnabled(false);




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
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.item_image_guardian:

                selectImage();

                break;

            case R.id.rel_action_guardian:

                checkValidation();

                break;

        }

    }

    private void setGuardianRelations(){

        list_Relation = new ArrayList<>();
        HashMap<String, String> hashMap;
        int postion = 0;
        String[] relation_array = getResources().getStringArray(R.array.relation_array);
        for (int i = 0; i < relation_array.length; i++){
            hashMap = new HashMap<>();

            hashMap.put("name", relation_array[i]);

            list_Relation.add(hashMap);


            if (type.matches("edit")){
                if (relation_array[i].matches(guardianData.getRelation())){
                    postion = i;
                }
            }
        }

        MySpinnerAdapter adapter = new MySpinnerAdapter(this, list_Relation);
        spinner_relation_guardian.setAdapter(adapter);

        if (type.matches("edit")){
            spinner_relation_guardian.setSelection(postion);
        }




    }



    public void selectImage() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GuardianView.this);
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
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        PICK_IMAGE_REQUEST_GUARDIAN);



                alertDialog.dismiss();

            }
        });


        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_GUARDIAN);

                alertDialog.dismiss();

            }
        });


        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_GUARDIAN && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();
            //p_image = new File(getRealPathFromURI(uri));

            Bitmap bitmap = null;

            Log.d(Constants.TAG , "PICK_IMAGE_REQUEST - "+uri);
            Log.d(Constants.TAG , "p_image - "+p_image_guardian);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                item_image_guardian.setImageBitmap(bitmap);

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

                    p_image_guardian = file;

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

        }else if (requestCode == CAMERA_REQUEST_GUARDIAN && resultCode == Activity.RESULT_OK) {

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

                item_image_guardian.setImageBitmap(photo);

                String path = Environment.getExternalStorageDirectory()+File.separator;

                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image_guardian = file;

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

        if (ContextCompat.checkSelfPermission(GuardianView.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(GuardianView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(GuardianView.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) GuardianView.this, permissionsList.toArray(new String[permissionsList.size()]),
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





    /////////////////

    private void checkValidation(){


        /// guardian ...



        if (!validationClass.validateIsEmpty(guardian_name, "Enter guardian name")){
            return;
        }

        if (s_guardian_relation == null){
            Toasty.info(getApplicationContext(),
                    "Select relation",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }

        if (!validationClass.validateMobileNo(phone_no_guardian)){
            return;
        }

        if (!validationClass.validateEmail(email_guardian)){
            return;
        }





        if (type.matches("edit")){

            updateGuardian();

        }

    }


    public void updateGuardian(){

        progressDialog.show();

        String url = ApiClient.update_guardian;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.guardian_id, guardianData.getId());
        params.put(ApiClient.guardian_name, guardian_name.getText().toString());
        params.put(ApiClient.relation, s_guardian_relation);
        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.mobile_no, phone_no_guardian.getText().toString().trim());
        params.put(ApiClient.email_id, email_guardian.getText().toString().trim());
        params.put(ApiClient.address, street_name_guardian.getText().toString());
        params.put(ApiClient.city, city_name_guardian.getText().toString());
        params.put(ApiClient.state, state_name_guardian.getText().toString());
        params.put(ApiClient.country, country_name_guardian.getText().toString());
        params.put(ApiClient.pincode, pincode_guardian.getText().toString());

        try{

            params.put(ApiClient.image, p_image_guardian);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(Constants.TAG , "update_guardian - " + url);
        Log.d(Constants.TAG , "update_guardian - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "update_guardian- " + response.toString());

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
                Log.d(Commons.TAG, "update_guardian- " + res);
                progressDialog.dismiss();

                AlertDialog alert =
                        new AlertDialog.Builder(GuardianView.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }






}
