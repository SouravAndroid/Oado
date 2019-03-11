package com.oado.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.ConnectivityReceiver;
import com.oado.utils.Constants;
import com.oado.utils.PrefManager;
import com.oado.utils.ValidationClass;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.old_password) EditText old_password;
    @BindView(R.id.new_password) EditText new_password;
    @BindView(R.id.confirm_password) EditText confirm_password;

    @BindView(R.id.rel_change_password) RelativeLayout rel_change_password;

    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Change Password");
        }


        prefManager = new PrefManager(this);

        validationClass = new ValidationClass(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        rel_change_password.setOnClickListener(this);



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

                checkValidation();

                break;
        }
    }


    private void checkValidation(){

        if (!validationClass.validateIsEmpty(old_password, "Enter Old Password")){
            return;
        }

        if (!validationClass.validatePassword1(new_password)){
            return;
        }

        if (!validationClass.validatePassword2(new_password, confirm_password)){
            return;
        }


        if (!ConnectivityReceiver.isConnected()){
            Toasty.error(getApplicationContext(),
                    "Please connect to internet",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }

        changePassword(old_password.getText().toString(),
                new_password.getText().toString());

    }



    private void changePassword(String oldPassword, String newPassword) {

        progressDialog.show();

        String url = ApiClient.change_password;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.id, prefManager.getId());
        params.put(ApiClient.oldpassword, oldPassword);
        params.put(ApiClient.password, newPassword);

        Log.d(Constants.TAG, "change_password - " + url);
        Log.d(Constants.TAG, "change_password - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "change_password- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1) {

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();

                            finish();

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
                Log.d(Commons.TAG, "change_password- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(ChangePassword.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }
}
