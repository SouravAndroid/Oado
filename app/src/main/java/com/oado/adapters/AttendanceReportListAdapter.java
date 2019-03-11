package com.oado.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oado.R;
import com.oado.models.AttendanceReasonData;
import com.oado.models.AttendanceReportData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendanceReportListAdapter extends
        RecyclerView.Adapter<AttendanceReportListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<AttendanceReportData> itemList;

    public AttendanceReportListAdapter(Context context, ArrayList<AttendanceReportData> itemList) {
        this.context = context;
        this.itemList = itemList;


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, textView2, tv_present, tv_late, tv_absent;
        private CircleImageView item_image;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            textView2 = view.findViewById(R.id.textView2);
            item_image = view.findViewById(R.id.item_image);

            tv_present = view.findViewById(R.id.tv_present);
            tv_late = view.findViewById(R.id.tv_late);
            tv_absent = view.findViewById(R.id.tv_absent);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_report_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        AttendanceReportData attendanceReasonData = itemList.get(position);

        holder.tv_name.setText("Name: "+attendanceReasonData.getName());
        holder.textView2.setText("Roll no: "+attendanceReasonData.getRoll_no());


        Picasso.with(context).load(attendanceReasonData.getImage()).placeholder(R.mipmap.no_image)
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


        holder.tv_present.setText("Present: "+attendanceReasonData.getP_count());
        holder.tv_late.setText("Late: "+attendanceReasonData.getL_count());
        holder.tv_absent.setText("Absent: "+attendanceReasonData.getA_count());



    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
