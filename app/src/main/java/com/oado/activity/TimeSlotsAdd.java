package com.oado.activity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.adapters.SubjectSpinnerAdapter;
import com.oado.adapters.WeekDaysSpinnerAdapter;
import com.oado.models.TimeSlotData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.PrefManager;
import com.oado.utils.ValidationClass;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class TimeSlotsAdd extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.spinner_class) Spinner spinner_class;
    @BindView(R.id.spinner_subject) Spinner spinner_subject;
    @BindView(R.id.spinner_section) Spinner spinner_section;
    @BindView(R.id.spinner_teacher) Spinner spinner_teacher;
    @BindView(R.id.spinner_week_days) Spinner spinner_week_days;
    @BindView(R.id.tv_week_days) TextView tv_week_days;
    @BindView(R.id.start_time) TextView start_time;
    @BindView(R.id.end_time) TextView end_time;
    @BindView(R.id.rel_create_time_slots) RelativeLayout rel_create_time_slots;
    @BindView(R.id.tv_button_title) TextView tv_button_title;
    @BindView(R.id.iv_plus) ImageView iv_plus;

    Calendar myCalendar = Calendar.getInstance();

    ArrayList<HashMap<String, String>> list_Class;
    ArrayList<HashMap<String, String>> list_Section;
    ArrayList<HashMap<String, String>> list_Subject;
    ArrayList<HashMap<String, String>> list_Teacher;

    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    MySpinnerAdapter spinnerAdapter;
    WeekDaysSpinnerAdapter weekDaysSpinnerAdapter;

    TimeSlotData timeSlotData;
    ArrayList<HashMap<String, String>> arrayListDays;


    String type, class_id, section_id, subject_id, teacher_id;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_slots_add);
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


        start_time.setOnClickListener(this);
        end_time.setOnClickListener(this);
        rel_create_time_slots.setOnClickListener(this);


        arrayListDays = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("name", "Select Days");
        arrayListDays.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Sunday");
        arrayListDays.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Monday");
        arrayListDays.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Tuesday");
        arrayListDays.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Wednesday");
        arrayListDays.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Thursday");
        arrayListDays.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Friday");
        arrayListDays.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Saturday");
        arrayListDays.add(hashMap);




        setActionOnViews();

        clickOnViews();

        setClassSectionSubjectData();

    }

    public void clickOnViews(){

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    class_id = list_Class.get(position).get("id");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    section_id = list_Section.get(position).get("id");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    subject_id = list_Subject.get(position).get("id");
                }

                Log.d(Commons.TAG, "subject_id = "+subject_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_teacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    teacher_id = list_Teacher.get(position).get("id");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        spinner_week_days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    private void setActionOnViews(){
        type = "add";
        tv_button_title.setText(getResources().getString(R.string.add_time_slot));
        getSupportActionBar().setTitle(getResources().getString(R.string.add_time_slot));
        iv_plus.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            type = bundle.getString("type");

            if (type.matches("edit")){

                tv_button_title.setText(getResources().getString(R.string.edit_time_slot));
                iv_plus.setVisibility(View.GONE);

                getSupportActionBar().setTitle(getResources().getString(R.string.edit_time_slot));

                timeSlotData = (TimeSlotData) bundle.getSerializable("user_data");


                class_id = timeSlotData.getClass_id();
                section_id = timeSlotData.getSection_id();
                subject_id = timeSlotData.getSubject_id();
                teacher_id = timeSlotData.getTeacher_id();

                start_time.setText(timeSlotData.getStart_time());
                end_time.setText(timeSlotData.getEnd_time());


            }else if (type.matches("add")) {

                tv_button_title.setText(getResources().getString(R.string.add_time_slot));
                getSupportActionBar().setTitle(getResources().getString(R.string.add_time_slot));

            }



        }


        if (type.matches("edit")){

            String[] array = timeSlotData.getWeek_days().split(",");
            ArrayList<String> preSelectedDaysList = new ArrayList<>();
            Collections.addAll(preSelectedDaysList, array);

            weekDaysSpinnerAdapter = new WeekDaysSpinnerAdapter(this, arrayListDays,
                    preSelectedDaysList, tv_week_days);
            spinner_week_days.setAdapter(weekDaysSpinnerAdapter);


        }else {

            weekDaysSpinnerAdapter = new WeekDaysSpinnerAdapter(this, arrayListDays, tv_week_days);
            spinner_week_days.setAdapter(weekDaysSpinnerAdapter);

        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.start_time:

                dialogTimePicker(start_time);

                break;

            case R.id.end_time:

                dialogTimePicker(end_time);

                break;


            case R.id.rel_create_time_slots:

                checkValidation();

                break;


        }

    }

    public void dialogTimePicker(final TextView textView){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(TimeSlotsAdd.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String in_time = selectedHour + ":" + selectedMinute;

                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

                Date date = null;
                try {
                    date = fmt.parse(in_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
                String formattedTime = fmtOut.format(date);


                textView.setText(formattedTime);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    private void setClassSectionSubjectData(){

        // class set
        list_Class = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        int set_position = 0;




        // section set
        list_Section = new ArrayList<>();
        hashMap = new HashMap<>();
        set_position = 0;

        if (globalClass.getListSection().size() == 0){

            hashMap.put("name", "No Section here");
            list_Section.add(hashMap);


            spinnerAdapter = new MySpinnerAdapter(TimeSlotsAdd.this,
                    list_Section);
            spinner_section.setAdapter(spinnerAdapter);

        }else {

            hashMap.put("name", "Select Section");
            list_Section.add(hashMap);

            for (int i = 0; i < globalClass.getListSection().size(); i++){

                hashMap = new HashMap<>();

                hashMap.put("id", globalClass.getListSection().get(i).getId());
                hashMap.put("name", globalClass.getListSection().get(i).getName());


                if (type.matches("edit")
                        && timeSlotData.getSection_id()
                        .matches(globalClass.getListSection().get(i).getId())){
                    set_position = i+1;
                }

                list_Section.add(hashMap);

            }


            spinnerAdapter = new MySpinnerAdapter(TimeSlotsAdd.this,
                    list_Section);
            spinner_section.setAdapter(spinnerAdapter);


            if (type.matches("edit")){
                spinner_section.setSelection(set_position);

            }

        }




        // subject set
        list_Subject = new ArrayList<>();
        hashMap = new HashMap<>();
        set_position = 0;

        //Log.d(Commons.TAG, "size subject = "+globalClass.getListSubject().size());

        if (globalClass.getListSubject().size() == 0){

            hashMap.put("name", "No Subject here");
            list_Subject.add(hashMap);

            spinnerAdapter = new MySpinnerAdapter(TimeSlotsAdd.this,
                    list_Section);
            spinner_subject.setAdapter(spinnerAdapter);

        }else {

            hashMap.put("name", "Select Subject");
            list_Subject.add(hashMap);

            for (int i = 0; i < globalClass.getListSubject().size(); i++){

                hashMap = new HashMap<>();

                hashMap.put("id", globalClass.getListSubject().get(i).getId());
                hashMap.put("name", globalClass.getListSubject().get(i).getSubject_name());

                if (type.matches("edit")
                        && timeSlotData.getSubject_id().matches(globalClass.getListSubject().get(i).getId())){
                    set_position = i+1;
                }


                list_Subject.add(hashMap);

            }


            spinnerAdapter = new MySpinnerAdapter(TimeSlotsAdd.this,
                    list_Subject);
            spinner_subject.setAdapter(spinnerAdapter);


            if (type.matches("edit")){
                spinner_subject.setSelection(set_position);

            }

        }


        getAllClass();
        getAllTeacher();

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

    private void getAllClass(){

        progressDialog.show();

        list_Class = new ArrayList<>();

        String url = ApiClient.get_all_classes;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());


        Log.d(Constants.TAG , "get_all_classes - " + url);
        Log.d(Constants.TAG , "get_all_classes - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_all_classes- " + response.toString());

                if (response != null) {
                    try {

                        HashMap<String, String> hashMap = new HashMap<>();
                        int set_position = 0;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No class found",
                                        Toast.LENGTH_SHORT, true).show();

                                hashMap.put("name", "No Class here");
                                list_Class.add(hashMap);

                                spinnerAdapter = new MySpinnerAdapter(TimeSlotsAdd.this,
                                        list_Class);
                                spinner_class.setAdapter(spinnerAdapter);

                                progressDialog.dismiss();

                                return;
                            }

                            hashMap.put("name", "Select Class");
                            list_Class.add(hashMap);

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                hashMap = new HashMap<>();

                                hashMap.put("id", object.optString("id"));
                                hashMap.put("name", object.optString("class_name"));


                                if (type.matches("edit")
                                        && timeSlotData.getClass_id()
                                        .equals(object.optString("id"))){
                                    set_position = i+1;
                                }

                                list_Class.add(hashMap);
                            }

                            spinnerAdapter = new MySpinnerAdapter(TimeSlotsAdd.this,
                                    list_Class);
                            spinner_class.setAdapter(spinnerAdapter);


                            if (type.matches("edit")){
                                spinner_class.setSelection(set_position);
                            }

                        }else {

                            Toasty.info(getApplicationContext(),
                                    "No class found",
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
                Log.d(Commons.TAG, "get_all_classes- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getApplicationContext()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }


    private void checkValidation(){


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

        if (subject_id == null){
            Toasty.info(getApplicationContext(),
                    "Select subject",
                    Toast.LENGTH_LONG, true).show();
            return;
        }

        if (teacher_id == null){
            Toasty.info(getApplicationContext(),
                    "Select teacher",
                    Toast.LENGTH_LONG, true).show();
            return;
        }



        if (weekDaysSpinnerAdapter.getSelectedDaysList().size() == 0){
            Toasty.info(getApplicationContext(),
                    "Select week days",
                    Toast.LENGTH_LONG, true).show();
            return;
        }


        if (start_time.getText().toString().isEmpty()){
            Toasty.info(getApplicationContext(),
                    "Set start time",
                    Toast.LENGTH_LONG, true).show();
            return;
        }

        if (end_time.getText().toString().isEmpty()){
            Toasty.info(getApplicationContext(),
                    "Set end time",
                    Toast.LENGTH_LONG, true).show();
            return;
        }



        if (type.matches("add")){

            createTimeSlot();

        }else if (type.matches("edit")){

            editTimeSlot();

        }


    }

    private String getWeekDays(){
        String days = "";

        for (int i = 0; i < weekDaysSpinnerAdapter.getSelectedDaysList().size(); i++){
            days = days + weekDaysSpinnerAdapter.getSelectedDaysList().get(i).toLowerCase() + ",";
        }

        if (days.endsWith(",")){
            days = Commons.removeLastChar(days);
        }

        return days;
    }


    private void getAllTeacher(){

        list_Teacher = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_all_teachers;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());

        Log.d(Constants.TAG , "get_all_teachers - " + url);
        Log.d(Constants.TAG , "get_all_teachers - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_all_teachers- " + response.toString());

                if (response != null) {
                    try {

                        HashMap<String, String> hashMap = new HashMap<>();

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No data found",
                                        Toast.LENGTH_SHORT, true).show();

                                hashMap.put("name", "No Teacher here");
                                list_Teacher.add(hashMap);

                                spinnerAdapter = new MySpinnerAdapter(TimeSlotsAdd.this,
                                        list_Teacher);
                                spinner_teacher.setAdapter(spinnerAdapter);

                                progressDialog.dismiss();

                                return;
                            }


                            int set_position = 0;
                            hashMap.put("name", "Select Teacher");
                            list_Teacher.add(hashMap);


                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                hashMap = new HashMap<>();

                                hashMap.put("id", object.optString("id"));
                                hashMap.put("name", object.optString("name"));

                                list_Teacher.add(hashMap);

                                if (type.matches("edit")
                                        && timeSlotData.getTeacher_id().
                                        matches(object.optString("id"))){
                                    set_position = i+1;
                                }


                            }

                            spinnerAdapter = new MySpinnerAdapter(TimeSlotsAdd.this,
                                    list_Teacher);
                            spinner_teacher.setAdapter(spinnerAdapter);

                            if (type.matches("edit")){
                                spinner_teacher.setSelection(set_position);
                            }


                        }else {

                            Toasty.info(getApplicationContext(),
                                    "No data found",
                                    Toast.LENGTH_SHORT, true).show();

                            hashMap.put("name", "No Teacher here");
                            list_Teacher.add(hashMap);

                            spinnerAdapter = new MySpinnerAdapter(TimeSlotsAdd.this,
                                    list_Teacher);
                            spinner_teacher.setAdapter(spinnerAdapter);

                            progressDialog.dismiss();

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_all_teachers- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getApplicationContext()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }


    public void createTimeSlot(){

        progressDialog.show();

        String url = ApiClient.add_timeslot;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.class_id, class_id);
        params.put(ApiClient.section_id, section_id);
        params.put(ApiClient.subject_id, subject_id);
        params.put(ApiClient.teacher_id, teacher_id);
        params.put(ApiClient.week_days, getWeekDays());
        params.put(ApiClient.start_time, start_time.getText().toString());
        params.put(ApiClient.end_time, end_time.getText().toString());



        Log.d(Constants.TAG , "add_timeslot - " + url);
        Log.d(Constants.TAG , "add_timeslot - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_timeslot- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getApplicationContext(), message,
                                    Toast.LENGTH_SHORT, true).show();

                         //   Commons.restartActivity(TimeSlotsAdd.this);

                            finish();

                        }else {

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
                Log.d(Commons.TAG, "add_timeslot- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(TimeSlotsAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }


    public void editTimeSlot(){

        progressDialog.show();

        String url = ApiClient.edit_timeslot;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.id, timeSlotData.getId());
        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.class_id, class_id);
        params.put(ApiClient.section_id, section_id);
        params.put(ApiClient.subject_id, subject_id);
        params.put(ApiClient.teacher_id, teacher_id);
        params.put(ApiClient.week_days, getWeekDays());
        params.put(ApiClient.start_time, start_time.getText().toString());
        params.put(ApiClient.end_time, end_time.getText().toString());



        Log.d(Constants.TAG , "edit_timeslot - " + url);
        Log.d(Constants.TAG , "edit_timeslot - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "edit_timeslot- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getApplicationContext(), message,
                                    Toast.LENGTH_SHORT, true).show();

                        }else {

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
                Log.d(Commons.TAG, "edit_timeslot- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(TimeSlotsAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }



}
