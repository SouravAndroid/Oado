package com.oado.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.oado.adapters.FeesAddListAdapter;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.models.SectionData;
import com.oado.models.SubjectData;
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

public class FeesAdd extends AppCompatActivity {

    @BindView(R.id.recycler_view) RecyclerView recycler_view;
    @BindView(R.id.rel_create_fees) RelativeLayout rel_create_fees;
    @BindView(R.id.spinner_class) Spinner spinner_class;
    @BindView(R.id.card_view1) CardView card_view1;
    @BindView(R.id.view1) View view1;


    ArrayList<HashMap<String, String>> list_Class;
    ArrayList<HashMap<String, String>> list_Class2;
    ArrayList<HashMap<String, String>> list_Subject;
    ArrayList<HashMap<String, String>> list_Subject2;
    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    MySpinnerAdapter spinnerAdapter;
    FeesAddListAdapter feesAddListAdapter;
    String class_id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fees_add);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        validationClass = new ValidationClass(this);
        prefManager = new PrefManager(this);
        globalClass = (GlobalClass) getApplicationContext();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());



        if (prefManager.getInstituteType().matches(ApiClient.coaching_center)){

            setClassData();

            setSubjectData("");

        }else if (prefManager.getInstituteType().matches(ApiClient.school)){

            card_view1.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);

            setClassData();
        }


        clickOnViews();

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


    public void clickOnViews(){

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    class_id = list_Class.get(position).get("id");

                    setSubjectData(class_id);

                }else {
                    class_id = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        rel_create_fees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectivityReceiver.isConnected()){

                    if (prefManager.getInstituteType().matches(ApiClient.coaching_center)){

                        if (class_id.isEmpty()){
                            Toasty.info(FeesAdd.this,
                                    "Select class",
                                    Toast.LENGTH_LONG, true).show();
                            return;
                        }


                        createFees();

                    }else {

                        createFees();
                    }



                }else {

                    Toasty.error(FeesAdd.this,
                            getResources().getString(R.string.connect_to_internet),
                            Toast.LENGTH_LONG, true).show();
                }


            }
        });


    }

    private void setClassData(){

        // class set
        list_Class = new ArrayList<>();
        list_Class2 = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();

        if (globalClass.getListClass().size() == 0){

            hashMap.put("name", "No Class here");
            list_Class.add(hashMap);

            spinnerAdapter = new MySpinnerAdapter(FeesAdd.this,
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
                list_Class2.add(hashMap);
            }

            spinnerAdapter = new MySpinnerAdapter(FeesAdd.this,
                    list_Class);
            spinner_class.setAdapter(spinnerAdapter);

        }

        if (prefManager.getInstituteType().matches(ApiClient.school)){

            feesAddListAdapter = new FeesAddListAdapter(FeesAdd.this, list_Class2);
            recycler_view.setAdapter(feesAddListAdapter);
        }


    }

    private void setSubjectData(String class_id){

        // filter subject class id wise ...
        ArrayList<SubjectData> subjectDataArrayList = new ArrayList<>();

        boolean is_exits = false;
        for (int i = 0; i < globalClass.getListClass().size(); i++){
            if (class_id.equals(globalClass.getListClass().get(i).getId())){
                ArrayList<SectionData> list = globalClass.getListClass().get(i).getList_Section();

                for (int j = 0; j < list.size(); j++){
                    ArrayList<SubjectData> list1 = list.get(j).getList_Subject();
                    for (int k = 0; k < list1.size(); k++){
                        SubjectData subjectData = list1.get(k);
                        for (int m = 0; m < subjectDataArrayList.size(); m++){
                            SubjectData subjectData1 = list1.get(m);
                            if (subjectData.getId().equals(subjectData1.getId())){
                                is_exits = true;
                                break;
                            }
                        }

                        if (!is_exits){
                            subjectDataArrayList.add(subjectData);
                        }
                    }
                }
            }
        }
        ///


        list_Subject = new ArrayList<>();
        list_Subject2 = new ArrayList<>();

        HashMap<String, String> hashMap = new HashMap<>();

        if (subjectDataArrayList.size() == 0){

            hashMap.put("name", "No Subject here");
            list_Subject.add(hashMap);

            spinnerAdapter = new MySpinnerAdapter(FeesAdd.this,
                    list_Subject);
           // spinner_class.setAdapter(spinnerAdapter);

        }else {
            hashMap.put("name", "Select Subject");
            list_Subject.add(hashMap);

            for (int i = 0; i < subjectDataArrayList.size(); i++){

                hashMap = new HashMap<>();

                hashMap.put("id", subjectDataArrayList.get(i).getId());
                hashMap.put("name", subjectDataArrayList.get(i).getSubject_name());


                list_Subject.add(hashMap);
                list_Subject2.add(hashMap);

            }

            spinnerAdapter = new MySpinnerAdapter(FeesAdd.this,
                    list_Subject);
           // spinner_class.setAdapter(spinnerAdapter);


        }

        feesAddListAdapter = new FeesAddListAdapter(FeesAdd.this, list_Subject2);
        recycler_view.setAdapter(feesAddListAdapter);


    }




    private void createFees(){

        progressDialog.show();

        String url = ApiClient.add_fees;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.type_of_center, prefManager.getInstituteType());
        params.put(ApiClient.fees_arr, getFeesData());


        Log.d(Constants.TAG , "add_fees - " + url);
        Log.d(Constants.TAG , "add_fees - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_fees- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.info(FeesAdd.this,
                                    message,
                                    Toast.LENGTH_SHORT, true).show();

                            Commons.restartActivity(FeesAdd.this);

                        }else {

                            Toasty.info(FeesAdd.this,
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
                Log.d(Commons.TAG, "add_fees- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(FeesAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }

    public String getFeesData(){

        JSONArray jsonArray_main = new JSONArray();
        ArrayList<HashMap<String, String>> dataArrayList;

        if (feesAddListAdapter != null){

            dataArrayList = feesAddListAdapter.getEntryList();

        }else {

            return jsonArray_main.toString();

        }



        try {

            for (int i = 0; i < dataArrayList.size(); i++){

                HashMap<String, String> hashMap = dataArrayList.get(i);

                JSONObject object1 = new JSONObject();

                if (prefManager.getInstituteType().matches(ApiClient.coaching_center)){

                    object1.put("class_id", class_id);
                    object1.put("subject_id", hashMap.get("class_subject_id"));

                }else if (prefManager.getInstituteType().matches(ApiClient.school)){

                    object1.put("class_id", hashMap.get("class_subject_id"));

                }

                object1.put("fees_amount", hashMap.get("amount"));

                jsonArray_main.put(object1);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonArray_main.toString();

    }



}
