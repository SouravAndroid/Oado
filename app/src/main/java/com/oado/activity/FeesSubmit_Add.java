package com.oado.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.FeesSubmitListAdapter;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.models.ClassData;
import com.oado.models.StudentData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.PrefManager;
import com.oado.utils.ValidationClass;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class FeesSubmit_Add extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.item_image) CircleImageView item_image;
    @BindView(R.id.tv_name) TextView tv_name;
    @BindView(R.id.textView2) TextView textView2;
    @BindView(R.id.textView3) TextView textView3;
    @BindView(R.id.spinner_month) Spinner spinner_month;
    @BindView(R.id.recycler_view) RecyclerView recycler_view;
    @BindView(R.id.tv_total_pay_amt) TextView tv_total_pay_amt;
    @BindView(R.id.spinner_year) Spinner spinner_year;
    @BindView(R.id.rel_submit_fees) RelativeLayout rel_submit_fees;


    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;

    String type;
    StudentData studentData;

    String month_name, year_name, class_name, section_name;

    ArrayList<HashMap<String, String>> feesList;
    FeesSubmitListAdapter feesSubmitListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fees_submit_add);
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


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        rel_submit_fees.setOnClickListener(this);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            studentData = (StudentData) bundle.getSerializable("data");
            class_name = bundle.getString("class_name");
            section_name = bundle.getString("section_name");

            tv_name.setText(studentData.getName());
            textView2.setText(class_name + " / " +section_name);
            textView3.setText("Roll No: "+studentData.getRoll_no());

            Picasso.with(getApplicationContext()).load(studentData.getImage()).placeholder(R.mipmap.no_image)
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


            get_fees_list_student();
        }



        setMonthData();

    }


    private void setMonthData(){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
       // tv_year.setText("Year: "+year);
        final ArrayList<HashMap<String, String>> listYear = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("name", "Select Year");
        listYear.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", String.valueOf(year - 1));
        listYear.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", String.valueOf(year));
        listYear.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", String.valueOf(year + 1));
        listYear.add(hashMap);


        MySpinnerAdapter mySpinnerAdapter = new MySpinnerAdapter(FeesSubmit_Add.this,
                listYear);
        spinner_year.setAdapter(mySpinnerAdapter);








        final ArrayList<HashMap<String, String>> listMonth = new ArrayList<>();
        hashMap = new HashMap<>();

        hashMap.put("name", "Select Month");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "January");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "February");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "March");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "April");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "May");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "June");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "July");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "August");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "September");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "October");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "November");
        listMonth.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "December");
        listMonth.add(hashMap);


        mySpinnerAdapter = new MySpinnerAdapter(FeesSubmit_Add.this,
                listMonth);
        spinner_month.setAdapter(mySpinnerAdapter);


        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    month_name = listMonth.get(position).get("name");
                }else {
                    month_name = null;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    year_name = listYear.get(position).get("name");
                }else {
                    year_name = null;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

            case R.id.rel_submit_fees:

                if (month_name == null){
                    Toasty.error(getApplicationContext(),
                            "Select month",
                            Toast.LENGTH_SHORT, true).show();

                    return;
                }

                if (year_name == null){
                    Toasty.error(getApplicationContext(),
                            "Select year",
                            Toast.LENGTH_SHORT, true).show();

                    return;
                }

                if (Float.parseFloat(tv_total_pay_amt.getText().toString()) == 0){
                    Toasty.error(getApplicationContext(),
                            "Pay amount 0.00.\nNothing to pay.",
                            Toast.LENGTH_SHORT, true).show();

                    return;
                }

                add_student_fees();


                break;
        }
    }

    public void get_fees_list_student() {

        feesList = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_fees_list_student;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.student_id, studentData.getId());


        Log.d(Constants.TAG, "get_fees_list_student - " + url);
        Log.d(Constants.TAG, "get_fees_list_student - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_fees_list_student- " + response.toString());

                if (response != null) {
                    try {

                        HashMap<String, String> hashMap;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        float total_amt = 0;

                        if (status == 1) {

                            JSONArray info = response.getJSONArray("info");
                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                hashMap = new HashMap<>();

                                hashMap.put("fees_amount", object.optString("fees_amount"));
                                hashMap.put("class_name", object.optString("class_name"));
                                hashMap.put("subject_name", object.optString("subject_name"));
                                hashMap.put("subject_id", object.optString("subject_id"));
                                hashMap.put("class_id", object.optString("class_id"));

                                feesList.add(hashMap);


                                if (!object.optString("fees_amount").isEmpty()){

                                    total_amt = total_amt +
                                            Float.parseFloat(object.optString("fees_amount"));
                                }

                            }


                            feesSubmitListAdapter = new FeesSubmitListAdapter(FeesSubmit_Add.this,
                                    feesList, tv_total_pay_amt);
                            recycler_view.setAdapter(feesSubmitListAdapter);


                            DecimalFormat precision = new DecimalFormat("0.00");
                            tv_total_pay_amt.setText(precision.format(total_amt));

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
                Log.d(Commons.TAG, "get_fees_list_student- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(FeesSubmit_Add.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }


    public void add_student_fees() {

        progressDialog.show();

        String url = ApiClient.add_student_fees;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.student_id, studentData.getId());
        params.put(ApiClient.type_of_center, prefManager.getInstituteType());
        params.put(ApiClient.fees_arr, getFeesJson());


        Log.d(Constants.TAG, "add_student_fees - " + url);
        Log.d(Constants.TAG, "add_student_fees - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_student_fees- " + response.toString());

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

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "add_student_fees- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(FeesSubmit_Add.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }

    private String getFeesJson(){
        String json = "";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        try {

            if (feesSubmitListAdapter != null){
                ArrayList<HashMap<String, String>> list =
                        feesSubmitListAdapter.getSelectedList();

                for (int i = 0; i < list.size(); i++){

                    HashMap<String, String> hashMap = list.get(i);

                    jsonObject = new JSONObject();

                    if (prefManager.getInstituteType().equals(ApiClient.coaching_center)){
                        jsonObject.put("subject_id", hashMap.get("subject_id"));
                    }else {
                        jsonObject.put("class_id", hashMap.get("class_id"));
                    }

                    jsonObject.put("total_amount", hashMap.get("fees_amount"));
                    jsonObject.put("pay_amount", hashMap.get("fees_amount"));
                    jsonObject.put("pay_date", getCurrentDate());
                    jsonObject.put("month_name", month_name);


                    jsonArray.put(jsonObject);

                }
            }

            json = jsonArray.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }



    public static String getCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }
}
