package com.oado.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.RelativeLayout;

import com.oado.R;
import com.oado.adapters.InboxListAdapter;
import com.oado.adapters.TrashListAdapter;
import com.oado.utils.HidingScrollListener;
import com.oado.utils.StaticText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TrashMessages extends Fragment implements SearchView.OnQueryTextListener {

    public TrashMessages() { }

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.searchView)
    SearchView searchView;

    Toolbar toolbar;
    FloatingActionButton fab_sent_message;
    AppBarLayout appBarLayout;



    Filter filter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_inbox, container, false);
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


        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");

        TrashListAdapter adapter = new TrashListAdapter(getActivity(), arrayList);
        recycler_view.setAdapter(adapter);
        filter = adapter.getFilter();



        setupSearchView();

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




}
