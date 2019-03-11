package com.oado.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.FeesPaidReportListAdapter;
import com.oado.models.FeesAmounts;
import com.oado.models.FeesReportData;
import com.oado.models.StudentData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.HidingScrollListener;
import com.oado.utils.PrefManager;
import com.oado.utils.StaticText;
import com.oado.utils.UserPermissionCheck;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class FeesPaidReportScreen extends AppCompatActivity implements SearchView.OnQueryTextListener{

    @BindView(R.id.recycler_view_fees_report)
    RecyclerView recycler_view_fees_report;

    @BindView(R.id.searchView)
    SearchView searchView;

    @BindView(R.id.tv_student_info)
    TextView tv_student_info;

    @BindView(R.id.rel_fees_submit)
    RelativeLayout rel_fees_submit;



    Filter filter;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    ArrayList<FeesReportData> reportDataArrayList;
    StudentData studentData;
    FeesPaidReportListAdapter feesPaidReportListAdapter;
    UserPermissionCheck userPermissionCheck;

    String class_name, section_name;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fees_paid_report_screen);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        prefManager = new PrefManager(this);
        userPermissionCheck = new UserPermissionCheck(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view_fees_report.setLayoutManager(mLayoutManager);
        recycler_view_fees_report.setItemAnimator(new DefaultItemAnimator());

        searchView.setVisibility(View.GONE);

        if (userPermissionCheck.isAdmin() || userPermissionCheck.isInstitute()){
            rel_fees_submit.setVisibility(View.VISIBLE);
        }else {
            rel_fees_submit.setVisibility(View.GONE);
        }


        recycler_view_fees_report.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                searchView.setVisibility(View.GONE);
            }
            @Override
            public void onShow() {
                searchView.setVisibility(View.VISIBLE);

            }
        });


        setupSearchView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            studentData = (StudentData) bundle.getSerializable("data");
            class_name = bundle.getString("class_name");
            section_name = bundle.getString("section_name");


            if (userPermissionCheck.isStudent() || userPermissionCheck.isGuardian()){
                tv_student_info.setText("Student Name: "+ studentData.getName()
                        + ",   Roll: " +studentData.getRoll_no());
            }else {
                tv_student_info.setText("Student Name: "+ studentData.getName()
                        + ",   Roll: " +studentData.getRoll_no()
                        + ",\nClass: "+class_name
                        + ", Section: "+section_name );
            }

        }




        rel_fees_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (studentData != null){

                    Intent intent = new Intent(FeesPaidReportScreen.this,
                            FeesSubmit_Add.class);
                    intent.putExtra("data", studentData);
                    intent.putExtra("class_name", class_name);
                    intent.putExtra("section_name", section_name);
                    startActivity(intent);

                }

            }
        });

    }


    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(StaticText.searchview_text);

    }



    @Override
    public boolean onQueryTextChange(String newText)
    {
        if (TextUtils.isEmpty(newText)) {

            if (filter != null) {
                filter.filter(null);
            }

        } else {
            filter.filter(newText);
        }
        return true;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {return false;}

    @Override
    public void onResume() {

        if (studentData != null){
            getStudentFeesReport(studentData.getId());
        }

        super.onResume();
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


    private void getStudentFeesReport(String s_id){

        reportDataArrayList = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_student_fees_list;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.student_id, s_id);


        Log.d(Constants.TAG , "get_student_fees_list - " + url);
        Log.d(Constants.TAG , "get_student_fees_list - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_student_fees_list- " + response.toString());

                if (response != null) {
                    try {

                        FeesReportData feesReportData;
                        FeesAmounts feesAmounts;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                feesReportData = new FeesReportData();

                                feesReportData.setId(object.optString("id"));
                                feesReportData.setInstitute_id(object.optString("institute_id"));
                                feesReportData.setStudent_id(object.optString("student_id"));
                                feesReportData.setClass_id(object.optString("class_id"));
                                feesReportData.setTotal_amount(object.optString("total_amount"));
                                feesReportData.setPay_amount(object.optString("pay_amount"));
                                feesReportData.setMonth_name(object.optString("month_name"));
                                feesReportData.setPay_date(object.optString("pay_date"));
                                feesReportData.setType_of_center(object.optString("type_of_center"));


                                ArrayList<FeesAmounts> listAmounts = new ArrayList<>();
                                JSONArray fees_details = object.getJSONArray("fees_details");
                                for (int j = 0; j < fees_details.length(); j++){
                                    JSONObject object1 = fees_details.getJSONObject(j);

                                    feesAmounts = new FeesAmounts();

                                    feesAmounts.setTotal_amount(object1.optString("total_amount"));
                                    feesAmounts.setPay_amount(object1.optString("pay_amount"));
                                    feesAmounts.setPay_date(object1.optString("pay_date"));
                                    feesAmounts.setClass_name(object1.optString("class_name"));
                                    feesAmounts.setSubject_name(object1.optString("subject_name"));

                                    listAmounts.add(feesAmounts);

                                }

                                feesReportData.setListAmounts(listAmounts);

                                reportDataArrayList.add(feesReportData);
                            }

                            feesPaidReportListAdapter =
                                    new FeesPaidReportListAdapter(FeesPaidReportScreen.this,
                                            reportDataArrayList);
                            recycler_view_fees_report.setAdapter(feesPaidReportListAdapter);

                            if (reportDataArrayList.size() > 10){
                                searchView.setVisibility(View.VISIBLE);
                            }else {
                                searchView.setVisibility(View.GONE);
                            }


                        }else {

                            Toasty.info(getApplicationContext(),
                                    "No data found",
                                    Toast.LENGTH_SHORT, true).show();
                            searchView.setVisibility(View.GONE);

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_student_fees_list- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getApplicationContext()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }



}
