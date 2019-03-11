package com.oado.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oado.R;
import com.oado.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class FeesAddListAdapter extends RecyclerView.Adapter<FeesAddListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> itemList;
    private ArrayList<HashMap<String, String>> entryList;


    public FeesAddListAdapter(Context context, ArrayList<HashMap<String, String>> itemList) {
        this.context = context;
        this.itemList = itemList;


        entryList = new ArrayList<>();

        initValues();
    }

    public void initValues(){

        for (int i = 0; i < itemList.size(); i++){
            HashMap<String, String> hashMap = new HashMap<>();

            hashMap.put("class_subject_id", itemList.get(i).get("id"));
            hashMap.put("amount", "");

            entryList.add(hashMap);

        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private EditText edt_amount;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            edt_amount = view.findViewById(R.id.edt_amount);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fees_add_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        HashMap<String, String> hashMap = itemList.get(position);

        holder.tv_name.setText(hashMap.get("name"));

        holder.edt_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().trim().length() > 0){

                    try {

                        if (Float.parseFloat(s.toString()) > 0){

                            HashMap<String, String> hashMap1 = entryList.get(position);
                            HashMap<String, String> hashMap2 = new HashMap<>();

                            hashMap2.put("class_subject_id", hashMap1.get("class_subject_id"));
                            hashMap2.put("amount", s.toString());

                            entryList.set(position, hashMap2);

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public ArrayList<HashMap<String, String>> getEntryList() {
        return entryList;
    }
}
