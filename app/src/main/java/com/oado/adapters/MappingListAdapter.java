package com.oado.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.activity.TimeSlotsAdd;
import com.oado.models.TimeSlotData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class MappingListAdapter extends RecyclerView.Adapter<MappingListAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private ArrayList<TimeSlotData> originalList;
    private ArrayList<TimeSlotData> filteredArrayList;
    private ArrayList<Boolean> booleanArrayList;
    private ProgressDialog progressDialog;

    public MappingListAdapter(Context context, ArrayList<TimeSlotData> itemList) {
        this.context = context;
        this.originalList = itemList;
        this.filteredArrayList = itemList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        booleanArrayList = new ArrayList<>();
        initState();
    }

    private void initState(){
        booleanArrayList = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i++){
            booleanArrayList.add(false);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_head, tv_name, textView2, textView3;
        private RelativeLayout rel_action_view, rel_main, rel_edit, rel_delete;

        public MyViewHolder(View view) {
            super(view);
            tv_head =  view.findViewById(R.id.tv_head);
            tv_name =  view.findViewById(R.id.tv_name);
            textView2 =  view.findViewById(R.id.textView2);
            textView3 =  view.findViewById(R.id.textView3);
            rel_main =  view.findViewById(R.id.rel_main);
            rel_action_view =  view.findViewById(R.id.rel_action_view);
            rel_edit =  view.findViewById(R.id.rel_edit);
            rel_delete =  view.findViewById(R.id.rel_delete);

        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.time_slots_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        TimeSlotData timeSlotData = filteredArrayList.get(position);

        holder.tv_head.setText(Commons.getFirstChar(timeSlotData.getSubject_name()));
        holder.tv_name.setText("Sub: "+timeSlotData.getSubject_name());
        holder.textView2.setText(timeSlotData.getClass_name()+" / "
                +timeSlotData.getSection_name());
        holder.textView3.setText(timeSlotData.getStart_time() + " - "
                + timeSlotData.getEnd_time());



        if (booleanArrayList.get(position)){
            holder.rel_action_view.setVisibility(View.VISIBLE);
        }else {
            holder.rel_action_view.setVisibility(View.GONE);
        }

        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (booleanArrayList.get(position)){
                    booleanArrayList.set(position, false);
                }else {
                    initState();
                    booleanArrayList.set(position, true);
                }

                notifyDataSetChanged();

            }
        });



        holder.rel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimeSlotData timeSlotData1 = filteredArrayList.get(position);

                Intent intent = new Intent(context, TimeSlotsAdd.class);
                intent.putExtra("user_data", timeSlotData1); //pass bundle to your intent
                intent.putExtra("type", "edit");

                context.startActivity(intent);

            }
        });

        holder.rel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimeSlotData timeSlotData1 = filteredArrayList.get(position);

                deleteDialog(timeSlotData1.getId());

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
                    ArrayList<TimeSlotData> filteredList = new ArrayList<>();
                    for (TimeSlotData row : originalList) {

                        // here we are looking for name or phone number match
                        if (row.getClass_name().toLowerCase().contains(charString.toLowerCase())
                                || row.getSubject_name().toLowerCase().contains(charString.toLowerCase())) {
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

                    filteredArrayList = (ArrayList<TimeSlotData>) filterResults.values;

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


    private void deleteDialog(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Oado");
        builder.setMessage("Are you sure you want to delete this Time Slot?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteTimeSlot(id);

            }
        });
        builder.setNegativeButton("Cancel", new     DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog b = builder.create();
        b.show();

    }

    private void deleteTimeSlot(final String id){

        progressDialog.show();

        String url = ApiClient.delete_timeslot;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.timeslot_id, id);

        Log.d(Constants.TAG , "delete_timeslot - " + url);
        Log.d(Constants.TAG , "delete_timeslot - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "delete_timeslot- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(context,
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                            for (int i = 0; i < originalList.size(); i++){
                                if (id.matches(originalList.get(i).getId())){
                                    originalList.remove(i);
                                }
                            }

                            for (int i = 0; i < filteredArrayList.size(); i++){
                                if (id.matches(filteredArrayList.get(i).getId())){
                                    filteredArrayList.remove(i);
                                }
                            }

                            filteredArrayList = originalList;

                            initState();

                            notifyDataSetChanged();


                        }else {

                            Toasty.error(context,
                                    "Some error occurred. Try Again",
                                    Toast.LENGTH_SHORT, true).show();

                        }

                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "delete_timeslot- " + res);
                progressDialog.dismiss();

                AlertDialog alert =
                        new AlertDialog.Builder(context).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }




}
