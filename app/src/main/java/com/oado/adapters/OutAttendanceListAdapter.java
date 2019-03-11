package com.oado.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oado.R;
import com.oado.models.StudentData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class OutAttendanceListAdapter extends RecyclerView.Adapter<OutAttendanceListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<StudentData> itemList;
    private Button present_submit;
    private ArrayList<String> listIds;
    private ArrayList<Boolean> selected_array;

    public OutAttendanceListAdapter(Context context, ArrayList<StudentData> itemList,
                                    Button present_submit) {
        this.context = context;
        this.itemList = itemList;
        this.present_submit = present_submit;
        this.listIds = new ArrayList<>();
        this.selected_array = new ArrayList<>();
        this.present_submit.setVisibility(View.GONE);


        initArray();
    }

    private void initArray(){

        for (int i = 0; i < itemList.size(); i++){
            selected_array.add(false);
            listIds.add("");
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_out;
        private CircleImageView item_image;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            item_image = view.findViewById(R.id.item_image);

            tv_out = view.findViewById(R.id.tv_out);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.out_attendance_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final StudentData studentData = itemList.get(position);

        holder.tv_name.setText(studentData.getName()
                + "\n" + "Roll no: "+studentData.getRoll_no());


        Picasso.with(context).load(studentData.getImage()).placeholder(R.mipmap.no_image)
                .into(holder.item_image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                                R.mipmap.no_image);

                        holder.item_image.setImageBitmap(icon);
                    }
                });


        if (selected_array.get(position)){
            holder.tv_out.setBackgroundResource(R.drawable.circle_green);
        }else {
            holder.tv_out.setBackgroundResource(R.drawable.circle_grey);
        }



        holder.tv_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selected_array.get(position)){

                    selected_array.set(position, false);

                    listIds.set(position, "");

                }else {

                    selected_array.set(position, true);

                    listIds.set(position, studentData.getId());

                }

                notifyDataSetChanged();
            }
        });

        if (getLengthOfIds() > 0){
            present_submit.setVisibility(View.VISIBLE);
        }else {
            present_submit.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public ArrayList<String> getListIds() {
        ArrayList<String> values=new ArrayList<String>();
        HashSet<String> hashSet = new HashSet<String>();
        for (String s : listIds){
            if (!s.isEmpty()){
                values.add(s);
            }
        }
        hashSet.addAll(values);
        listIds.clear();
        listIds.addAll(hashSet);

        return listIds;
    }


    private int getLengthOfIds(){
        int aa = 0;

        for (String s : listIds){
            if (!s.isEmpty()){
                aa++;
            }
        }

        return aa;
    }


}
