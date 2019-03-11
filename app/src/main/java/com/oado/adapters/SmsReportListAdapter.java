package com.oado.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oado.R;

import java.util.ArrayList;

public class SmsReportListAdapter extends RecyclerView.Adapter<SmsReportListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> itemList;
    private LayoutInflater inflater;

    public SmsReportListAdapter(Context context, ArrayList<String> itemList) {
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_message, tv_date_time;

        public MyViewHolder(View view) {
            super(view);
            tv_name =  view.findViewById(R.id.tv_name);
            tv_message =  view.findViewById(R.id.tv_message);
            tv_date_time =  view.findViewById(R.id.tv_date_time);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_report_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



}
