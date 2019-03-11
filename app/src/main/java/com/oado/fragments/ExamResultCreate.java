package com.oado.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.ExamResultEntryAdapter;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.adapters.StudentSpinnerAdapter;
import com.oado.models.ExamScoreData;
import com.oado.models.SectionData;
import com.oado.models.StudentData;
import com.oado.models.SubjectData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.NonScrollListView;
import com.oado.utils.PrefManager;
import com.oado.utils.ValidationClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ExamResultCreate extends Fragment {
    public ExamResultCreate() { }

    Unbinder unbinder;

    @BindView(R.id.spinner_class)
    Spinner spinner_class;

    @BindView(R.id.spinner_section)
    Spinner spinner_section;

    @BindView(R.id.spinner_student)
    Spinner spinner_student;

    @BindView(R.id.edt_exam_type)
    EditText edt_exam_type;

    @BindView(R.id.listView)
    NonScrollListView listView;

    @BindView(R.id.rel_create_examResult)
    RelativeLayout rel_create_examResult;


    ArrayList<HashMap<String, String>> list_Class;
    ArrayList<HashMap<String, String>> list_Section;
    ArrayList<SectionData> sectionDataArrayList;
    ArrayList<SubjectData> subjectDataArrayList;

    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    MySpinnerAdapter spinnerAdapter;
    ExamResultEntryAdapter resultEntryAdapter;
    ArrayList<StudentData> listStudent;

    String class_id, class_name, section_id, section_name, student_id;






    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exam_result_create, container, false);
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

        validationClass = new ValidationClass(getActivity());
        prefManager = new PrefManager(getActivity());
        globalClass = (GlobalClass) getActivity().getApplicationContext();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);




        listStudent = new ArrayList<>();

        StudentData studentData = new StudentData();
        studentData.setName("Select Student");
        studentData.setRoll_no("");
        listStudent.add(studentData);

        StudentSpinnerAdapter studentsSpinnerAdapter =
                new StudentSpinnerAdapter(getActivity(), listStudent);
        spinner_student.setAdapter(studentsSpinnerAdapter);



        setClassData();

        clickOnViews();

    }

    public void clickOnViews(){

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    class_id = list_Class.get(position).get("id");

                    setSectionData(class_id);

                    if (class_id != null && section_id != null){
                        getStudentsClassAndSectionWise();
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
                //String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    section_id = list_Section.get(position).get("id");

                    //setSubjectData(section_id);

                    if (class_id != null && section_id != null){
                        getStudentsClassAndSectionWise();
                    }

                }else {
                    section_id = null;

                    //setSubjectData("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_student.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    student_id = listStudent.get(position).getId();

                    getStudentWiseSubject(student_id);

                }else {
                    student_id = null;

                    subjectDataArrayList = new ArrayList<>();

                    resultEntryAdapter = new ExamResultEntryAdapter(getActivity(),
                            subjectDataArrayList);
                    listView.setAdapter(resultEntryAdapter);
                    resultEntryAdapter.notifyDataSetChanged();


                    if (subjectDataArrayList.size() == 0){
                        rel_create_examResult.setVisibility(View.GONE);
                    }else {
                        rel_create_examResult.setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        rel_create_examResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (student_id == null) {
                    Toasty.info(getActivity(),
                            "Select Student",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }

                if (edt_exam_type.getText().toString().trim().length() == 0) {
                    Toasty.info(getActivity(),
                            "Enter exam name",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }


                createExamResult();


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

            spinnerAdapter = new MySpinnerAdapter(getActivity(),
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

            spinnerAdapter = new MySpinnerAdapter(getActivity(),
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

                break;
            }

        }

        if (sectionDataArrayList.size() == 0){

            hashMap.put("name", "No Section here");
            list_Section.add(hashMap);


            spinnerAdapter = new MySpinnerAdapter(getActivity(),
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

            spinnerAdapter = new MySpinnerAdapter(getActivity(),
                    list_Section);
            spinner_section.setAdapter(spinnerAdapter);


        }

    }


    private void setSubjectData(String section_id_){

        subjectDataArrayList = new ArrayList<>();

        for (int i = 0; i < sectionDataArrayList.size(); i++){

            if (section_id_.equals(sectionDataArrayList.get(i).getId())){

                subjectDataArrayList = sectionDataArrayList.get(i).getList_Subject();

                break;
            }

        }

        Log.d(Commons.TAG, "LLL = "+subjectDataArrayList.size());

        resultEntryAdapter = new ExamResultEntryAdapter(getActivity(),
                subjectDataArrayList);
        listView.setAdapter(resultEntryAdapter);
        resultEntryAdapter.notifyDataSetChanged();



        if (subjectDataArrayList.size() == 0){
            rel_create_examResult.setVisibility(View.GONE);
        }else {
            rel_create_examResult.setVisibility(View.VISIBLE);
        }

    }



    private void getStudentsClassAndSectionWise(){

        listStudent = new ArrayList<>();

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

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getActivity(),
                                        "No Student found",
                                        Toast.LENGTH_LONG, true).show();

                                progressDialog.dismiss();

                                studentData.setName("No Student");
                                studentData.setRoll_no("");
                                listStudent.add(studentData);

                                StudentSpinnerAdapter studentsSpinnerAdapter =
                                        new StudentSpinnerAdapter(getActivity(), listStudent);
                                spinner_student.setAdapter(studentsSpinnerAdapter);


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

                            StudentSpinnerAdapter studentsSpinnerAdapter =
                                    new StudentSpinnerAdapter(getActivity(), listStudent);
                            spinner_student.setAdapter(studentsSpinnerAdapter);

                        }else {

                            Toasty.info(getActivity(),
                                    "No Student found",
                                    Toast.LENGTH_SHORT, true).show();


                            studentData.setName("No Student");
                            studentData.setRoll_no("");
                            listStudent.add(studentData);

                            StudentSpinnerAdapter studentsSpinnerAdapter =
                                    new StudentSpinnerAdapter(getActivity(), listStudent);
                            spinner_student.setAdapter(studentsSpinnerAdapter);

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
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }

    private void getStudentWiseSubject(String student_id){

        subjectDataArrayList = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_subject_list_student;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.student_id, student_id);

        Log.d(Constants.TAG , "get_subject_list_student - " + url);
        Log.d(Constants.TAG , "get_subject_list_student - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_subject_list_student- " + response.toString());

                if (response != null) {
                    try {

                        SubjectData subjectData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){

                                Toasty.info(getActivity(),
                                        "No Subject found",
                                        Toast.LENGTH_SHORT, true).show();

                                return;
                            }


                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                subjectData = new SubjectData();

                                subjectData.setId(object.optString("id"));
                                subjectData.setSubject_name(object.optString("subject_name"));

                                subjectDataArrayList.add(subjectData);

                            }

                            resultEntryAdapter = new ExamResultEntryAdapter(getActivity(),
                                    subjectDataArrayList);
                            listView.setAdapter(resultEntryAdapter);
                            resultEntryAdapter.notifyDataSetChanged();



                            if (subjectDataArrayList.size() == 0){
                                rel_create_examResult.setVisibility(View.GONE);
                            }else {
                                rel_create_examResult.setVisibility(View.VISIBLE);
                            }

                        }else {

                            Toasty.info(getActivity(),
                                    "No Subject found",
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
                Log.d(Commons.TAG, "get_subject_list_student- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }

    private void createExamResult(){

        progressDialog.show();

        String url = ApiClient.create_exam_result;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.class_id, class_id);
        params.put(ApiClient.section_id, section_id);
        params.put(ApiClient.student_id, student_id);
        params.put(ApiClient.exam_name, edt_exam_type.getText().toString());
        params.put(ApiClient.exam_score_arr, getScoreData());


        Log.d(Constants.TAG , "create_exam_result - " + url);
        Log.d(Constants.TAG , "create_exam_result - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "create_exam_result- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getActivity(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();

                            progressDialog.dismiss();

                            setClassData();
                            edt_exam_type.setText("");



                            subjectDataArrayList = new ArrayList<>();

                            resultEntryAdapter = new ExamResultEntryAdapter(getActivity(),
                                    subjectDataArrayList);
                            listView.setAdapter(resultEntryAdapter);
                            resultEntryAdapter.notifyDataSetChanged();


                            if (subjectDataArrayList.size() == 0){
                                rel_create_examResult.setVisibility(View.GONE);
                            }else {
                                rel_create_examResult.setVisibility(View.VISIBLE);
                            }



                        }else {

                            Toasty.info(getActivity(),
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
                Log.d(Commons.TAG, "create_exam_result- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }

    public String getScoreData(){

        JSONArray jsonArray_main = new JSONArray();
        ArrayList<ExamScoreData> dataArrayList;

        if (resultEntryAdapter != null){

            dataArrayList = resultEntryAdapter.getScoreDataArrayList();

        }else {

            return jsonArray_main.toString();

        }



        try {

            for (int i = 0; i < dataArrayList.size(); i++){

                ExamScoreData examScoreData = dataArrayList.get(i);

                JSONObject object1 = new JSONObject();

                object1.put("subject_id", examScoreData.getId());
                object1.put("total_marks", examScoreData.getFull_marks());
                object1.put("scored_marks", examScoreData.getMarks());

                jsonArray_main.put(object1);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonArray_main.toString();

    }


}
