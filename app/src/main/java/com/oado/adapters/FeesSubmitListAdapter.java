package com.oado.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.oado.R;
import com.oado.utils.ApiClient;
import com.oado.utils.PrefManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class FeesSubmitListAdapter extends RecyclerView.Adapter<FeesSubmitListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> itemList;
    private ArrayList<HashMap<String, String>> selectedList;
    private ArrayList<Boolean> booleanArrayList;

    private PrefManager prefManager;
    private TextView tv_total_pay_amt;


    public FeesSubmitListAdapter(Context context,
                                 ArrayList<HashMap<String, String>> itemList,
                                 TextView tv_total_pay_amt) {
        this.context = context;
        this.itemList = itemList;
        this.tv_total_pay_amt = tv_total_pay_amt;

        booleanArrayList = new ArrayList<>();
        selectedList = new ArrayList<>();

        prefManager = new PrefManager(context);

        initValues();
    }

    private void initValues(){

        for (int i = 0; i < itemList.size(); i++){
            booleanArrayList.add(true);
        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_amount;
        private CheckBox checkbox;
        private CardView card_view;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_amount = view.findViewById(R.id.tv_amount);
            checkbox = view.findViewById(R.id.checkbox);
            card_view = view.findViewById(R.id.card_view);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fees_submit_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        HashMap<String, String> hashMap = itemList.get(position);

        if (prefManager.getInstituteType().equals(ApiClient.coaching_center)){
            holder.tv_name.setText(hashMap.get("subject_name"));
        }else {
            holder.tv_name.setText(hashMap.get("class_name"));
        }

        holder.tv_amount.setText(hashMap.get("fees_amount"));

        if (booleanArrayList.get(position)){
            holder.checkbox.setChecked(true);
        }else {
            holder.checkbox.setChecked(false);
        }


        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                booleanArrayList.set(position, isChecked);

                notifyDataSetChanged();

                setTotalAmt();

            }
        });




    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    private void setTotalAmt(){

        float total_amt = 0;

        for (int i = 0; i < booleanArrayList.size(); i++){
            if (booleanArrayList.get(i)){
                if (!itemList.get(i).get("fees_amount").isEmpty()){
                    float amt = Float.parseFloat(itemList.get(i).get("fees_amount"));

                    total_amt = total_amt + amt;
                }
            }
        }


        DecimalFormat precision = new DecimalFormat("0.00");
        this.tv_total_pay_amt.setText(precision.format(total_amt));

    }


    public ArrayList<HashMap<String, String>> getSelectedList() {
        selectedList = new ArrayList<>();

        for (int i = 0; i < booleanArrayList.size(); i++){
            if (booleanArrayList.get(i)){

                selectedList.add(itemList.get(i));
            }

        }

        return selectedList;
    }
}
