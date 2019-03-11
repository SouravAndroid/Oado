package com.oado.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oado.R;
import com.oado.models.InstituteData;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectInstituteAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<InstituteData> arrayList;
    private LayoutInflater inflater;

    public SelectInstituteAdapter(Context context, ArrayList<InstituteData> arrayList) {
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
        convertView = inflater.inflate(R.layout.spinner_item, null);
        ViewHolder holder = new ViewHolder(convertView);

        InstituteData instituteData = arrayList.get(position);

        holder.tv_name.setText(instituteData.getName());


        return convertView;

    }

    private class ViewHolder {
        TextView tv_name;
        public ViewHolder(View view) {
            tv_name = view.findViewById(R.id.tv_name);
        }
    }

}
