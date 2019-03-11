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
import com.oado.models.StaffData;

import java.util.ArrayList;

public class OthersStaffSpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<StaffData> arrayList;
    private ArrayList<Boolean> booleanArrayList;
    private ArrayList<String> selectedStaffIdsList;
    private LayoutInflater inflater;
    private TextView tv_staff;
    String select_staff = "";

    public OthersStaffSpinnerAdapter(Context context, ArrayList<StaffData> arrayList,
                                     TextView tv_staff) {
        this.context = context;
        this.arrayList = arrayList;
        this.tv_staff = tv_staff;
        inflater = LayoutInflater.from(context);

        booleanArrayList = new ArrayList<>();
        selectedStaffIdsList = new ArrayList<>();

        tv_staff.setText("0 Staff selected");

        initState();

    }

    private void initState(){

        for (int i = 0; i < arrayList.size(); i++){
            booleanArrayList.add(false);
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

        final StaffData staffData = arrayList.get(position);

        if (position == 0){
            holder.tv_name.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
            holder.tv_name.setText(staffData.getName());
        }else {
            holder.tv_name.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);


            holder.checkbox.setText(staffData.getName());

            holder.checkbox.setChecked(booleanArrayList.get(position));


            holder.checkbox.setTag(position);
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int getPosition = (Integer) buttonView.getTag();
                    //String text = buttonView.getText().toString();
                    String text = staffData.getId();

                    if (isChecked){

                        selectedStaffIdsList.add(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }else {

                        selectedStaffIdsList.remove(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }


                   // Log.d(StaticText.TAG, "Array = "+selectedStaffIdsList);
                    tv_staff.setText("");
                    select_staff = "";

                   /* for (int i = 0; i < selectedStaffIdsList.size(); i++){

                        select_staff = select_staff + selectedStaffIdsList.get(i) +", ";

                    }*/

                    tv_staff.setText(selectedStaffIdsList.size()+" Staff selected");

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


    public ArrayList<String> getSelectedStaffIdsList() {
        return selectedStaffIdsList;
    }
}
