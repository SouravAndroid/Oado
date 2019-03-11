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
import com.oado.models.ClassData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.PrefManager;
import com.oado.utils.ValidationClass;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ClassAdd extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.class_name) EditText class_name;
    @BindView(R.id.tv_button_title)
    TextView tv_button_title;

    @BindView(R.id.rel_create_class)
    RelativeLayout rel_create_class;

    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;

    String type;
    ClassData classData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_add);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        prefManager = new PrefManager(this);
        validationClass = new ValidationClass(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        rel_create_class.setOnClickListener(this);


        setActionOnViews();

    }

    private void setActionOnViews(){
        type = "add";
        tv_button_title.setText(getResources().getString(R.string.add_class));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");

            if (type == null){
                return;
            }

            if (type.equals("edit")) {

                getSupportActionBar().setTitle(getResources().getString(R.string.edit_class));

                classData = (ClassData) bundle.getSerializable("user_data");

                class_name.setText(classData.getName());
                class_name.setSelection(class_name.length());

                tv_button_title.setText(getResources().getString(R.string.edit_class));

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

        switch (v.getId()) {

            case R.id.rel_create_class:

                if (type.equals("add")){

                    if (!validationClass.validateIsEmpty(class_name, "Enter class name")){
                        return;
                    }

                    addClass();

                }else if (type.equals("edit")){

                    if (!validationClass.validateIsEmpty(class_name, "Enter class name")){
                        return;
                    }

                    updateClass();
                }


                break;
        }
    }

    public void addClass() {

        progressDialog.show();

        String url = ApiClient.add_class;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.class_name, class_name.getText().toString());


        Log.d(Constants.TAG, "add_class - " + url);
        Log.d(Constants.TAG, "add_class - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_class- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1) {

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                           // Commons.restartActivity(ClassAdd.this);

                            finish();

                        } else {

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
                Log.d(Commons.TAG, "add_class- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(ClassAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }


    public void updateClass() {

        progressDialog.show();

        String url = ApiClient.update_class;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_id, classData.getInstitute_id());
        params.put(ApiClient.class_id, classData.getId());
        params.put(ApiClient.class_name, class_name.getText().toString());


        Log.d(Constants.TAG, "update_class - " + url);
        Log.d(Constants.TAG, "update_class - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "update_class- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1) {

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_LONG, true).show();


                        } else {

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
                Log.d(Commons.TAG, "update_class- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(ClassAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }


}
