package com.oado.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oado.R;
import com.oado.models.ExamResultData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ExamResultViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ExamResultData> arrayList;
    private LayoutInflater inflater;

    private DecimalFormat precision = new DecimalFormat("0.00");

    public ExamResultViewAdapter(Context context, ArrayList<ExamResultData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);



    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.exam_result_view_item, null);
        ViewHolder holder = new ViewHolder(convertView);

        ExamResultData examResultData = arrayList.get(position);

        holder.tv_subject_name.setText(examResultData.getSubject_name());
        holder.tv_scored.setText(examResultData.getScored_marks() + "/"
                + examResultData.getTotal_marks());

        float scored = Float.parseFloat(examResultData.getScored_marks());
        float total_marks = Float.parseFloat(examResultData.getTotal_marks());

        float percent = (scored/total_marks) * 100;
        holder.tv_percent.setText(precision.format(percent) + "%");


        return convertView;

    }

    private class ViewHolder {
        private TextView tv_subject_name, tv_scored, tv_percent;
        public ViewHolder(View view) {
            tv_subject_name = view.findViewById(R.id.tv_subject_name);
            tv_scored = view.findViewById(R.id.tv_scored);
            tv_percent = view.findViewById(R.id.tv_percent);

        }
    }

}
