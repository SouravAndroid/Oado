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
import com.oado.models.GuardianData;

import java.util.ArrayList;

public class GuardiansSpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GuardianData> arrayList;
    private ArrayList<Boolean> booleanArrayList;
    private ArrayList<String> selectedGuardianIdsList;
    private LayoutInflater inflater;
    private TextView tv_guardian;
    String select_guardian = "";

    public GuardiansSpinnerAdapter(Context context, ArrayList<GuardianData> arrayList,
                                   TextView tv_guardian) {
        this.context = context;
        this.arrayList = arrayList;
        this.tv_guardian = tv_guardian;
        inflater = LayoutInflater.from(context);

        booleanArrayList = new ArrayList<>();
        selectedGuardianIdsList = new ArrayList<>();

        tv_guardian.setText("0 Guardian selected");

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

        final GuardianData guardianData = arrayList.get(position);

        if (position == 0){
            holder.tv_name.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
            holder.tv_name.setText(guardianData.getName());
        }else {
            holder.tv_name.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);


            holder.checkbox.setText(guardianData.getName());

            holder.checkbox.setChecked(booleanArrayList.get(position));


            holder.checkbox.setTag(position);
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int getPosition = (Integer) buttonView.getTag();
                    //String text = buttonView.getText().toString();
                    String text = guardianData.getId();

                    if (isChecked){

                        selectedGuardianIdsList.add(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }else {

                        selectedGuardianIdsList.remove(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }


                   // Log.d(StaticText.TAG, "Array = "+selectedGuardianIdsList);
                    tv_guardian.setText("");
                    select_guardian = "";

                    /*for (int i = 0; i < selectedGuardianIdsList.size(); i++){

                        select_guardian = select_guardian + selectedGuardianIdsList.get(i) +", ";

                    }*/

                    tv_guardian.setText(selectedGuardianIdsList.size()+" Guardian selected");

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


    public ArrayList<String> getSelectedGuardianIdsList() {
        return selectedGuardianIdsList;
    }
}
