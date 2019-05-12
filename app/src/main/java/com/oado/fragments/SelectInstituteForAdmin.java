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
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.activity.InstituteAdd;
import com.oado.adapters.InstituteListAdapter;
import com.oado.adapters.SelectInstituteAdapter;
import com.oado.models.InstituteData;
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
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SelectInstituteForAdmin extends Fragment {
    public SelectInstituteForAdmin() { }

    Unbinder unbinder;

    @BindView(R.id.spinner_institute)
    Spinner spinner_institute;
    @BindView(R.id.tv_message)
    TextView tv_message;


    Filter filter;
    ProgressDialog progressDialog;
    ArrayList<InstituteData> arrayList;

    PrefManager prefManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_institute_for_admin, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        unbinder = ButterKnife.bind(this, view);

        initViews();



        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // unbind the view to free some memory
        unbinder.unbind();
    }


    private void initViews(){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        prefManager = new PrefManager(getActivity());


        tv_message.setText(getActivity().getResources().getString(R.string.message_for_admin));


        spinner_institute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){

                    prefManager.setInstitute_id(arrayList.get(position).getId());

                }else {

                    prefManager.setInstitute_id("");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getAllInstitute();
    }

    @Override
    public void onResume() {



        super.onResume();
    }



    private void getAllInstitute(){

        arrayList = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_all_institutes;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        Log.d(Constants.TAG , "get_all_institutes - " + url);
        Log.d(Constants.TAG , "get_all_institutes - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_all_institutes- " + response.toString());

                if (response != null) {
                    try {

                        InstituteData instituteData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            arrayList = new ArrayList<>();

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                /*Toasty.info(getActivity(),
                                        "No data found",
                                        Toast.LENGTH_SHORT, true).show();*/

                                instituteData = new InstituteData();
                                instituteData.setName("No Institute Found");

                                arrayList.add(instituteData);

                                SelectInstituteAdapter selectInstituteAdapter =
                                        new SelectInstituteAdapter(getActivity(), arrayList);
                                spinner_institute.setAdapter(selectInstituteAdapter);

                                progressDialog.dismiss();

                                return;
                            }


                            arrayList = new ArrayList<>();

                            instituteData = new InstituteData();
                            instituteData.setName("Select Institute");

                            arrayList.add(instituteData);

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                instituteData = new InstituteData();

                                instituteData.setId(object.optString("id"));
                                instituteData.setName(object.optString("name"));
                                instituteData.setCode(object.optString("institute_code"));
                                instituteData.setBoard_name(object.optString("boardname"));
                                instituteData.setMobile(object.optString("phone_no"));
                                instituteData.setEmail(object.optString("email"));
                                instituteData.setStreet(object.optString("address"));
                                instituteData.setCity(object.optString("city"));
                                instituteData.setState(object.optString("state"));
                                instituteData.setCountry(object.optString("country"));
                                instituteData.setPincode(object.optString("pincode"));
                                instituteData.setInstitute_type(object.optString("type_of_center"));
                                instituteData.setSms_subcription(object.optString("sms_subscription"));
                                instituteData.setImage(object.optString("image"));


                                arrayList.add(instituteData);


                            }


                            SelectInstituteAdapter selectInstituteAdapter =
                                    new SelectInstituteAdapter(getActivity(), arrayList);
                            spinner_institute.setAdapter(selectInstituteAdapter);


                        }else {

                            /*Toasty.info(getActivity(),
                                    "No data found",
                                    Toast.LENGTH_SHORT, true).show();*/

                            instituteData = new InstituteData();
                            instituteData.setName("No Institute Found");

                            arrayList.add(instituteData);

                            SelectInstituteAdapter selectInstituteAdapter =
                                    new SelectInstituteAdapter(getActivity(), arrayList);
                            spinner_institute.setAdapter(selectInstituteAdapter);
                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_all_institutes- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }

}
