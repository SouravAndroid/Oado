package com.oado.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oado.R;
import com.oado.database.DatabaseHelper;
import com.oado.database.MsgData;

import java.util.ArrayList;

public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationsListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MsgData> msgDataArrayList;
    private LayoutInflater inflater;
    private DatabaseHelper databaseHelper;

    public NotificationsListAdapter(Context context, ArrayList<MsgData> msgDataArrayList) {
        this.context = context;
        this.msgDataArrayList = msgDataArrayList;
        inflater = LayoutInflater.from(context);

        databaseHelper = new DatabaseHelper(context);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_message, tv_date_time;
        private ImageView iv_delete;

        public MyViewHolder(View view) {
            super(view);
            tv_name =  view.findViewById(R.id.tv_name);
            tv_message =  view.findViewById(R.id.tv_message);
            tv_date_time =  view.findViewById(R.id.tv_date_time);
            iv_delete =  view.findViewById(R.id.iv_delete);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final MsgData msgData = msgDataArrayList.get(position);

        holder.tv_name.setText("Oado");
        holder.tv_message.setText(msgData.getMsg());
        holder.tv_date_time.setText(msgData.getDate_time());


        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseHelper.deleteItem(msgData.getId());

                msgDataArrayList.remove(position);

                notifyDataSetChanged();

            }
        });


    }

    @Override
    public int getItemCount() {
        return msgDataArrayList.size();
    }



}
