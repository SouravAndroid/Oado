package com.oado.activity;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.ExamListAdapter;
import com.oado.models.ExamListData;
import com.oado.models.ExamResultData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.HidingScrollListener;
import com.oado.utils.PrefManager;
import com.oado.utils.StaticText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ExamList extends AppCompatActivity implements SearchView.OnQueryTextListener{

    @BindView(R.id.recycler_view_exam)
    RecyclerView recycler_view_exam;

    @BindView(R.id.searchView)
    SearchView searchView;

    @BindView(R.id.tv_student_info)
    TextView tv_student_info;



    Filter filter;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    ArrayList<ExamListData> arrayList;
    ExamListAdapter examListAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_list);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        prefManager = new PrefManager(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view_exam.setLayoutManager(mLayoutManager);
        recycler_view_exam.setItemAnimator(new DefaultItemAnimator());

        searchView.setVisibility(View.GONE);


        recycler_view_exam.addOnScrollListener(new HidingScrollListener() {
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

            String s_id = bundle.getString("data");

            getExamList(s_id);
        }


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


    private void getExamList(String s_id){

        arrayList = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_student_exam_result;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.student_id, s_id);


        Log.d(Constants.TAG , "get_student_exam_result - " + url);
        Log.d(Constants.TAG , "get_student_exam_result - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_student_exam_result- " + response.toString());

                if (response != null) {
                    try {

                        ExamListData examListData;
                        ExamResultData examResultData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONObject info = response.getJSONObject("info");

                            String name = info.optString("name");
                            String email = info.optString("email");
                            String phone_no = info.optString("phone_no");
                            String roll_no = info.optString("roll_no");
                            String image = info.optString("image");
                            String class_name = info.optString("class_name");
                            String section_name = info.optString("section_name");


                            tv_student_info.setText("Name: "+ name
                                    + ", Roll: " +roll_no
                                    + ",\nClass: "+class_name
                                    + ", Section: "+section_name );


                            if (!info.has("exam_name_list")) {

                                Toasty.info(getApplicationContext(),
                                        "No exam report found",
                                        Toast.LENGTH_SHORT, true).show();
                                searchView.setVisibility(View.GONE);

                                searchView.setVisibility(View.GONE);
                                progressDialog.dismiss();

                                return;

                            }

                            JSONArray exam_name_list = info.getJSONArray("exam_name_list");

                            if (exam_name_list.length() == 0){
                                Toasty.info(getApplicationContext(),
                                        "No exam report found",
                                        Toast.LENGTH_SHORT, true).show();
                                searchView.setVisibility(View.GONE);

                                progressDialog.dismiss();

                                return;
                            }


                            searchView.setVisibility(View.VISIBLE);

                            for (int i = 0; i < exam_name_list.length(); i++){
                                JSONObject object = exam_name_list.getJSONObject(i);

                                examListData = new ExamListData();

                                examListData.setId(object.optString("id"));
                                examListData.setName(object.optString("exam_name"));

                                examListData.setStudent_name(name);
                                examListData.setStudent_email(email);
                                examListData.setStudent_phone(phone_no);
                                examListData.setImage(image);
                                examListData.setStudent_roll(roll_no);
                                examListData.setClass_name(class_name);
                                examListData.setSection_name(section_name);


                                JSONArray score_details = object.getJSONArray("score_details");
                                ArrayList<ExamResultData> list_ExamResult = new ArrayList<>();
                                for (int j = 0; j < score_details.length(); j++){
                                    JSONObject object1 = score_details.getJSONObject(j);

                                    examResultData = new ExamResultData();

                                    examResultData.setScored_marks(object1.optString("scored_marks"));
                                    examResultData.setTotal_marks(object1.optString("total_marks"));
                                    examResultData.setSubject_name(object1.optString("subject_name"));
                                    examResultData.setSubject_id(object1.optString("subject_id"));


                                    list_ExamResult.add(examResultData);

                                }

                                examListData.setList_ExamResult(list_ExamResult);

                                arrayList.add(examListData);

                            }


                            examListAdapter = new ExamListAdapter(ExamList.this, arrayList);
                            recycler_view_exam.setAdapter(examListAdapter);
                            examListAdapter.getFilter();

                            if (arrayList.size() > 8){
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
                Log.d(Commons.TAG, "get_student_exam_result- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getApplicationContext()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }

}
