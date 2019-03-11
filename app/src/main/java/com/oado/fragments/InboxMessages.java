package com.oado.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.InboxListAdapter;
import com.oado.models.DiaryMessage;
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
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class InboxMessages extends Fragment implements SearchView.OnQueryTextListener {

    public InboxMessages() { }

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.searchView)
    SearchView searchView;

    Toolbar toolbar;
    FloatingActionButton fab_sent_message;
    AppBarLayout appBarLayout;

    Filter filter;
    ArrayList<DiaryMessage> list_Diary;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;

    InboxListAdapter inboxListAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_inbox, container, false);
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

        toolbar = getActivity().findViewById(R.id.toolbar);
        fab_sent_message = getActivity().findViewById(R.id.fab_sent_message);
        appBarLayout = getActivity().findViewById(R.id.appBarLayout);


        recycler_view.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }
            @Override
            public void onShow() {
                showViews();

            }
        });



        setupSearchView();

        getReceiveMessage();

    }


    private void hideViews() {
        searchView.animate().translationY(-searchView.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        //appBarLayout.setExpanded(false, true);
        searchView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fab_sent_message.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        fab_sent_message.animate().translationY(fab_sent_message.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        searchView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        searchView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

        //appBarLayout.setExpanded(true, true);
        fab_sent_message.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
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


    ////////////////


    private void getReceiveMessage(){

        list_Diary = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_received_messages;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.id, prefManager.getId());


        Log.d(Constants.TAG , "get_received_messages - " + url);
        Log.d(Constants.TAG , "get_received_messages - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_received_messages- " + response.toString());

                if (response != null) {
                    try {

                        DiaryMessage diaryMessage;

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getActivity(),
                                        "No message here",
                                        Toast.LENGTH_SHORT, true).show();

                                searchView.setVisibility(View.GONE);

                                progressDialog.dismiss();

                                return;
                            }

                            list_Diary.clear();

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                diaryMessage = new DiaryMessage();

                                diaryMessage.setId(object.optString("id"));
                                diaryMessage.setMessage_type(object.optString("message_type"));
                                diaryMessage.setMessage(object.optString("message"));
                                diaryMessage.setPhoto(object.optString("photo"));
                                diaryMessage.setLink(object.optString("link"));
                                diaryMessage.setYoutube_link(object.optString("youtube_link"));
                                diaryMessage.setName(object.optString("name"));
                                diaryMessage.setImage(object.optString("image"));
                                diaryMessage.setUser_type(object.optString("user_type"));
                                diaryMessage.setCreated_date(object.optString("created_date"));

                                list_Diary.add(diaryMessage);

                            }


                            inboxListAdapter = new InboxListAdapter(getActivity(), list_Diary);
                            recycler_view.setAdapter(inboxListAdapter);
                            filter = inboxListAdapter.getFilter();
                            searchView.setVisibility(View.VISIBLE);

                        }else {

                            Toasty.info(getActivity(),
                                    "No message here",
                                    Toast.LENGTH_SHORT, true).show();
                            searchView.setVisibility(View.GONE);
                        }

                        getActivity().getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_received_messages- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }



}
