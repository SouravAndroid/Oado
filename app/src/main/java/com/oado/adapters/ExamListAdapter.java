package com.oado.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.oado.activity.ExamResultView;
import com.oado.models.ExamListData;

import java.util.ArrayList;

public class ExamListAdapter extends RecyclerView.Adapter<ExamListAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private ArrayList<ExamListData> originalList;
    private ArrayList<ExamListData> filteredArrayList;
    private ProgressDialog progressDialog;


    public ExamListAdapter(Context context, ArrayList<ExamListData> itemList) {
        this.context = context;
        this.originalList = itemList;
        this.filteredArrayList = itemList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        RelativeLayout rel_main;
        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            rel_main = view.findViewById(R.id.rel_main);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_name_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@Nullable MyViewHolder holder, final int position) {

        ExamListData examListData = filteredArrayList.get(position);

        holder.tv_name.setText(examListData.getName());


        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExamListData examListData1 = filteredArrayList.get(position);

                Intent intent = new Intent(context, ExamResultView.class);
                intent.putExtra("data", examListData1);
                context.startActivity(intent);

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
                    ArrayList<ExamListData> filteredList = new ArrayList<>();
                    for (ExamListData row : originalList) {

                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getName().toLowerCase().contains(charSequence)) {
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

                    filteredArrayList = (ArrayList<ExamListData>) filterResults.values;

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
