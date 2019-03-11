package com.oado.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.adapters.ExamResultListAdapter;
import com.oado.models.ExamResultData;
import com.oado.models.SectionData;
import com.oado.models.StudentData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.HidingScrollListener;
import com.oado.utils.PrefManager;
import com.oado.utils.StaticText;
import com.oado.utils.UserPermissionCheck;
import com.oado.utils.ValidationClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;


public class ExamResult extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.recycler_view) RecyclerView recycler_view;
    @BindView(R.id.tv_select_filter) TextView tv_select_filter;
    @BindView(R.id.searchView) SearchView searchView;



    Spinner spinner_class, spinner_section;

    ArrayList<HashMap<String, String>> list_Class;
    ArrayList<HashMap<String, String>> list_Section;
    ArrayList<SectionData> sectionDataArrayList;

    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    ArrayList<StudentData> listStudent;
    MySpinnerAdapter spinnerAdapter;
    ExamResultListAdapter examResultListAdapter;
    UserPermissionCheck userPermissionCheck;

    String class_id, class_name, section_id, section_name, student_id;

    Filter filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_results);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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


        tv_select_filter.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


        recycler_view.addOnScrollListener(new HidingScrollListener() {
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

        if (userPermissionCheck.isStudent()
                || userPermissionCheck.isGuardian()){

            getStudentsClassSectionWise();

        }else {

            showFilterDialog();
        }



    }


    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(StaticText.searchview_text);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_menu, menu);

        MenuItem item = menu.findItem(R.id.action_filter);
        if (userPermissionCheck.isStudent()
                || userPermissionCheck.isGuardian()){

            item.setVisible(false);
        }


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


                tv_select_filter.setText("Filter value: "+ class_name + ", " + section_name);
                tv_select_filter.setVisibility(View.VISIBLE);

                alertDialog.dismiss();


                getStudentsClassSectionWise();


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

            spinnerAdapter = new MySpinnerAdapter(ExamResult.this,
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

            spinnerAdapter = new MySpinnerAdapter(ExamResult.this,
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

            spinnerAdapter = new MySpinnerAdapter(ExamResult.this,
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

            spinnerAdapter = new MySpinnerAdapter(ExamResult.this,
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onResume();
    }



    private void getStudentsClassSectionWise(){

        listStudent = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_student_result_list;
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



        Log.d(Constants.TAG , "get_student_result_list - " + url);
        Log.d(Constants.TAG , "get_student_result_list - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_student_result_list- " + response.toString());

                if (response != null) {
                    try {

                        StudentData studentData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No student found",
                                        Toast.LENGTH_SHORT, true).show();


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




                                if (userPermissionCheck.isStudent()
                                        || userPermissionCheck.isGuardian()){
                                    if (prefManager.getStudent_id()
                                            .equals(object.optString("id"))){
                                        listStudent.add(studentData);
                                    }

                                }else {
                                    listStudent.add(studentData);
                                }

                            }

                            sortingData();

                            if (listStudent.size() > 7){
                                searchView.setVisibility(View.VISIBLE);
                            }else {
                                searchView.setVisibility(View.GONE);
                            }


                            if (listStudent.size() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No student found",
                                        Toast.LENGTH_SHORT, true).show();

                            }

                        }else {

                            Toasty.info(getApplicationContext(),
                                    "No student found",
                                    Toast.LENGTH_SHORT, true).show();

                            sortingData();
                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_student_result_list- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(ExamResult.this).create();
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


        examResultListAdapter = new ExamResultListAdapter(this, listStudent);
        recycler_view.setAdapter(examResultListAdapter);
        filter = examResultListAdapter.getFilter();

    }




}
