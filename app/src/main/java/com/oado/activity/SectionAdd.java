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
import com.oado.models.SectionData;
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

public class SectionAdd extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.section_name) EditText section_name;
    @BindView(R.id.tv_button_title)
    TextView tv_button_title;

    @BindView(R.id.rel_create_section)
    RelativeLayout rel_create_section;


    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;

    String type;
    SectionData sectionData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_add);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.add_section));
        }

        prefManager = new PrefManager(this);
        validationClass = new ValidationClass(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        rel_create_section.setOnClickListener(this);

        setActionOnViews();
    }


    private void setActionOnViews(){
        type = "add";
        tv_button_title.setText(getResources().getString(R.string.add_section));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");

            if (type.matches("edit")) {

                getSupportActionBar().setTitle(getResources().getString(R.string.edit_section));

                sectionData = (SectionData) bundle.getSerializable("user_data");

                section_name.setText(sectionData.getName());
                section_name.setSelection(section_name.length());

                tv_button_title.setText(getResources().getString(R.string.edit_section));

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

            case R.id.rel_create_section:

                if (type.matches("add")){

                    if (!validationClass.validateIsEmpty(section_name, "Enter section name")){
                        return;
                    }

                    addSection();

                }else if (type.matches("edit")){

                    if (!validationClass.validateIsEmpty(section_name, "Enter section name")){
                        return;
                    }

                    updateSection();
                }


                break;
        }
    }

    public void addSection() {

            progressDialog.show();

            String url = ApiClient.add_section;
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();


            params.put(ApiClient.institute_id, prefManager.getInstitute_id());
            params.put(ApiClient.section_name, section_name.getText().toString());


            Log.d(Constants.TAG, "add_section - " + url);
            Log.d(Constants.TAG, "add_section - " + params.toString());

            int DEFAULT_TIMEOUT = 30 * 1000;
            client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
            client.post(url, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(Constants.TAG, "add_section- " + response.toString());

                    if (response != null) {
                        try {

                            int status = response.optInt("status");
                            String message = response.optString("message");

                            if (status == 1) {

                                Toasty.success(getApplicationContext(),
                                        message,
                                        Toast.LENGTH_LONG, true).show();

                               // Commons.restartActivity(SectionAdd.this);

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
                    Log.d(Commons.TAG, "add_section- " + res);
                    progressDialog.dismiss();

                    android.app.AlertDialog alert =
                            new android.app.AlertDialog.Builder(SectionAdd.this).create();
                    alert.setMessage("Server Error");
                    alert.show();

                }

            });


        }


    public void updateSection() {

        progressDialog.show();

        String url = ApiClient.update_section;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_id, sectionData.getInstitute_id());
        params.put(ApiClient.section_id, sectionData.getId());
        params.put(ApiClient.section_name, section_name.getText().toString());


        Log.d(Constants.TAG, "update_section - " + url);
        Log.d(Constants.TAG, "update_section - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "update_section- " + response.toString());

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
                Log.d(Commons.TAG, "update_section- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(SectionAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }

}
