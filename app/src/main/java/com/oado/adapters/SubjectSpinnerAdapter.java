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
import com.oado.utils.Commons;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectSpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> arrayList;
    private ArrayList<Boolean> booleanArrayList;
    private ArrayList<String> selectedSubjectList;
    private ArrayList<String> selectedSubjectId;
    private ArrayList<String> preSelectedSubjects;
    private LayoutInflater inflater;
    private TextView tv_subjects;
    String select_subject = "";

    public SubjectSpinnerAdapter(Context context, ArrayList<HashMap<String, String>> arrayList,
                                 TextView tv_subjects) {
        this.context = context;
        this.arrayList = arrayList;
        this.tv_subjects = tv_subjects;
        inflater = LayoutInflater.from(context);

        booleanArrayList = new ArrayList<>();
        selectedSubjectList = new ArrayList<>();
        selectedSubjectId = new ArrayList<>();
        preSelectedSubjects = new ArrayList<>();

        tv_subjects.setText("0 Subject selected");

        initState();

    }

    public SubjectSpinnerAdapter(Context context, ArrayList<HashMap<String, String>> arrayList,
                                 ArrayList<String> preSelectedSubjects,
                                 TextView tv_subjects) {
        this.context = context;
        this.arrayList = arrayList;
        this.tv_subjects = tv_subjects;
        this.preSelectedSubjects = preSelectedSubjects;
        inflater = LayoutInflater.from(context);

        booleanArrayList = new ArrayList<>();
        selectedSubjectList = new ArrayList<>();
        selectedSubjectId = new ArrayList<>();

        tv_subjects.setText(preSelectedSubjects.size()+" Subject selected");

        initState();

    }


    private void initState(){

        if (preSelectedSubjects.size() == 0){
            for (int i = 0; i < arrayList.size(); i++){
                booleanArrayList.add(false);
            }
        }else {
            for (int i = 0; i < arrayList.size(); i++){
                if (i == 0){

                    booleanArrayList.add(false);

                }else {

                    boolean isExits = false;
                    for (int j = 0; j < preSelectedSubjects.size(); j++){
                        if (arrayList.get(i).get("name").toLowerCase()
                                .matches(preSelectedSubjects.get(j).toLowerCase())){

                            isExits = true;

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
                    String name = buttonView.getText().toString();
                    String id = arrayList.get(getPosition).get("id");

                    if (isChecked){

                        selectedSubjectList.add(name);
                        selectedSubjectId.add(id);

                        booleanArrayList.set(getPosition, isChecked);

                    }else {

                        selectedSubjectList.remove(name);
                        selectedSubjectId.remove(id);

                        booleanArrayList.set(getPosition, isChecked);

                    }


                   // Log.d(StaticText.TAG, "Array = "+selectedSubjectList);
                    tv_subjects.setText("");
                    select_subject = "";

                    /*for (int i = 0; i < selectedSubjectList.size(); i++){

                        select_guardian = select_guardian + selectedSubjectList.get(i) +", ";

                    }*/

                    tv_subjects.setText(selectedSubjectList.size()+" Subject selected");

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


    public int getIdsLength(){
        return selectedSubjectId.size();
    }

    public String getSubjectIds(){
        String ids = "";

        for (int i = 0; i < selectedSubjectId.size(); i++){

            ids = ids + selectedSubjectId.get(i) + ",";

        }

        if (ids.endsWith(",")){
            ids = Commons.removeLastChar(ids);
        }

        return ids;

    }




}
