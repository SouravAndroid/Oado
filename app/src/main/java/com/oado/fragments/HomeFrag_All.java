package com.oado.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.HomeFrag_AllListAdapter;
import com.oado.models.DiaryMessage;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class HomeFrag_All extends Fragment {


    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;


    ArrayList<DiaryMessage> list_DiaryMessages;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;

    HomeFrag_AllListAdapter homeFragAllListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home_all, container, false);
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

        prefManager = new PrefManager(getActivity());
        globalClass = (GlobalClass) getActivity().getApplicationContext();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


        getDiaryMessages();

    }


    @Override
    public void onResume() {

        try {
            getActivity().registerReceiver(mMessageReceiver,
                    new IntentFilter(Constants.message_coming_count));
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    public void onPause() {

        try {
            getActivity().unregisterReceiver(mMessageReceiver);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        super.onPause();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
           // String type = intent.getStringExtra("type");
            //do other stuff here


            getDiaryMessages();


        }

    };
    private void getDiaryMessages(){

        list_DiaryMessages = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_diary_message;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.user_id, prefManager.getId());


        Log.d(Constants.TAG , "get_diary_message - " + url);
        Log.d(Constants.TAG , "get_diary_message - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_diary_message- " + response.toString());

                if (response != null) {
                    try {

                        DiaryMessage diaryMessage;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                /*Toasty.info(getActivity(),
                                        "No message here",
                                        Toast.LENGTH_SHORT, true).show();*/

                                progressDialog.dismiss();

                                return;
                            }


                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                diaryMessage = new DiaryMessage();

                                diaryMessage.setId(object.optString("id"));
                                diaryMessage.setInstitute_id(object.optString("institute_id"));
                                diaryMessage.setDiary_name(object.optString("diary_name"));
                                diaryMessage.setCreated_by(object.optString("created_by"));
                                diaryMessage.setUser_ids(object.optString("user_ids"));
                                diaryMessage.setDiary_id(object.optString("diary_id"));
                                diaryMessage.setMessage_type(object.optString("message_type"));
                                diaryMessage.setMessage(object.optString("message"));
                                diaryMessage.setPhoto(object.optString("photo"));
                                diaryMessage.setLink(object.optString("link"));
                                diaryMessage.setYoutube_link(object.optString("youtube_link"));
                                diaryMessage.setEvent_start_date(object.optString("event_start_date"));
                                diaryMessage.setEvent_end_date(object.optString("event_end_date"));
                                diaryMessage.setName(object.optString("name"));
                                diaryMessage.setImage(object.optString("image"));
                                diaryMessage.setUser_type(object.optString("user_type"));

                                diaryMessage.setCreated_date(getShowDateFormat(object.optString("created_date")));

                                list_DiaryMessages.add(diaryMessage);

                            }

                            globalClass.setDiaryMessageArrayList(list_DiaryMessages);

                            homeFragAllListAdapter =
                                    new HomeFrag_AllListAdapter(getActivity(), list_DiaryMessages);
                            recycler_view.setAdapter(homeFragAllListAdapter);

                        }else {

                            /*Toasty.info(getActivity(),
                                    "No data found",
                                    Toast.LENGTH_SHORT, true).show();*/

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_diary_message- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }


    private String getShowDateFormat(String sourceDate){
        String formattedDate = "";
        try {
            DateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            Date date = originalFormat.parse(sourceDate);
            formattedDate = targetFormat.format(date);  //
        }catch (ParseException e){
            e.printStackTrace();
        }

        return formattedDate;
    }

}
