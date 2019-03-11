package com.oado.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.oado.adapters.HomeFrag_Group_Adapter;
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

public class HomeFrag_Group extends Fragment {


    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;


    HomeFrag_Group_Adapter adapter;
    ArrayList<String> arrayList;
    ArrayList<Integer> iconsList;
    PrefManager prefManager;
    GlobalClass globalClass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home_group, container, false);
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2,
                GridLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(gridLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());



        arrayList = new ArrayList<>();
        arrayList.add("Announcement");
        arrayList.add("Event");
        arrayList.add("Photo");
        arrayList.add("Link");
        arrayList.add("Youtube Video");
        arrayList.add("Inbox");

        iconsList = new ArrayList<>();
        iconsList.add(R.mipmap.announcement_icon);
        iconsList.add(R.mipmap.event_icon);
        iconsList.add(R.mipmap.photo_icon);
        iconsList.add(R.mipmap.link_icon);
        iconsList.add(R.mipmap.video_icon);
        iconsList.add(R.mipmap.message_icon);


        adapter = new HomeFrag_Group_Adapter(getActivity(), arrayList, iconsList);
        recycler_view.setAdapter(adapter);


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

    @Override
    public void onResume() {

        try {
            getActivity().registerReceiver(mMessageReceiver,
                    new IntentFilter(Constants.message_coming_count));
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if (adapter != null){
            adapter.notifyDataSetChanged();
        }else {
            adapter = new HomeFrag_Group_Adapter(getActivity(), arrayList, iconsList);
            recycler_view.setAdapter(adapter);
        }

        super.onResume();
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");
            Log.d(Constants.TAG, "type = "+type);

            /*Toasty.info(getActivity(),
                    "received",
                    Toast.LENGTH_LONG, true).show();*/

            if (adapter != null){
                adapter.notifyDataSetChanged();
            }else {
                adapter = new HomeFrag_Group_Adapter(getActivity(), arrayList, iconsList);
                recycler_view.setAdapter(adapter);
            }


            getDiaryMessages();


        }

    };



    private void getDiaryMessages(){

        final ArrayList<DiaryMessage> list_DiaryMessages = new ArrayList<>();


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

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_diary_message- " + res);

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
