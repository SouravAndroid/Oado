package com.oado.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.GuardiansSpinnerAdapter;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.adapters.OthersStaffSpinnerAdapter;
import com.oado.adapters.StudentsSpinnerAdapter;
import com.oado.adapters.TeachersSpinnerAdapter;
import com.oado.models.GuardianData;
import com.oado.models.SectionData;
import com.oado.models.StaffData;
import com.oado.models.StudentData;
import com.oado.models.TeacherData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
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

public class DiaryAdd extends AppCompatActivity {

    @BindView(R.id.spinner_class) Spinner spinner_class;
    @BindView(R.id.spinner_section) Spinner spinner_section;
    @BindView(R.id.spinner_teacher) Spinner spinner_teacher;
    @BindView(R.id.spinner_student) Spinner spinner_student;
    @BindView(R.id.spinner_guardian) Spinner spinner_guardian;
    @BindView(R.id.spinner_others_staff) Spinner spinner_others_staff;

    @BindView(R.id.edt_dairy_name) EditText edt_dairy_name;

    @BindView(R.id.tv_teachers) TextView tv_teachers;
    @BindView(R.id.tv_students) TextView tv_students;
    @BindView(R.id.tv_guardians) TextView tv_guardians;
    @BindView(R.id.tv_others_staff) TextView tv_others_staff;

    @BindView(R.id.rel_create_dairy) RelativeLayout rel_create_dairy;
    @BindView(R.id.linear_selection_wise) LinearLayout linear_selection_wise;


    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;

    MySpinnerAdapter spinnerAdapter;
    TeachersSpinnerAdapter teachersSpinnerAdapter;
    OthersStaffSpinnerAdapter othersStaffSpinnerAdapter;
    GuardiansSpinnerAdapter guardiansSpinnerAdapter;
    StudentsSpinnerAdapter studentsSpinnerAdapter;


    ArrayList<HashMap<String, String>> list_Class;
    ArrayList<HashMap<String, String>> list_Section;
    ArrayList<SectionData> sectionDataArrayList;


    ArrayList<StudentData> listStudent;
    ArrayList<TeacherData> listTeacher;
    ArrayList<StaffData> listStaff;
    ArrayList<GuardianData> listGuardian;


    String class_id, class_name, section_id, section_name;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_add);
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


        listStudent = new ArrayList<>();
        StudentData studentData = new StudentData();
        studentData.setName("No Student found");
        studentData.setRoll_no("");
        listStudent.add(studentData);


        studentsSpinnerAdapter =
                new StudentsSpinnerAdapter(DiaryAdd.this,
                        listStudent, tv_students);
        spinner_student.setAdapter(studentsSpinnerAdapter);


        setClassData();

        clickOnViews();


        getAllTeacher();
        getAllStaff();


    }

    public void clickOnViews(){


        spinner_teacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_student.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_guardian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_others_staff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    class_id = list_Class.get(position).get("id");

                    setSectionData(class_id);

                    if (class_id != null && section_id != null){
                        getStudentsAndGuardianClassAndSectionWise();
                    }

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

                if (position > 0){
                    section_id = list_Section.get(position).get("id");

                    if (class_id != null && section_id != null){
                        getStudentsAndGuardianClassAndSectionWise();
                    }

                }else {
                    section_id = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        rel_create_dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_dairy_name.getText().toString().trim().length() == 0){

                    Toasty.info(getApplicationContext(),
                            "Enter diary name",
                            Toast.LENGTH_SHORT, true).show();

                    return;

                }


                createDiary();


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

    private void setClassData(){

        // class set
        list_Class = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();

        if (globalClass.getListClass().size() == 0){

            hashMap.put("name", "No Class here");
            list_Class.add(hashMap);

            spinnerAdapter = new MySpinnerAdapter(DiaryAdd.this,
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

            spinnerAdapter = new MySpinnerAdapter(DiaryAdd.this,
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

            spinnerAdapter = new MySpinnerAdapter(DiaryAdd.this,
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

            spinnerAdapter = new MySpinnerAdapter(DiaryAdd.this,
                    list_Section);
            spinner_section.setAdapter(spinnerAdapter);

        }

    }

    private void getAllTeacher(){

        listTeacher = new ArrayList<>();

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

                        TeacherData teacherData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No Teacher found",
                                        Toast.LENGTH_SHORT, true).show();

                                teacherData = new TeacherData();
                                teacherData.setName("No Teacher found");
                                listTeacher.add(teacherData);

                                teachersSpinnerAdapter = new TeachersSpinnerAdapter(DiaryAdd.this,
                                        listTeacher, tv_teachers);
                                spinner_teacher.setAdapter(teachersSpinnerAdapter);

                                progressDialog.dismiss();

                                return;
                            }

                            teacherData = new TeacherData();
                            teacherData.setName("Select Teacher");
                            listTeacher.add(teacherData);


                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                teacherData = new TeacherData();

                                teacherData.setId(object.optString("id"));
                                teacherData.setName(object.optString("name"));
                                teacherData.setSubject_id(object.optString("subject_id"));
                                teacherData.setInstitute_id(object.optString("institute_id"));
                                teacherData.setSubject_name(object.optString("subject_name"));
                                teacherData.setGender(object.optString("gender"));
                                teacherData.setDob(object.optString("dob"));
                                teacherData.setMobile(object.optString("phone_no"));
                                teacherData.setEmail(object.optString("email"));
                                teacherData.setStreet(object.optString("address"));
                                teacherData.setCity(object.optString("city"));
                                teacherData.setState(object.optString("state"));
                                teacherData.setCountry(object.optString("country"));
                                teacherData.setPincode(object.optString("pincode"));
                                teacherData.setImage(object.optString("image"));


                                listTeacher.add(teacherData);

                            }

                            teachersSpinnerAdapter = new TeachersSpinnerAdapter(DiaryAdd.this,
                                    listTeacher, tv_teachers);
                            spinner_teacher.setAdapter(teachersSpinnerAdapter);

                        }else {

                            Toasty.info(getApplicationContext(),
                                    "No Teacher found",
                                    Toast.LENGTH_SHORT, true).show();

                            teacherData = new TeacherData();
                            teacherData.setName("No Teacher found");
                            listTeacher.add(teacherData);

                            teachersSpinnerAdapter = new TeachersSpinnerAdapter(DiaryAdd.this,
                                    listTeacher, tv_teachers);
                            spinner_teacher.setAdapter(teachersSpinnerAdapter);

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

    private void getAllStaff(){

        listStaff = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_all_staffs;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());

        Log.d(Constants.TAG , "get_all_staffs - " + url);
        Log.d(Constants.TAG , "get_all_staffs - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_all_staffs- " + response.toString());

                if (response != null) {
                    try {

                        StaffData staffData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No Staff found",
                                        Toast.LENGTH_SHORT, true).show();

                                staffData = new StaffData();
                                staffData.setName("No Staff found");
                                listStaff.add(staffData);

                                othersStaffSpinnerAdapter = new OthersStaffSpinnerAdapter(DiaryAdd.this,
                                        listStaff, tv_others_staff);
                                spinner_others_staff.setAdapter(othersStaffSpinnerAdapter);

                                progressDialog.dismiss();

                                return;
                            }

                            staffData = new StaffData();
                            staffData.setName("Select Staff");
                            listStaff.add(staffData);


                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                staffData = new StaffData();

                                staffData.setId(object.optString("id"));
                                staffData.setName(object.optString("name"));
                                staffData.setInstitute_id(object.optString("institute_id"));
                                staffData.setGender(object.optString("gender"));
                                staffData.setDob(object.optString("dob"));
                                staffData.setMobile(object.optString("phone_no"));
                                staffData.setEmail(object.optString("email"));
                                staffData.setStreet(object.optString("address"));
                                staffData.setCity(object.optString("city"));
                                staffData.setState(object.optString("state"));
                                staffData.setCountry(object.optString("country"));
                                staffData.setPincode(object.optString("pincode"));
                                staffData.setImage(object.optString("image"));


                                listStaff.add(staffData);


                            }

                            othersStaffSpinnerAdapter = new OthersStaffSpinnerAdapter(DiaryAdd.this,
                                            listStaff, tv_others_staff);
                            spinner_others_staff.setAdapter(othersStaffSpinnerAdapter);


                        }else {

                            Toasty.info(getApplicationContext(),
                                    "No Staff found",
                                    Toast.LENGTH_SHORT, true).show();


                            staffData = new StaffData();
                            staffData.setName("No Staff found");
                            listStaff.add(staffData);

                            othersStaffSpinnerAdapter = new OthersStaffSpinnerAdapter(DiaryAdd.this,
                                    listStaff, tv_others_staff);
                            spinner_others_staff.setAdapter(othersStaffSpinnerAdapter);

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
                Log.d(Commons.TAG, "get_all_staffs- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getApplicationContext()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }

    private void getStudentsAndGuardianClassAndSectionWise(){

        listStudent = new ArrayList<>();
        listGuardian = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_student_list;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.class_id, class_id);
        params.put(ApiClient.section_id, section_id);


        Log.d(Constants.TAG , "get_student_list - " + url);
        Log.d(Constants.TAG , "get_student_list - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_student_list- " + response.toString());

                if (response != null) {
                    try {

                        StudentData studentData = new StudentData();

                        GuardianData guardianData = new GuardianData();

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No Student found",
                                        Toast.LENGTH_SHORT, true).show();

                                studentData.setName("No Student found");
                                studentData.setRoll_no("");
                                listStudent.add(studentData);

                                studentsSpinnerAdapter =
                                        new StudentsSpinnerAdapter(DiaryAdd.this,
                                                listStudent, tv_students);
                                spinner_student.setAdapter(studentsSpinnerAdapter);




                                guardianData.setName("No Guardian found");
                                listGuardian.add(guardianData);

                                guardiansSpinnerAdapter = new GuardiansSpinnerAdapter(DiaryAdd.this,
                                        listGuardian, tv_guardians);
                                spinner_guardian.setAdapter(guardiansSpinnerAdapter);



                                return;
                            }

                            studentData.setName("Select Student");
                            studentData.setRoll_no("");
                            listStudent.add(studentData);


                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                studentData = new StudentData();

                                studentData.setId(object.optString("id"));
                                studentData.setName(object.optString("name"));
                                studentData.setRoll_no(object.optString("roll_no"));
                                studentData.setMobile(object.optString("phone_no"));
                                studentData.setEmail(object.optString("email"));
                                studentData.setImage(object.optString("image"));

                                listStudent.add(studentData);

                            }

                            //////////////////////////////////////

                            JSONArray guardian_info = response.getJSONArray("guardian_info");


                            guardianData = new GuardianData();
                            guardianData.setName("Select Guardian");
                            listGuardian.add(guardianData);

                            for (int i = 0; i < guardian_info.length(); i++){
                                JSONObject object1 = guardian_info.getJSONObject(i);

                                guardianData = new GuardianData();

                                guardianData.setId(object1.optString("id"));
                                guardianData.setName(object1.optString("name"));
                                guardianData.setMobile(object1.optString("phone_no"));
                                guardianData.setStreet(object1.optString("address"));
                                guardianData.setImage(object1.optString("image"));
                                guardianData.setRelation(object1.optString("relation"));

                                guardianData.setStudent_name(object1.optString("student_name"));
                                guardianData.setClass_name(object1.optString("class_name"));
                                guardianData.setSection_name(object1.optString("section_name"));


                                listGuardian.add(guardianData);

                            }

                            guardiansSpinnerAdapter = new GuardiansSpinnerAdapter(DiaryAdd.this,
                                    listGuardian, tv_guardians);
                            spinner_guardian.setAdapter(guardiansSpinnerAdapter);



                            studentsSpinnerAdapter =
                                    new StudentsSpinnerAdapter(DiaryAdd.this,
                                            listStudent, tv_students);
                            spinner_student.setAdapter(studentsSpinnerAdapter);

                        }else {

                            Toasty.info(getApplicationContext(),
                                    "No Student found",
                                    Toast.LENGTH_SHORT, true).show();


                            studentData.setName("No Student found");
                            studentData.setRoll_no("");
                            listStudent.add(studentData);

                            studentsSpinnerAdapter =
                                    new StudentsSpinnerAdapter(DiaryAdd.this,
                                            listStudent, tv_students);
                            spinner_student.setAdapter(studentsSpinnerAdapter);



                            guardianData.setName("No Guardian found");
                            listGuardian.add(guardianData);

                            guardiansSpinnerAdapter = new GuardiansSpinnerAdapter(DiaryAdd.this,
                                    listGuardian, tv_guardians);
                            spinner_guardian.setAdapter(guardiansSpinnerAdapter);

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_student_list- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getApplicationContext()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }

    public void createDiary() {

        progressDialog.show();

        String url = ApiClient.create_diary;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.created_by, prefManager.getId());
        params.put(ApiClient.diary_name, edt_dairy_name.getText().toString());
        params.put(ApiClient.user_ids, getIds());


        Log.d(Constants.TAG, "create_diary - " + url);
        Log.d(Constants.TAG, "create_diary - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "create_diary- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1) {

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                           // Commons.restartActivity(DiaryAdd.this);

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
                Log.d(Commons.TAG, "create_diary- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(DiaryAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }

    private String getIds(){

        ArrayList<String> all_ids_list = new ArrayList<>();
        String ids = "";

        try {

            if (teachersSpinnerAdapter != null){

                all_ids_list.addAll(teachersSpinnerAdapter.getSelectedTeacherIdsList());
            }

            if (othersStaffSpinnerAdapter != null){

                all_ids_list.addAll(othersStaffSpinnerAdapter.getSelectedStaffIdsList());
            }

            if (studentsSpinnerAdapter != null){

                all_ids_list.addAll(studentsSpinnerAdapter.getSelectedStudentsIdsList());
            }

            if (guardiansSpinnerAdapter != null){

                all_ids_list.addAll(guardiansSpinnerAdapter.getSelectedGuardianIdsList());
            }


            ids = TextUtils.join(",", all_ids_list);

            ids = ids + "," +prefManager.getInstitute_id();


        }catch (Exception e){
            e.printStackTrace();
        }

        return ids;
    }


}
