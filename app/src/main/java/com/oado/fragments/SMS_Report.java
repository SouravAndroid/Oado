package com.oado.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.SmsReportListAdapter;
import com.oado.adapters.SubjectListAdapter;
import com.oado.models.SmsReportData;
import com.oado.models.SubjectData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.ConnectivityReceiver;
import com.oado.utils.Constants;
import com.oado.utils.PrefManager;
import com.oado.utils.UserPermissionCheck;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SMS_Report extends Fragment {
    public SMS_Report() { }

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    @BindView(R.id.linear_start_date)
    LinearLayout linear_start_date;
    @BindView(R.id.linear_end_date) LinearLayout linear_end_date;
    @BindView(R.id.tv_start_date)
    TextView tv_start_date;
    @BindView(R.id.tv_end_date) TextView tv_end_date;
    @BindView(R.id.btn_submit)
    Button btn_submit;


    private static final String dateFormat = "dd/MM/yyyy";
    String startDate, endDate;
    Calendar myCalendar;

    ProgressDialog progressDialog;
    ArrayList<SmsReportData> smsReportDataArrayList;
    PrefManager prefManager;
    UserPermissionCheck userPermissionCheck;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_sms_report, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        unbinder = ButterKnife.bind(this, view);

        initViews(view);



        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // unbind the view to free some memory
        unbinder.unbind();
    }


    private void initViews(View view){

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        prefManager = new PrefManager(getActivity());
        userPermissionCheck = new UserPermissionCheck(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);




        myCalendar = Calendar.getInstance();


        linear_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myCalendar = Calendar.getInstance();


                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                    myCalendar.setTime(sdf.parse(startDate));
                }catch (Exception e){
                    e.printStackTrace();
                }

                new DatePickerDialog(getActivity(), dateFrom, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        linear_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myCalendar = Calendar.getInstance();

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                    myCalendar.setTime(sdf.parse(endDate));
                }catch (Exception e){
                    e.printStackTrace();
                }

                new DatePickerDialog(getActivity(), dateTo, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectivityReceiver.isConnected()){

                    getSmsReport();

                }else {

                    Toasty.error(getActivity(),
                            "Please connect to internet",
                            Toast.LENGTH_LONG, true).show();
                }


            }
        });

        setStartDate();
    }


    private void setStartDate(){

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        startDate = sdf.format(myCalendar.getTime());
        tv_start_date.setText(sdf.format(myCalendar.getTime()));

        setEndDate();
    }

    private void setEndDate(){

        myCalendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        endDate = sdf.format(myCalendar.getTime());
        tv_end_date.setText(sdf.format(myCalendar.getTime()));


        if (ConnectivityReceiver.isConnected()){
            getSmsReport();
        }else {
            Toasty.error(getActivity(),
                    "Please connect to internet",
                    Toast.LENGTH_LONG, true).show();
        }
    }

    DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (checkStartDate()){
                updateLabelFrom();
            }else {

            }

        }

    };

    DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (checkEndDate()){
                updateLabelTo();
            }

        }

    };

    private void updateLabelFrom() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        startDate = sdf.format(myCalendar.getTime());
        tv_start_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabelTo() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        endDate = sdf.format(myCalendar.getTime());
        tv_end_date.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean checkStartDate(){
        boolean bool = true;

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        startDate = sdf.format(myCalendar.getTime());

        try {
            sdf = new SimpleDateFormat(dateFormat, Locale.US);
            Date strDate = sdf.parse(startDate);
            Date endDate = sdf.parse(tv_end_date.getText().toString());

            System.out.println("Compare Result start : " + strDate.compareTo(endDate));
            //  0 comes when two date are same,
            //  1 comes when date1 is higher then date2
            // -1 comes when date1 is lower then date2

            if (strDate.getTime() > endDate.getTime()){
                bool = false; // if then false
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return bool;
    }

    private boolean checkEndDate(){
        boolean bool = true;

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        endDate = sdf.format(myCalendar.getTime());

        try {
            sdf = new SimpleDateFormat(dateFormat, Locale.US);
            Date strDate = sdf.parse(tv_start_date.getText().toString());
            Date endDate = sdf.parse(this.endDate);

            System.out.println("Compare Result end : " + strDate.compareTo(endDate));

            if (strDate.getTime() > endDate.getTime()){
                bool = false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return bool;
    }


    private void getSmsReport(){

        smsReportDataArrayList = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_monthly_sms_report;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.from_date, startDate);
        params.put(ApiClient.to_date, endDate);

        if (userPermissionCheck.isTeacher()){
            params.put(ApiClient.teacher_id, prefManager.getId());
        }


        Log.d(Constants.TAG , "get_monthly_sms_report - " + url);
        Log.d(Constants.TAG , "get_monthly_sms_report - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_monthly_sms_report- " + response.toString());

                if (response != null) {
                    try {

                        SmsReportData smsReportData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray result = response.getJSONArray("result");

                            for (int i = 0; i < result.length(); i++){
                                JSONObject object = result.getJSONObject(i);

                                smsReportData = new SmsReportData();

                                smsReportData.setInstitute_id(object.optString("institute_id"));
                                smsReportData.setName(object.optString("name"));
                                smsReportData.setAddress(object.optString("address"));
                                smsReportData.setCity(object.optString("city"));
                                smsReportData.setState(object.optString("state"));
                                smsReportData.setCountry(object.optString("country"));
                                smsReportData.setPincode(object.optString("pincode"));
                                smsReportData.setTotal_count(object.optString("total_count"));

                                if (userPermissionCheck.isAdmin()){

                                    smsReportDataArrayList.add(smsReportData);

                                }else if (userPermissionCheck.isInstitute()){

                                    if (prefManager.getInstitute_id()
                                            .equals(object.optString("institute_id"))){
                                        smsReportDataArrayList.add(smsReportData);
                                    }
                                }

                            }

                            SmsReportListAdapter adapter =
                                    new SmsReportListAdapter(getActivity(), smsReportDataArrayList);
                            recycler_view.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                        }

                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_all_subjects- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }





}
