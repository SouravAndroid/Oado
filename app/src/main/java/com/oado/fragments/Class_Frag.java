package com.oado.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.activity.ClassAdd;
import com.oado.adapters.ClassListAdapter;
import com.oado.models.ClassData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.HidingScrollListener;
import com.oado.utils.PrefManager;
import com.oado.utils.StaticText;
import com.oado.utils.UserPermissionCheck;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class Class_Frag extends Fragment implements SearchView.OnQueryTextListener{

    public Class_Frag() { }


    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.fab_add)
    FloatingActionButton fab_add;

    @BindView(R.id.searchView)
    SearchView searchView;

    Filter filter;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    ArrayList<ClassData> classDataArrayList;
    ClassListAdapter adapter;

    UserPermissionCheck userPermissionCheck;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_class, container, false);
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
        userPermissionCheck = new UserPermissionCheck(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ClassAdd.class);
                intent.putExtra("type", "add");
                startActivity(intent);
            }
        });


        recycler_view.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                searchView.setVisibility(View.GONE);
            }
            @Override
            public void onShow() {
                searchView.setVisibility(View.VISIBLE);

            }
        });


        /// permission
        if (userPermissionCheck.isAdmin() || userPermissionCheck.isInstitute()){
            fab_add.setVisibility(View.VISIBLE);
        }else {
            fab_add.setVisibility(View.GONE);
        }

        setupSearchView();

        hideSoftKeyboard(searchView);

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

        getAllClass();

        super.onResume();
    }

    private void getAllClass(){

        classDataArrayList = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_all_classes;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());

        if (userPermissionCheck.isTeacher()){
            params.put(ApiClient.teacher_id, prefManager.getId());
        }


        Log.d(Constants.TAG , "get_all_classes - " + url);
        Log.d(Constants.TAG , "get_all_classes - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_all_classes- " + response.toString());

                if (response != null) {
                    try {

                        ClassData classData;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getActivity(),
                                        "No class here",
                                        Toast.LENGTH_SHORT, true).show();
                                searchView.setVisibility(View.GONE);

                                progressDialog.dismiss();

                                return;
                            }

                            classDataArrayList.clear();

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                classData = new ClassData();

                                classData.setId(object.optString("id"));
                                classData.setName(object.optString("class_name"));
                                classData.setInstitute_id(object.optString("institute_id"));



                                if (userPermissionCheck.isStudent() ||
                                        userPermissionCheck.isGuardian()){

                                    if (prefManager.getClass_id()
                                            .equals(object.optString("id"))){

                                        classDataArrayList.add(classData);
                                    }


                                }else {

                                    classDataArrayList.add(classData);
                                }

                            }


                            adapter = new ClassListAdapter(getActivity(), classDataArrayList);
                            recycler_view.setAdapter(adapter);
                            filter = adapter.getFilter();

                            if (classDataArrayList.size() > 10){
                                searchView.setVisibility(View.VISIBLE);
                            }else {
                                searchView.setVisibility(View.GONE);
                            }

                        }else {

                            Toasty.info(getActivity(),
                                    "No class here",
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
                Log.d(Commons.TAG, "get_all_classes- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }



    private static void hideSoftKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager)
                    view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}
