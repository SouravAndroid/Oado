package com.oado.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.oado.R;
import com.oado.models.ExamScoreData;
import com.oado.models.SubjectData;

import java.util.ArrayList;

public class ExamResultEntryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SubjectData> arrayList;
    private ArrayList<ExamScoreData> scoreDataArrayList;
    private LayoutInflater inflater;

    public ExamResultEntryAdapter(Context context, ArrayList<SubjectData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.inflater = LayoutInflater.from(context);
        scoreDataArrayList = new ArrayList<>();


        setInitData();
    }

    private void setInitData(){
        for (int i = 0; i < arrayList.size(); i++){

            ExamScoreData examScoreData = new ExamScoreData();

            examScoreData.setId(arrayList.get(i).getId());
            examScoreData.setSubject_name(arrayList.get(i).getSubject_name());
            examScoreData.setMarks("");
            examScoreData.setFull_marks("");

            scoreDataArrayList.add(examScoreData);

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
        convertView = inflater.inflate(R.layout.exam_result_entry_item, null);
        ViewHolder holder = new ViewHolder(convertView);

        holder.tv_subject_name.setText(arrayList.get(position).getSubject_name());


        holder.edt_marks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() > 0){

                    try {

                        float score = Float.parseFloat(s.toString());

                        if (score > 0){

                            ExamScoreData examScoreData = scoreDataArrayList.get(position);
                            ExamScoreData examScoreData1 = new ExamScoreData();

                            examScoreData1.setId(examScoreData.getId());
                            examScoreData1.setSubject_name(examScoreData.getSubject_name());
                            examScoreData1.setMarks(s.toString());
                            examScoreData1.setFull_marks(examScoreData.getFull_marks());

                            scoreDataArrayList.set(position, examScoreData1);

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }


            }
        });


        holder.edt_full_marks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() > 0){

                    try {

                        float score = Float.parseFloat(s.toString());

                        if (score > 0){

                            ExamScoreData examScoreData = scoreDataArrayList.get(position);
                            ExamScoreData examScoreData1 = new ExamScoreData();

                            examScoreData1.setId(examScoreData.getId());
                            examScoreData1.setSubject_name(examScoreData.getSubject_name());
                            examScoreData1.setMarks(examScoreData.getMarks());
                            examScoreData1.setFull_marks(s.toString());

                            scoreDataArrayList.set(position, examScoreData1);

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }


            }
        });


        return convertView;

    }

    private class ViewHolder {
        private TextView tv_subject_name;
        private EditText edt_marks, edt_full_marks;
        public ViewHolder(View view) {
            tv_subject_name = view.findViewById(R.id.tv_subject_name);
            edt_marks = view.findViewById(R.id.edt_marks);
            edt_full_marks = view.findViewById(R.id.edt_full_marks);
        }
    }


    public ArrayList<ExamScoreData> getScoreDataArrayList() {
        return scoreDataArrayList;
    }



}
