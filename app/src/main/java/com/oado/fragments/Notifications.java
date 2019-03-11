package com.oado.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.oado.R;
import com.oado.adapters.NotificationsListAdapter;
import com.oado.database.DatabaseHelper;
import com.oado.database.MsgData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class Notifications extends Fragment {
    public Notifications() { }

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_notifications, container, false);
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

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());

        ArrayList<MsgData> msgDataArrayList = databaseHelper.getAllMsg();


        NotificationsListAdapter adapter = new NotificationsListAdapter(getActivity(),
                msgDataArrayList);
        recycler_view.setAdapter(adapter);

        if (msgDataArrayList.size() == 0){
            Toasty.info(getActivity(),
                    "No notification here",
                    Toast.LENGTH_SHORT, true).show();
        }

    }





}
