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
import com.oado.models.StudentData;

import java.util.ArrayList;

public class StudentsSpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<StudentData> arrayList;
    private ArrayList<Boolean> booleanArrayList;
    private ArrayList<String> selectedStudentsIdsList;
    private LayoutInflater inflater;
    private TextView tv_student;
    String select_students = "";

    public StudentsSpinnerAdapter(Context context, ArrayList<StudentData> arrayList,
                                  TextView tv_student) {
        this.context = context;
        this.arrayList = arrayList;
        this.tv_student = tv_student;
        inflater = LayoutInflater.from(context);

        booleanArrayList = new ArrayList<>();
        selectedStudentsIdsList = new ArrayList<>();

        tv_student.setText("0 Student selected");

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

        final StudentData studentData = arrayList.get(position);

        if (position == 0){
            holder.tv_name.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
            holder.tv_name.setText(studentData.getName());
        }else {
            holder.tv_name.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);


            holder.checkbox.setText(studentData.getName());

            holder.checkbox.setChecked(booleanArrayList.get(position));


            holder.checkbox.setTag(position);
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int getPosition = (Integer) buttonView.getTag();
                    //String text = buttonView.getText().toString();
                    String text = studentData.getId();

                    if (isChecked){

                        selectedStudentsIdsList.add(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }else {

                        selectedStudentsIdsList.remove(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }


                   // Log.d(StaticText.TAG, "Array = "+selectedStudentsIdsList);
                    tv_student.setText("");
                    select_students = "";

                    /*for (int i = 0; i < selectedStudentsIdsList.size(); i++){

                        select_students = select_students + selectedStudentsIdsList.get(i) +", ";

                    }*/

                    tv_student.setText(selectedStudentsIdsList.size()+" Student selected");

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


    public ArrayList<String> getSelectedStudentsIdsList() {
        return selectedStudentsIdsList;
    }
}
