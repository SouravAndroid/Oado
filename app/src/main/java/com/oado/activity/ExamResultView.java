package com.oado.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.oado.R;
import com.oado.adapters.ExamResultViewAdapter;
import com.oado.models.ExamListData;
import com.oado.models.ExamResultData;
import com.oado.models.StudentData;
import com.oado.utils.Commons;
import com.oado.utils.NonScrollListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ExamResultView extends AppCompatActivity {

    TextView tv_student_info, tv_total_scored, tv_total_percent;
    NonScrollListView listView;

    float total_marks = 0, scored_marks = 0;

    ExamListData examListData;

    DecimalFormat precision = new DecimalFormat("0.00");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_result_view);
        initViews();



    }

    public void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tv_student_info = findViewById(R.id.tv_student_info);
        tv_total_scored = findViewById(R.id.tv_total_scored);
        tv_total_percent = findViewById(R.id.tv_total_percent);
        listView = findViewById(R.id.listView);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            examListData = (ExamListData) bundle.getSerializable("data");

            tv_student_info.setText("Name: "+examListData.getStudent_name()
                    + ", Roll No: "+examListData.getStudent_roll());


            ArrayList<ExamResultData> resultDataArrayList =
                    examListData.getList_ExamResult();

            for (int i = 0; i < resultDataArrayList.size(); i++){

                ExamResultData examResultData = resultDataArrayList.get(i);

                scored_marks = scored_marks + Float.parseFloat(examResultData.getScored_marks());
                total_marks = total_marks + Float.parseFloat(examResultData.getTotal_marks());


            }

            tv_total_scored.setText(((int)scored_marks) + "/" + ((int)total_marks));

            float percent = (scored_marks/total_marks) * 100;
            tv_total_percent.setText(precision.format(percent) + "%");


            ExamResultViewAdapter  examResultViewAdapter =
                    new ExamResultViewAdapter(ExamResultView.this, resultDataArrayList);
            listView.setAdapter(examResultViewAdapter);

        }






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
}
