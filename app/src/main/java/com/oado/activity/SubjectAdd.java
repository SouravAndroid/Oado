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
import com.oado.models.SubjectData;
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

public class SubjectAdd extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.subject_name)
    EditText subject_name;
    @BindView(R.id.subject_code)
    EditText subject_code;
    @BindView(R.id.tv_button_title)
    TextView tv_button_title;
    @BindView(R.id.rel_create_subject)
    RelativeLayout rel_create_subject;

    ValidationClass validationClass;
    ProgressDialog progressDialog;
    String type;
    SubjectData subjectData;

    PrefManager prefManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_add);
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

        rel_create_subject.setOnClickListener(this);


        setActionOnViews();

    }

    private void setActionOnViews(){
        type = "add";
        tv_button_title.setText(getResources().getString(R.string.add_subject));
        getSupportActionBar().setTitle(getResources().getString(R.string.add_subject));


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");

            if (type.matches("edit")) {

                getSupportActionBar().setTitle(getResources().getString(R.string.edit_subject));

                subjectData = (SubjectData) bundle.getSerializable("user_data");

                subject_name.setText(subjectData.getSubject_name());
                subject_name.setSelection(subject_name.length());

                subject_code.setText(subjectData.getSubject_code());
                subject_code.setSelection(subject_code.length());

                tv_button_title.setText(getResources().getString(R.string.edit_subject));

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

            case R.id.rel_create_subject:

                validateOnClick();

                break;
        }
    }


    private void validateOnClick(){

        if (!validationClass.validateIsEmpty(subject_name, "Enter subject name")){
            return;
        }

        if (!validationClass.validateIsEmpty(subject_code, "Enter subject code")){
            return;
        }

        if (type.matches("add")){

            addSubject();

        }else if (type.matches("edit")){

            updateSubject();
        }


    }



    public void addSubject() {

        progressDialog.show();

        String url = ApiClient.add_subject;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.subject_name, subject_name.getText().toString());
        params.put(ApiClient.subject_code, subject_code.getText().toString());


        Log.d(Constants.TAG, "add_subject - " + url);
        Log.d(Constants.TAG, "add_subject - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_subject- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1) {

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                           // Commons.restartActivity(SubjectAdd.this);

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
                Log.d(Commons.TAG, "add_subject- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(SubjectAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }


    public void updateSubject () {

        progressDialog.show();

        String url = ApiClient.update_subject;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_id, subjectData.getInstitute_id());
        params.put(ApiClient.subject_id, subjectData.getId());
        params.put(ApiClient.subject_name, subject_name.getText().toString());
        params.put(ApiClient.subject_code, subject_code.getText().toString());


        Log.d(Constants.TAG, "update_subject - " + url);
        Log.d(Constants.TAG, "update_subject - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "update_subject- " + response.toString());

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
                Log.d(Commons.TAG, "update_subject- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(SubjectAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }


}
