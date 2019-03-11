package com.oado.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oado.R;
import com.oado.activity.ExamList;
import com.oado.activity.ExamResultView;
import com.oado.models.StudentData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExamResultListAdapter extends
        RecyclerView.Adapter<ExamResultListAdapter.MyViewHolder>
        implements Filterable{

    private Context context;
    private ArrayList<StudentData> originalList;
    private ArrayList<StudentData> filteredArrayList;

    public ExamResultListAdapter(Context context, ArrayList<StudentData> itemList) {
        this.context = context;
        this.originalList = itemList;
        this.filteredArrayList = itemList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, textView2;
        private CircleImageView item_image;
        private RelativeLayout rel_action_view;
        private LinearLayout linear_main;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            textView2 = view.findViewById(R.id.textView2);
            item_image = view.findViewById(R.id.item_image);
            rel_action_view = view.findViewById(R.id.rel_action_view);
            linear_main = view.findViewById(R.id.linear_main);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        StudentData studentData = filteredArrayList.get(position);

        holder.tv_name.setText(studentData.getName());
        holder.textView2.setText("Roll No: "+studentData.getRoll_no());


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



        holder.rel_action_view.setVisibility(View.GONE);
        holder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StudentData studentData1 = filteredArrayList.get(position);

                Intent intent = new Intent(context, ExamList.class);
                intent.putExtra("data", studentData1.getId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredArrayList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredArrayList = originalList;
                } else {
                    ArrayList<StudentData> filteredList = new ArrayList<>();
                    for (StudentData row : originalList) {

                        // here we are looking for name or phone number match
                        if (row.getName().contains(charString.toLowerCase())
                                || row.getName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    filteredArrayList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (charSequence != null) {

                    filteredArrayList = (ArrayList<StudentData>) filterResults.values;

                    if (filteredArrayList != null){
                        notifyDataSetChanged();
                    }else {
                        filteredArrayList = new ArrayList<>();
                    }

                }else {
                    filteredArrayList = originalList;

                    notifyDataSetChanged();
                }
            }
        };
    }


}
