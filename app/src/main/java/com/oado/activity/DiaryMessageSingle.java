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
import com.oado.adapters.HomeFrag_AllListAdapter;
import com.oado.models.DiaryMessage;
import com.oado.models.ExamListData;
import com.oado.models.ExamResultData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
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

public class DiaryMessageSingle extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;


    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    ArrayList<DiaryMessage> list_Diary;
    HomeFrag_AllListAdapter homeFragAllListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_message_single);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        prefManager = new PrefManager(this);
        globalClass = (GlobalClass) getApplicationContext();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


        list_Diary = new ArrayList<>();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            String type = bundle.getString("type");
            String Name = bundle.getString("name");

            getSupportActionBar().setTitle(Name);

            setCount(Integer.parseInt(type));

            for (int i = 0; i < globalClass.getDiaryMessageArrayList().size(); i++){
                DiaryMessage diaryMessage = globalClass.getDiaryMessageArrayList().get(i);

                if (type.equals(diaryMessage.getMessage_type())){

                    list_Diary.add(diaryMessage);

                }
            }



            homeFragAllListAdapter = new HomeFrag_AllListAdapter(
                    DiaryMessageSingle.this, list_Diary);
            recycler_view.setAdapter(homeFragAllListAdapter);

        }


    }




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


    private void setCount(int type){

        switch (type){

            case 1:

                prefManager.setKeyCounter1(0);

                break;

            case 2:

                prefManager.setKeyCounter2(0);

                break;

            case 3:

                prefManager.setKeyCounter3(0);

                break;

            case 4:

                prefManager.setKeyCounter4(0);

                break;

            case 5:

                prefManager.setKeyCounter5(0);

                break;

        }

    }

}
