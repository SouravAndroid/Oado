package com.oado.activity;

import android.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.adapters.OutAttendanceListAdapter;
import com.oado.models.SectionData;
import com.oado.models.StudentData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.ConnectivityReceiver;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.PrefManager;
import com.oado.utils.ValidationClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class OutAttendanceByTeacher extends AppCompatActivity {

    @BindView(R.id.recycler_view) RecyclerView recycler_view;
    @BindView(R.id.btn_out_all_submit) Button btn_out_all_submit;
    @BindView(R.id.btn_out_selected) Button btn_out_selected;
    @BindView(R.id.tv_select_filter) TextView tv_select_filter;


    Spinner spinner_class, spinner_section;


    ArrayList<HashMap<String, String>> list_Class;
    ArrayList<HashMap<String, String>> list_Section;
    ArrayList<SectionData> sectionDataArrayList;

    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    MySpinnerAdapter spinnerAdapter;
    ArrayList<StudentData> listStudent;
    OutAttendanceListAdapter outAttendanceListAdapter;
    ArrayList<String> list_all_ids;

    String class_id, class_name, section_id, section_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.out_attendence_by_teacher);
        ButterKnife.bind(this);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        list_all_ids = new ArrayList<>();


        tv_select_filter.setVisibility(View.GONE);
        btn_out_selected.setVisibility(View.GONE);
        btn_out_all_submit.setVisibility(View.GONE);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


        showFilterDialog();


        btn_out_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (outAttendanceListAdapter != null){

                    submitOutAttendanceStatus("selected");
                }

            }
        });


        btn_out_all_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list_all_ids.size() > 0){

                    submitOutAttendanceStatus("all");
                }

            }
        });

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


    public void showFilterDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_attendance_filter, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Filter Student :");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        spinner_class = dialogView.findViewById(R.id.spinner_class);
        spinner_section = dialogView.findViewById(R.id.spinner_section);
        Button apply_filter = dialogView.findViewById(R.id.apply_filter);

        class_id = null;
        section_id = null;

        setClassData();

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


                if (!ConnectivityReceiver.isConnected()){
                    Toasty.error(getApplicationContext(),
                            "Connect to internet",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }


                tv_select_filter.setText("Filter value: "+ class_name + ", " + section_name);
                tv_select_filter.setVisibility(View.VISIBLE);


                alertDialog.dismiss();


                getAllStudentsWithAttendanceStatus();


            }
        });

    }

    private void setClassData(){

        // class set
        list_Class = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();

        if (globalClass.getListClass().size() == 0){

            hashMap.put("name", "No Class here");
            list_Class.add(hashMap);

            spinnerAdapter = new MySpinnerAdapter(OutAttendanceByTeacher.this,
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

            spinnerAdapter = new MySpinnerAdapter(OutAttendanceByTeacher.this,
                    list_Class);
            spinner_class.setAdapter(spinnerAdapter);

        }


    }


    private void setSectionData(String class_id_){
        list_Section = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        sectionDataArrayList = new ArrayList<>();

        for (int i = 0; i < globalClass.getListClass().size(); i++){

            if (class_id_.equals(globalClass.getListClass().get(i).getId())){

                sectionDataArrayList = globalClass.getListClass().get(i).getList_Section();

            }

        }

        if (sectionDataArrayList.size() == 0){

            hashMap.put("name", "No Section here");
            list_Section.add(hashMap);


            spinnerAdapter = new MySpinnerAdapter(OutAttendanceByTeacher.this,
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

            spinnerAdapter = new MySpinnerAdapter(OutAttendanceByTeacher.this,
                    list_Section);
            spinner_section.setAdapter(spinnerAdapter);


        }

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



    private void getAllStudentsWithAttendanceStatus(){

        listStudent = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_attendance_out_report;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.class_id, class_id);
        params.put(ApiClient.section_id, section_id);
        params.put(ApiClient.from_date, Commons.getCurrentDate());
        params.put(ApiClient.to_date, Commons.getCurrentDate());


        Log.d(Constants.TAG , "get_attendance_out_report - " + url);
        Log.d(Constants.TAG , "get_attendance_out_report - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_attendance_out_report- " + response.toString());

                if (response != null) {
                    try {

                        StudentData studentData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No student found for out punch",
                                        Toast.LENGTH_LONG, true).show();

                                listStudent = new ArrayList<>();

                                sortingData();

                                progressDialog.dismiss();

                                return;
                            }

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                studentData = new StudentData();


                                studentData.setId(object.optString("id"));
                                studentData.setName(object.optString("name"));
                                studentData.setRoll_no(object.optString("roll_no"));
                                studentData.setEmail(object.optString("email"));
                                studentData.setImage(object.optString("image"));

                                listStudent.add(studentData);

                                list_all_ids.add(object.optString("id"));

                            }


                            if (listStudent.size() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No student found for out punch",
                                        Toast.LENGTH_LONG, true).show();

                                sortingData();

                            }else {

                                btn_out_all_submit.setVisibility(View.VISIBLE);

                                sortingData();
                            }


                        }else {

                            Toasty.info(getApplicationContext(),
                                    "No student found for out punch",
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
                Log.d(Commons.TAG, "get_attendance_out_report- " + res);
                progressDialog.dismiss();

                AlertDialog alert =
                        new AlertDialog.Builder(OutAttendanceByTeacher.this).create();
                alert.setMessage("Server Error");
                alert.show();
            }
        });

    }


    private void sortingData(){

        StudentData studentData;
        for (int i = 1; i < listStudent.size(); i++) {

            for (int j = i; j > 0; j--) {

                if (Integer.parseInt(listStudent.get(j).getRoll_no()) <
                        Integer.parseInt(listStudent.get(j - 1).getRoll_no())) {

                    studentData = listStudent.get(j);
                    listStudent.set(j, listStudent.get(j - 1));
                    listStudent.set(j - 1, studentData);

                }
            }
        }


        outAttendanceListAdapter = new OutAttendanceListAdapter(this,
                listStudent, btn_out_selected);
        recycler_view.setAdapter(outAttendanceListAdapter);
        outAttendanceListAdapter.notifyDataSetChanged();
    }


    private String getSelected_Ids(){

        StringBuffer sb = new StringBuffer();
        for (String s : outAttendanceListAdapter.getListIds()){
            sb.append(s).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }


    private String getAll_Ids(){

        StringBuffer sb = new StringBuffer();
        for (String s : list_all_ids){
            sb.append(s).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    private void submitOutAttendanceStatus(String where){

        progressDialog.show();

        String url = ApiClient.add_attendance_out_by_teacher;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.class_id, class_id);
        params.put(ApiClient.section_id, section_id);
        params.put(ApiClient.attendance_date, Commons.getCurrentDate());


        if (where.equals("all")){
            params.put(ApiClient.student_id, getAll_Ids());
        }else if (where.equals("selected")){
            params.put(ApiClient.student_id, getSelected_Ids());
        }


        Log.d(Constants.TAG , "add_attendance_out_by_teacher - " + url);
        Log.d(Constants.TAG , "add_attendance_out_by_teacher - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_attendance_out_by_teacher- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            btn_out_all_submit.setVisibility(View.GONE);
                            btn_out_selected.setVisibility(View.GONE);


                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();

                            getAllStudentsWithAttendanceStatus();

                        }else {

                            Toasty.info(getApplicationContext(),
                                    message,
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
                Log.d(Commons.TAG, "add_attendance_out_by_teacher- " + res);
                progressDialog.dismiss();

                AlertDialog alert =
                        new AlertDialog.Builder(OutAttendanceByTeacher.this).create();
                alert.setMessage("Server Error");
                alert.show();
            }

        });

    }




}
