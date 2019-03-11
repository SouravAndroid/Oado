package com.oado.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.oado.activity.DiaryAdd;
import com.oado.adapters.DiaryListAdapter;
import com.oado.models.ClassData;
import com.oado.models.DiaryData;
import com.oado.models.DiaryMembersData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.PrefManager;
import com.oado.utils.UserPermissionCheck;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class Diary extends Fragment {
    public Diary() { }

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.fab_add)
    FloatingActionButton fab_add;


    ProgressDialog progressDialog;
    PrefManager prefManager;
    ArrayList<DiaryData> list_Diary;
    DiaryListAdapter diaryListAdapter;
    UserPermissionCheck userPermissionCheck;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_dairy, container, false);
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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        userPermissionCheck = new UserPermissionCheck(getActivity());

        if (userPermissionCheck.isAdmin() || userPermissionCheck.isInstitute()){
            fab_add.setVisibility(View.VISIBLE);
        }else {
            fab_add.setVisibility(View.GONE);
        }


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), DiaryAdd.class);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onResume() {

        getMyDiary();

        super.onResume();
    }

    private void getMyDiary(){

        list_Diary = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_my_diary;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        //params.put(ApiClient.user_id, prefManager.getId());
        params.put(ApiClient.user_id, prefManager.getInstitute_id());


        Log.d(Constants.TAG , "get_my_diary - " + url);
        Log.d(Constants.TAG , "get_my_diary - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_my_diary- " + response.toString());

                if (response != null) {
                    try {

                        DiaryData diaryData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getActivity(),
                                        "No diary here",
                                        Toast.LENGTH_SHORT, true).show();

                                progressDialog.dismiss();

                                return;
                            }

                            list_Diary.clear();

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                diaryData = new DiaryData();

                                diaryData.setId(object.optString("id"));
                                diaryData.setName(object.optString("diary_name"));
                                diaryData.setInstitute_id(object.optString("institute_id"));
                                diaryData.setCreated_by(object.optString("created_by"));
                                diaryData.setUser_ids(object.optString("user_ids"));



                                ArrayList<DiaryMembersData> dataArrayList = new ArrayList<>();
                                JSONArray members_arr = object.getJSONArray("members_arr");
                                for (int j = 0; j < members_arr.length(); j++){
                                    JSONObject object1 = members_arr.getJSONObject(j);

                                    DiaryMembersData membersData = new DiaryMembersData();

                                    membersData.setId(object1.optString("id"));
                                    membersData.setName(object1.optString("name"));
                                    membersData.setEmail(object1.optString("email"));
                                    membersData.setMobile(object1.optString("phone_no"));
                                    membersData.setUser_type(object1.optString("type"));
                                    membersData.setImage(object1.optString("image"));


                                    dataArrayList.add(membersData);

                                }

                                diaryData.setList_Members(dataArrayList);

                                list_Diary.add(diaryData);

                            }

                            diaryListAdapter = new DiaryListAdapter(getActivity(), list_Diary);
                            recycler_view.setAdapter(diaryListAdapter);

                        }else {

                            Toasty.info(getActivity(),
                                    "No diary here",
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
                Log.d(Commons.TAG, "get_my_diary- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }


}
