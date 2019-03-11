package com.oado.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.ConnectivityReceiver;
import com.oado.utils.Constants;
import com.oado.utils.PrefManager;
import com.oado.utils.StaticText;
import com.oado.utils.ValidationClass;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.edt_email) EditText edt_email;
    @BindView(R.id.edt_password) EditText edt_password;
    @BindView(R.id.view_password) ImageView view_password;
    @BindView(R.id.sign_in) Button sign_in;
    @BindView(R.id.forgot_password) TextView forgot_password;


    boolean showPassword = false;

    private PrefManager prefManager;
    private ValidationClass validationClass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_login);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        initViews();


        requestPermission();

    }


    public void initViews(){

        prefManager = new PrefManager(this);
        validationClass = new ValidationClass(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        view_password.setImageResource(R.mipmap.eye_gray);

        view_password.setOnClickListener(this);
        sign_in.setOnClickListener(this);
        forgot_password.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.view_password:

                if (showPassword){
                    edt_password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPassword = false;
                    view_password.setImageResource(R.mipmap.eye_gray);
                }else {
                    edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPassword = true;
                    view_password.setImageResource(R.mipmap.eye_green);
                }

                edt_password.setSelection(edt_password.length());


                break;

            case R.id.sign_in:

                checkValidation();

                break;


            case R.id.forgot_password:

                forgotPasswordDialog();

                break;


            default:
                break;


        }

    }


    private void checkValidation(){

        if (!validationClass.validateEmail(edt_email)){
            return;
        }

        if (!validationClass.validatePassword1(edt_password)){
            return;
        }

        if (!ConnectivityReceiver.isConnected()){
            Toasty.error(getApplicationContext(),
                    "Please connect to internet",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }

        loginCall();

    }



    private void loginCall() {

        progressDialog.show();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();


        String url = ApiClient.user_login;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.email, edt_email.getText().toString().trim());
        params.put(ApiClient.password, edt_password.getText().toString());
        params.put("device_type", "android");
        params.put("fcm_reg_token", refreshedToken);


        Log.d(Constants.TAG, "user_login - " + url);
        Log.d(Constants.TAG, "user_login - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "user_login- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1) {

                            JSONObject user_info = response.getJSONObject("user_info");

                            String id = user_info.optString("id");
                            String name = user_info.optString("name");
                            String phone_no = user_info.optString("phone_no");
                            String image = user_info.optString("image");
                            String email = user_info.optString("email");
                            String address = user_info.optString("address");
                            String pincode = user_info.optString("pincode");
                            String city = user_info.optString("city");
                            String state = user_info.optString("state");
                            String country = user_info.optString("country");
                            String login_type = user_info.optString("login_type");
                            String institute_type = user_info.optString("institute_type");
                            String institute_id = user_info.optString("institute_id");
                            String guardian_id = user_info.optString("guardian_id");
                            String student_id = user_info.optString("student_id");


                            prefManager.setUserData(id, name, email, phone_no, image,
                                    address, city, state, country, pincode, login_type);

                            prefManager.setInstituteType(institute_type);
                            prefManager.setInstitute_id(institute_id);


                            prefManager.setLogin(true);


                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_LONG, true).show();


                            if (login_type.equals(ApiClient.institute)){

                                prefManager.setInstitute_id(id);

                            }else if (login_type.equals(ApiClient.guardian)){

                                String class_id = user_info.optString("class_id");
                                String section_id = user_info.optString("section_id");


                                prefManager.setGuardian_id(id);
                                prefManager.setStudent_id(student_id);
                                prefManager.setClass_id(class_id);
                                prefManager.setSection_id(section_id);

                            }else if (login_type.equals(ApiClient.student)){

                                String class_id = user_info.optString("class_id");
                                String section_id = user_info.optString("section_id");

                                prefManager.setGuardian_id(guardian_id);
                                prefManager.setStudent_id(id);
                                prefManager.setClass_id(class_id);
                                prefManager.setSection_id(section_id);
                            }



                            progressDialog.dismiss();

                            Intent intent = new Intent(Login.this, DrawerActivity.class);
                            startActivity(intent);
                            finish();

                        } else {

                            Toasty.error(getApplicationContext(),
                                    "You have entered wrong credential.",
                                    Toast.LENGTH_LONG, true).show();

                            progressDialog.dismiss();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "user_login- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(Login.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }







    private static final int PERMISSION_REQUEST_CODE = 1;
    private void requestPermission(){

        ActivityCompat.requestPermissions((Activity)Login.this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(StaticText.TAG, "PERMISSION_GRANTED >>>>");



                } else {
                    //code for deny

                    requestPermission();
                }
                break;
        }
    }


    public void forgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogview = inflater.inflate(R.layout.forgot_password,null);
        builder.setView(dialogview);
        builder.setCancelable(false);

        final EditText edt_email = dialogview.findViewById(R.id.edt_email);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!validationClass.validateEmail(edt_email)){
                    return;
                }

                forgotPassword(edt_email.getText().toString());


            }
        });
        builder.setNegativeButton("Cancel", new     DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog b = builder.create();
        b.show();

    }

    private void forgotPassword(String email) {

        progressDialog.show();

        String url = ApiClient.forget_password;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.email, email);

        Log.d(Constants.TAG, "forget_password - " + url);
        Log.d(Constants.TAG, "forget_password - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "forget_password- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1) {

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();

                        } else {

                            Toasty.error(getApplicationContext(),
                                    "Some error occurred. Try Again",
                                    Toast.LENGTH_SHORT, true).show();

                            progressDialog.dismiss();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "forget_password- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(Login.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }



}
