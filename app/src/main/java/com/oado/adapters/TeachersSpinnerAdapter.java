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
import com.oado.models.TeacherData;

import java.util.ArrayList;

public class TeachersSpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TeacherData> arrayList;
    private ArrayList<Boolean> booleanArrayList;
    private ArrayList<String> selectedTeacherIdsList;
    private LayoutInflater inflater;
    private TextView tv_teachers;
    String teachers = "";

    public TeachersSpinnerAdapter(Context context, ArrayList<TeacherData> arrayList,
                                  TextView tv_teachers) {
        this.context = context;
        this.arrayList = arrayList;
        this.tv_teachers = tv_teachers;
        inflater = LayoutInflater.from(context);

        booleanArrayList = new ArrayList<>();
        selectedTeacherIdsList = new ArrayList<>();

        tv_teachers.setText("0 Teacher selected");

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

        final TeacherData teacherData = arrayList.get(position);

        if (position == 0){
            holder.tv_name.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
            holder.tv_name.setText(teacherData.getName());
        }else {
            holder.tv_name.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);


            holder.checkbox.setText(teacherData.getName()
                    + " (" + teacherData.getSubject_name() + ")");

            holder.checkbox.setChecked(booleanArrayList.get(position));


            holder.checkbox.setTag(position);
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int getPosition = (Integer) buttonView.getTag();
                   // String text = buttonView.getText().toString();
                    String text = teacherData.getId();

                    if (isChecked){

                        selectedTeacherIdsList.add(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }else {

                        selectedTeacherIdsList.remove(text);

                        booleanArrayList.set(getPosition, isChecked);

                    }


                   // Log.d(StaticText.TAG, "Array = "+selectedTeacherIdsList);
                    tv_teachers.setText("0 teacher selected");
                    teachers = "";

                   /* for (int i = 0; i < selectedTeacherIdsList.size(); i++){

                        teachers = teachers + selectedTeacherIdsList.get(i) +", ";

                    }*/

                    tv_teachers.setText(selectedTeacherIdsList.size() + " Teacher selected");

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


    public ArrayList<String> getSelectedTeacherIdsList() {
        return selectedTeacherIdsList;
    }


}
