package com.oado.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.activity.FeesAdd;
import com.oado.adapters.FeesListAdapter;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.models.FeesData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.PrefManager;
import com.oado.utils.UserPermissionCheck;
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

public class Fees extends Fragment implements FeesListAdapter.ViewClickListener {
    public Fees() { }

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.fab_add)
    FloatingActionButton fab_add;

    @BindView(R.id.spinner_class)
    Spinner spinner_class;

    @BindView(R.id.card_view1)
    CardView card_view1;

    @BindView(R.id.view1) View view1;



    ArrayList<HashMap<String, String>> list_Class;
    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    MySpinnerAdapter spinnerAdapter;
    ArrayList<FeesData> list_Fees;
    FeesListAdapter feesListAdapter;
    String class_id = "";

    UserPermissionCheck userPermissionCheck;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_fees, container, false);
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
        userPermissionCheck = new UserPermissionCheck(getActivity());


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        list_Fees = new ArrayList<>();


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), FeesAdd.class);
                startActivity(intent);
            }
        });


        if (prefManager.getInstituteType().equalsIgnoreCase(ApiClient.coaching_center)){

            setClassData();

        }else if (prefManager.getInstituteType().equalsIgnoreCase(ApiClient.school)){

            card_view1.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);

            //setClassData();
        }




        clickOnViews();


        /// permission ...
        if (userPermissionCheck.isAdmin() || userPermissionCheck.isInstitute()){
            fab_add.setVisibility(View.VISIBLE);
        }else {
            fab_add.setVisibility(View.GONE);
        }

    }


    public void clickOnViews(){

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                class_id = list_Class.get(position).get("id");

                filteredForCoachingCenter();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getFees();

    }

    @Override
    public void onClick(String what_action) {
        if (what_action != null){
            getFees();
        }
    }

    @Override
    public void onResume() {
        getFees();
        super.onResume();
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

            //hashMap.put("name", "Select Class");
            //list_Class.add(hashMap);

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


        Log.d(Commons.TAG, "Class size = "+globalClass.getListClass().size());
        Log.d(Commons.TAG, "list_Class = "+list_Class);

    }

    private void getFees(){

        list_Fees = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_fees_list;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());


        Log.d(Constants.TAG , "get_fees_list - " + url);
        Log.d(Constants.TAG , "get_fees_list - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_fees_list- " + response.toString());

                if (response != null) {
                    try {

                        FeesData feesData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                /*Toasty.info(getActivity(),
                                        "No data found",
                                        Toast.LENGTH_SHORT, true).show();
*/
                                progressDialog.dismiss();

                                return;
                            }


                            list_Fees.clear();

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                feesData = new FeesData();

                                feesData.setId(object.optString("id"));
                                feesData.setInstitute_id(object.optString("institute_id"));
                                feesData.setClass_id(object.optString("class_id"));
                                feesData.setSubject_id(object.optString("subject_id"));
                                feesData.setFees_amount(object.optString("fees_amount"));
                                feesData.setType_of_center(object.optString("type_of_center"));
                                feesData.setClass_name(object.optString("class_name"));
                                feesData.setSubject_name(object.optString("subject_name"));


                                list_Fees.add(feesData);

                            }


                            if (prefManager.getInstituteType()
                                    .matches(ApiClient.coaching_center)){

                                filteredForCoachingCenter();
                            }else {

                                setData();
                            }

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
                Log.d(Commons.TAG, "get_fees_list- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }

    private void setData(){

        feesListAdapter = new FeesListAdapter(getActivity(), list_Fees);
        recycler_view.setAdapter(feesListAdapter);
        feesListAdapter.setOnClickListener(this);

    }

    private void filteredForCoachingCenter(){

        ArrayList<FeesData> list_Fees_filtered = new ArrayList<>();
        for (int j = 0; j < list_Fees.size(); j++){

            FeesData feesData1 = list_Fees.get(j);
            if (class_id.equals(feesData1.getClass_id())
                    && feesData1.getType_of_center().equals(ApiClient.coaching_center)){

                list_Fees_filtered.add(feesData1);
            }

        }

        feesListAdapter = new FeesListAdapter(getActivity(), list_Fees_filtered);
        recycler_view.setAdapter(feesListAdapter);
        feesListAdapter.setOnClickListener(this);

    }

}
