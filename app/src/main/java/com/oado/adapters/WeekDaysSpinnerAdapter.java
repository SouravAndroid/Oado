package com.oado.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oado.R;

import java.util.ArrayList;
import java.util.HashMap;

public class WeekDaysSpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> arrayList;
    private ArrayList<Boolean> booleanArrayList;
    private ArrayList<String> preSelectedDaysList;
    private ArrayList<String> selectedDaysList;
    private LayoutInflater inflater;
    private TextView tv_week_days;
    String select_days = "";

    public WeekDaysSpinnerAdapter(Context context, ArrayList<HashMap<String, String>> arrayList,
                                  TextView tv_week_days) {
        this.context = context;
        this.arrayList = arrayList;
        this.tv_week_days = tv_week_days;
        inflater = LayoutInflater.from(context);

        booleanArrayList = new ArrayList<>();
        selectedDaysList = new ArrayList<>();
        preSelectedDaysList = new ArrayList<>();

        initState();

    }

    public WeekDaysSpinnerAdapter(Context context, ArrayList<HashMap<String, String>> arrayList,
                                  ArrayList<String> preSelectedDaysList, TextView tv_week_days) {
        this.context = context;
        this.arrayList = arrayList;
        this.tv_week_days = tv_week_days;
        this.preSelectedDaysList = preSelectedDaysList;
        inflater = LayoutInflater.from(context);

        booleanArrayList = new ArrayList<>();
        selectedDaysList = new ArrayList<>();

        initState();

    }

    private void initState(){

        if (preSelectedDaysList.size() == 0){

            for (int i = 0; i < arrayList.size(); i++){
                booleanArrayList.add(false);
            }

        }else {
            String days = "";

            for (int i = 0; i < arrayList.size(); i++){
                if (i == 0){

                    booleanArrayList.add(false);

                }else {

                    boolean isExits = false;

                    for (int j = 0; j < preSelectedDaysList.size(); j++){
                        if (arrayList.get(i).get("name").toLowerCase()
                                .matches(preSelectedDaysList.get(j).toLowerCase())){

                            isExits = true;
                            selectedDaysList.add(arrayList.get(i).get("name"));
                            days = days + arrayList.get(i).get("name") + ",";

                            break;

                        }
                    }


                    if (isExits){
                        booleanArrayList.add(true);
                    }else {
                        booleanArrayList.add(false);
                    }

                }

            }


            tv_week_days.setText(days);

        }


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
        convertView = inflater.inflate(R.layout.week_days_spinner_item, null);
        ViewHolder holder = new ViewHolder(convertView);

        if (position == 0){
            holder.tv_name.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
            holder.tv_name.setText(arrayList.get(position).get("name"));
        }else {
            holder.tv_name.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);


            holder.checkbox.setText(arrayList.get(position).get("name"));

            holder.checkbox.setChecked(booleanArrayList.get(position));


            holder.checkbox.setTag(position);
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int getPosition = (Integer) buttonView.getTag();
                    String text = buttonView.getText().toString();

                    if (isChecked){

                        selectedDaysList.add(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }else {

                        selectedDaysList.remove(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }


                   // Log.d(StaticText.TAG, "Array = "+selectedDaysList);
                    tv_week_days.setText("");
                    select_days = "";

                    for (int i = 0; i < selectedDaysList.size(); i++){

                        select_days = select_days + selectedDaysList.get(i) +", ";

                    }

                    tv_week_days.setText(select_days);

                }
            });


        }



        return convertView;

    }

    private class ViewHolder {
        TextView tv_name;
        CheckBox checkbox;

        public ViewHolder(View view) {
            checkbox = view.findViewById(R.id.checkbox);
            tv_name = view.findViewById(R.id.tv_name);

        }

    }


    public ArrayList<String> getSelectedDaysList() {
        return selectedDaysList;
    }
}
