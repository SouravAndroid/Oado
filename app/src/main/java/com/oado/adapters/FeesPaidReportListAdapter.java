package com.oado.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oado.R;
import com.oado.models.FeesReportData;

import java.util.ArrayList;

public class FeesPaidReportListAdapter extends RecyclerView.Adapter<FeesPaidReportListAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private ArrayList<FeesReportData> originalList;
    private ArrayList<FeesReportData> filteredArrayList;

    public FeesPaidReportListAdapter(Context context, ArrayList<FeesReportData> itemList) {
        this.context = context;
        this.originalList = itemList;
        this.filteredArrayList = itemList;


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_pay_date, tv_price;
        RelativeLayout rel_main;
        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_pay_date = view.findViewById(R.id.tv_pay_date);
            tv_price = view.findViewById(R.id.tv_price);
            rel_main = view.findViewById(R.id.rel_main);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fees_report_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@Nullable MyViewHolder holder, final int position) {

        FeesReportData feesReportData = filteredArrayList.get(position);

        holder.tv_name.setText("Month: "+feesReportData.getMonth_name());
        holder.tv_pay_date.setText("Pay date: "+feesReportData.getPay_date());
        holder.tv_price.setText(feesReportData.getPay_amount());

        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });



    }

    @Override
    public int getItemCount() {
        return filteredArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredArrayList = originalList;
                } else {
                    ArrayList<FeesReportData> filteredList = new ArrayList<>();
                    for (FeesReportData row : originalList) {

                        // here we are looking for name or phone number match
                        if (row.getMonth_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filteredArrayList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (charSequence != null) {

                    filteredArrayList = (ArrayList<FeesReportData>) filterResults.values;

                    if (filteredArrayList != null){
                        notifyDataSetChanged();
                    }else {
                        filteredArrayList = new ArrayList<>();
                    }

                }else {
                    filteredArrayList = originalList;

                    notifyDataSetChanged();
                }
            }
        };
    }






}
