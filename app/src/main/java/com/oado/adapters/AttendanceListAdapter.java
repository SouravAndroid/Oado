package com.oado.adapters;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.oado.R;
import com.oado.models.AttendanceReasonData;
import com.oado.models.StudentData;
import com.oado.utils.Commons;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<StudentData> itemList;
    private ArrayList<String> selected_array;
    private ArrayList<AttendanceReasonData> reasonDataArrayList;
    private Button present_submit;

    public AttendanceListAdapter(Context context, ArrayList<StudentData> itemList,
                                 Button present_submit) {
        this.context = context;
        this.itemList = itemList;
        this.present_submit = present_submit;
        selected_array = new ArrayList<>();
        reasonDataArrayList = new ArrayList<>();
        present_submit.setVisibility(View.GONE);


        initArray();
    }

    private void initArray(){

        for (int i = 0; i < itemList.size(); i++){
            selected_array.add("");
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_present, tv_late, tv_absent;
        private CircleImageView item_image;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            item_image = view.findViewById(R.id.item_image);

            tv_present = view.findViewById(R.id.tv_present);
            tv_late = view.findViewById(R.id.tv_late);
            tv_absent = view.findViewById(R.id.tv_absent);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_listitem, parent, false);
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



        if (selected_array.get(position).matches("P")){
            holder.tv_present.setBackgroundResource(R.drawable.circle_green);
            holder.tv_late.setBackgroundResource(R.drawable.circle_grey);
            holder.tv_absent.setBackgroundResource(R.drawable.circle_grey);

            holder.tv_present.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_late.setTextColor(context.getResources().getColor(R.color.dark_grey));
            holder.tv_absent.setTextColor(context.getResources().getColor(R.color.dark_grey));

        }else if (selected_array.get(position).matches("L")){
            holder.tv_present.setBackgroundResource(R.drawable.circle_grey);
            holder.tv_late.setBackgroundResource(R.drawable.circle_orange);
            holder.tv_absent.setBackgroundResource(R.drawable.circle_grey);

            holder.tv_present.setTextColor(context.getResources().getColor(R.color.dark_grey));
            holder.tv_late.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_absent.setTextColor(context.getResources().getColor(R.color.dark_grey));

        }else if (selected_array.get(position).matches("A")){
            holder.tv_present.setBackgroundResource(R.drawable.circle_grey);
            holder.tv_late.setBackgroundResource(R.drawable.circle_grey);
            holder.tv_absent.setBackgroundResource(R.drawable.circle_red);

            holder.tv_present.setTextColor(context.getResources().getColor(R.color.dark_grey));
            holder.tv_late.setTextColor(context.getResources().getColor(R.color.dark_grey));
            holder.tv_absent.setTextColor(context.getResources().getColor(R.color.white));

        }else {
            holder.tv_present.setBackgroundResource(R.drawable.circle_grey);
            holder.tv_late.setBackgroundResource(R.drawable.circle_grey);
            holder.tv_absent.setBackgroundResource(R.drawable.circle_grey);

            holder.tv_present.setTextColor(context.getResources().getColor(R.color.dark_grey));
            holder.tv_late.setTextColor(context.getResources().getColor(R.color.dark_grey));
            holder.tv_absent.setTextColor(context.getResources().getColor(R.color.dark_grey));
        }


        holder.tv_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selected_array.set(position, "P");


                setReasonData(studentData.getId(), "P", Commons.getCurrentDate(),
                        Commons.getCurrentTime(), "", "", position);

                notifyDataSetChanged();
            }
        });

        holder.tv_late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selected_array.set(position, "L");

                notifyDataSetChanged();

                dialogAttendanceReason(position, studentData.getId());


            }
        });

        holder.tv_absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selected_array.set(position, "A");

                setReasonData(studentData.getId(), "A", Commons.getCurrentDate(),
                        Commons.getCurrentTime(), "", "", position);

                notifyDataSetChanged();
            }
        });

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public ArrayList<AttendanceReasonData> getReasonDataArrayList() {
        return reasonDataArrayList;
    }

    private void setReasonData(String student_id, String type, String date,
                              String time, String late_time, String reason, int position){

        for (int i = 0; i < reasonDataArrayList.size(); i++){
            if (Integer.parseInt(student_id) == (reasonDataArrayList.get(i).getStu_id())){

                reasonDataArrayList.remove(i);

            }
        }

        AttendanceReasonData reasonData = new AttendanceReasonData();
        reasonData.setStu_id(Integer.parseInt(student_id));
        reasonData.setAtten_type(type);
        reasonData.setAtten_date(date);
        reasonData.setAtten_time(time);
        reasonData.setLate_time(late_time);
        reasonData.setReason(reason);

        reasonDataArrayList.add(reasonData);




        if (reasonDataArrayList.size() > 0){
            this.present_submit.setVisibility(View.VISIBLE);
        }else {
            this.present_submit.setVisibility(View.GONE);
        }


    }


    private void dialogAttendanceReason(final int position, final String stu_id){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_attendence_reason_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Set Time and Reason:");


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        final TextView tv_set_time = dialogView.findViewById(R.id.tv_set_time);
        final EditText edt_reason = dialogView.findViewById(R.id.edt_reason);
        Button apply_reason = dialogView.findViewById(R.id.apply_reason);


        tv_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogTimePicker(tv_set_time);

            }
        });

        apply_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tv_set_time.getText().toString().trim().length() == 0){
                    Toasty.info(context,
                            "Enter time",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (edt_reason.getText().toString().trim().length() == 0){
                    Toasty.info(context,
                            "Enter reason",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }


                setReasonData(stu_id, "L", Commons.getCurrentDate(),
                        Commons.getCurrentTime(), tv_set_time.getText().toString(),
                        edt_reason.getText().toString(), position);


                alertDialog.dismiss();
            }
        });






    }


    private void dialogTimePicker(final TextView textView){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String in_time = selectedHour + ":" + selectedMinute;

                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

                Date date = null;
                try {
                    date = fmt.parse(in_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
                String formattedTime = fmtOut.format(date);


                textView.setText(formattedTime);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


}
