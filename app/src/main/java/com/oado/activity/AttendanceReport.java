package com.oado.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.AttendanceReportListAdapter;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.models.AttendanceReportData;
import com.oado.models.SectionData;
import com.oado.models.StudentData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.PrefManager;
import com.oado.utils.UserPermissionCheck;
import com.oado.utils.ValidationClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class AttendanceReport extends AppCompatActivity {

    RecyclerView recycler_view;
    TextView tv_selected_filter;

    TextView tv_from_date, tv_to_date, tv_from_date_set, tv_to_date_set;
    Spinner spinner_class, spinner_section;


    ArrayList<HashMap<String, String>> list_Class;
    ArrayList<HashMap<String, String>> list_Section;
    ArrayList<SectionData> sectionDataArrayList;

    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    MySpinnerAdapter spinnerAdapter;
    ArrayList<AttendanceReportData> listStudent;
    AttendanceReportListAdapter reportListAdapter;
    UserPermissionCheck userPermissionCheck;

    Calendar myCalendar = Calendar.getInstance();

    String class_id, class_name, section_id, section_name, from_data, to_data;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendence_report);
        initViews();



    }

    public void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        validationClass = new ValidationClass(this);
        prefManager = new PrefManager(this);
        globalClass = (GlobalClass) getApplicationContext();
        userPermissionCheck = new UserPermissionCheck(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        recycler_view = findViewById(R.id.recycler_view);
        tv_selected_filter = findViewById(R.id.tv_selected_filter);
        tv_selected_filter.setVisibility(View.GONE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());



        showFilterDialog();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            case R.id.action_filter:

                showFilterDialog();

                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setClassData(){

        // class set
        list_Class = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();

        if (globalClass.getListClass().size() == 0){

            hashMap.put("name", "No Class here");
            list_Class.add(hashMap);

            spinnerAdapter = new MySpinnerAdapter(AttendanceReport.this,
                    list_Class);
            spinner_class.setAdapter(spinnerAdapter);

        }else {
            hashMap.put("name", "Select Class");
            list_Class.add(hashMap);

            for (int i = 0; i < globalClass.getListClass().size(); i++){

                hashMap = new HashMap<>();

                hashMap.put("id", globalClass.getListClass().get(i).getId());
                hashMap.put("name", globalClass.getListClass().get(i).getName());

                list_Class.add(hashMap);
            }

            spinnerAdapter = new MySpinnerAdapter(AttendanceReport.this,
                    list_Class);
            spinner_class.setAdapter(spinnerAdapter);

        }

    }

    private void setSectionData(String class_id){
        list_Section = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        sectionDataArrayList = new ArrayList<>();

        for (int i = 0; i < globalClass.getListClass().size(); i++){

            if (class_id.equals(globalClass.getListClass().get(i).getId())){

                sectionDataArrayList = globalClass.getListClass().get(i).getList_Section();

            }

        }

        if (sectionDataArrayList.size() == 0){

            hashMap.put("name", "No Section here");
            list_Section.add(hashMap);


            spinnerAdapter = new MySpinnerAdapter(AttendanceReport.this,
                    list_Section);
            spinner_section.setAdapter(spinnerAdapter);

        }else {
            hashMap.put("name", "Select Section");
            list_Section.add(hashMap);

            for (int i = 0; i < sectionDataArrayList.size(); i++){

                hashMap = new HashMap<>();

                hashMap.put("id", sectionDataArrayList.get(i).getId());
                hashMap.put("name", sectionDataArrayList.get(i).getName());

                list_Section.add(hashMap);

            }

            spinnerAdapter = new MySpinnerAdapter(AttendanceReport.this,
                    list_Section);
            spinner_section.setAdapter(spinnerAdapter);


        }

    }

    public void showFilterDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_attendance_report_filter, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Filter :");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        from_data = null; to_data = null;
        class_id = null; section_id = null;

        spinner_class = dialogView.findViewById(R.id.spinner_class);
        spinner_section = dialogView.findViewById(R.id.spinner_section);
        Button apply_filter = dialogView.findViewById(R.id.apply_filter);
        tv_from_date = dialogView.findViewById(R.id.tv_from_date);
        tv_to_date = dialogView.findViewById(R.id.tv_to_date);
        tv_from_date_set = dialogView.findViewById(R.id.tv_from_date_set);
        tv_to_date_set = dialogView.findViewById(R.id.tv_to_date_set);
        LinearLayout linear_spinners = dialogView.findViewById(R.id.linear_spinners);

        if (userPermissionCheck.isStudent()
                || userPermissionCheck.isGuardian()){
            linear_spinners.setVisibility(View.GONE);

            class_id = prefManager.getClass_id();
            section_id = prefManager.getSection_id();

        }

        setClassData();

        tv_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myCalendar = Calendar.getInstance();

                new DatePickerDialog(AttendanceReport.this, dateFrom, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        tv_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myCalendar = Calendar.getInstance();

                new DatePickerDialog(AttendanceReport.this, dateTo, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });



        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                class_name = "";
                if (position > 0){
                    class_id = list_Class.get(position).get("id");
                    class_name = list_Class.get(position).get("name");

                    setSectionData(class_id);
                }else {

                    class_id = null;
                    section_id = null;

                    setSectionData("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                section_name = "";
                if (position > 0){
                    section_id = list_Section.get(position).get("id");
                    section_name = list_Section.get(position).get("name");
                }else {

                    section_id = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        apply_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (from_data == null){
                    Toasty.info(getApplicationContext(),
                            "Set From date",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }

                if (to_data == null){
                    Toasty.info(getApplicationContext(),
                            "Set To date",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }

                if (class_id == null){
                    Toasty.info(getApplicationContext(),
                            "Select class",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }

                if (section_id == null){
                    Toasty.info(getApplicationContext(),
                            "Select section",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }


                if (userPermissionCheck.isStudent()
                        || userPermissionCheck.isGuardian()){

                    tv_selected_filter.setText("From : "+from_data + "  -  To : " + to_data);
                }else {

                    tv_selected_filter.setText("From : "+from_data + "  -  To : " + to_data
                            +"\n"+"Filter value: "+ class_name + ", " + section_name);
                }


                tv_selected_filter.setVisibility(View.VISIBLE);

                alertDialog.dismiss();

                getAttendanceReport();


            }
        });



    }



    DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelFrom();
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
            updateLabelTo();
        }

    };


    private void updateLabelFrom() {
        //String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(Commons.dateFormat, Locale.US);
        from_data = sdf.format(myCalendar.getTime());
        tv_from_date_set.setText("From: "+sdf.format(myCalendar.getTime()));
    }

    private void updateLabelTo() {
        //String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(Commons.dateFormat, Locale.US);
        to_data = sdf.format(myCalendar.getTime());
        tv_to_date_set.setText("To: "+sdf.format(myCalendar.getTime()));
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }




    private void getAttendanceReport(){

        listStudent = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_attendance_report;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());

        if (userPermissionCheck.isStudent()
                || userPermissionCheck.isGuardian()){

            params.put(ApiClient.class_id, prefManager.getClass_id());
            params.put(ApiClient.section_id, prefManager.getSection_id());
        }else {
            params.put(ApiClient.class_id, class_id);
            params.put(ApiClient.section_id, section_id);
        }


        Log.d(Constants.TAG , "get_attendance_report - " + url);
        Log.d(Constants.TAG , "get_attendance_report - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_attendance_report- " + response.toString());

                if (response != null) {
                    try {

                        AttendanceReportData reportData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No student found",
                                        Toast.LENGTH_LONG, true).show();

                                progressDialog.dismiss();

                                return;
                            }

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                reportData = new AttendanceReportData();

                                reportData.setStu_id(object.optInt("id"));
                                reportData.setName(object.optString("name"));
                                reportData.setRoll_no(object.optString("roll_no"));
                                reportData.setEmail(object.optString("email"));
                                reportData.setImage(object.optString("image"));

                                JSONArray attendance_data = object.getJSONArray("attendance_data");
                                reportData.setAttendance_data(attendance_data.toString());


                                int P = 0; int L = 0; int A = 0;

                                if (userPermissionCheck.isStudent()
                                        || userPermissionCheck.isGuardian()){

                                    if (prefManager.getStudent_id()
                                            .equals(object.optString("id"))){

                                        for (int k = 0; k < attendance_data.length(); k++){
                                            JSONObject object1 = attendance_data.getJSONObject(k);

                                            String attendance_type =
                                                    object1.optString("attendance_type");
                                            if (attendance_type.matches("P")){

                                                P++;

                                            }else if (attendance_type.matches("L")){

                                                L++;

                                            }else if (attendance_type.matches("A")){

                                                A++;

                                            }

                                        }

                                        reportData.setP_count(String.valueOf(P));
                                        reportData.setL_count(String.valueOf(L));
                                        reportData.setA_count(String.valueOf(A));


                                        listStudent.add(reportData);

                                    }

                                }else {

                                    for (int k = 0; k < attendance_data.length(); k++){
                                        JSONObject object1 = attendance_data.getJSONObject(k);

                                        String attendance_type =
                                                object1.optString("attendance_type");
                                        if (attendance_type.matches("P")){

                                            P++;

                                        }else if (attendance_type.matches("L")){

                                            L++;

                                        }else if (attendance_type.matches("A")){

                                            A++;

                                        }

                                    }

                                    reportData.setP_count(String.valueOf(P));
                                    reportData.setL_count(String.valueOf(L));
                                    reportData.setA_count(String.valueOf(A));


                                    listStudent.add(reportData);
                                }




                            }

                            reportListAdapter = new AttendanceReportListAdapter(AttendanceReport.this,
                                    listStudent);
                            recycler_view.setAdapter(reportListAdapter);

                        }else {

                            Toasty.info(getApplicationContext(),
                                    "No student found",
                                    Toast.LENGTH_LONG, true).show();

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_attendance_report- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(AttendanceReport.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }


}
