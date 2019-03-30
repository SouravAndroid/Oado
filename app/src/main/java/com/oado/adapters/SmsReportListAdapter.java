package com.oado.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oado.R;
import com.oado.models.SmsReportData;

import java.util.ArrayList;

public class SmsReportListAdapter extends RecyclerView.Adapter<SmsReportListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<SmsReportData> itemList;
    private LayoutInflater inflater;

    public SmsReportListAdapter(Context context, ArrayList<SmsReportData> itemList) {
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_address, tv_sms_count;

        public MyViewHolder(View view) {
            super(view);
            tv_name =  view.findViewById(R.id.tv_name);
            tv_address =  view.findViewById(R.id.tv_address);
            tv_sms_count =  view.findViewById(R.id.tv_sms_count);
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

        SmsReportData smsReportData = itemList.get(position);

        holder.tv_name.setText(smsReportData.getName());

        String address = smsReportData.getAddress()
                + ", " + smsReportData.getCity()
                + ", " + smsReportData.getState()
                + ", " + smsReportData.getCountry()
                + ", " + smsReportData.getPincode();

        holder.tv_address.setText(address);
        holder.tv_sms_count.setText(smsReportData.getTotal_count());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



}
