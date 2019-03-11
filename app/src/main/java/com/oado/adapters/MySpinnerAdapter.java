package com.oado.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oado.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MySpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> arrayList;
    private LayoutInflater inflater;

    public MySpinnerAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.tv_name.setText(arrayList.get(position).get("name"));


        return convertView;

    }

    private class ViewHolder {
        TextView tv_name;
        public ViewHolder(View view) {
            tv_name = view.findViewById(R.id.tv_name);
        }
    }

}
