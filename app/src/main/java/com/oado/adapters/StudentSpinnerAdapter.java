package com.oado.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oado.R;
import com.oado.models.StudentData;

import java.util.ArrayList;

public class StudentSpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<StudentData> arrayList;
    private LayoutInflater inflater;

    public StudentSpinnerAdapter(Context context,
                                 ArrayList<StudentData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.inflater = LayoutInflater.from(context);

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
        convertView = inflater.inflate(R.layout.spinner_item, null);
        ViewHolder holder = new ViewHolder(convertView);

        if (arrayList.get(position).getRoll_no().isEmpty()){

            holder.tv_name.setText(arrayList.get(position).getName());

        }else {

            holder.tv_name.setText(arrayList.get(position).getName()
                    + ", Roll No: " + arrayList.get(position).getRoll_no());

        }



        return convertView;

    }

    private class ViewHolder {
        TextView tv_name;
        public ViewHolder(View view) {
            tv_name = view.findViewById(R.id.tv_name);
        }
    }

}
