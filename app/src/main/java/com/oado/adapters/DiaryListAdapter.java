package com.oado.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.models.DiaryData;
import com.oado.models.DiaryMembersData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.UserPermissionCheck;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DiaryData> itemList;
    private ArrayList<Boolean> booleanArrayList;
    private ProgressDialog progressDialog;
    private UserPermissionCheck userPermissionCheck;


    public DiaryListAdapter(Context context, ArrayList<DiaryData> itemList) {
        this.context = context;
        this.itemList = itemList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        userPermissionCheck = new UserPermissionCheck(context);

        booleanArrayList = new ArrayList<>();
        initState();
    }

    private void initState(){
        booleanArrayList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++){
            booleanArrayList.add(false);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_head, tv_name, tv_members;
        private RelativeLayout rel_main, rel_action_view, rel_view, rel_delete;

        public MyViewHolder(View view) {
            super(view);
            tv_head = view.findViewById(R.id.tv_head);
            tv_name = view.findViewById(R.id.tv_name);
            tv_members = view.findViewById(R.id.tv_members);
            rel_main = view.findViewById(R.id.rel_main);
            rel_action_view = view.findViewById(R.id.rel_action_view);
            rel_view = view.findViewById(R.id.rel_view);
            rel_delete = view.findViewById(R.id.rel_delete);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dairy_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final DiaryData diaryData = itemList.get(position);

        holder.tv_name.setText(diaryData.getName());

        holder.tv_head.setText(Commons.getFirstChar(diaryData.getName()));

        holder.tv_members.setText("(" + diaryData.getList_Members().size() + " members)");


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


        holder.rel_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<DiaryMembersData> membersDataArrayList =
                        diaryData.getList_Members();

                dialogMembers(membersDataArrayList);
            }
        });


        holder.rel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteDialog(diaryData.getId());

            }
        });


        if (userPermissionCheck.isAdmin() || userPermissionCheck.isInstitute()){
            holder.rel_delete.setVisibility(View.VISIBLE);
            holder.rel_view.setVisibility(View.VISIBLE);
        }else if (userPermissionCheck.isTeacher()){
            holder.rel_delete.setVisibility(View.GONE);
            holder.rel_view.setVisibility(View.VISIBLE);
        }else {
            holder.rel_action_view.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    private void dialogMembers(ArrayList<DiaryMembersData> membersDataArrayList){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_diary_members, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();alertDialog.setCanceledOnTouchOutside(false);


        RecyclerView recycler_view = dialogView.findViewById(R.id.recycler_view);
        Button btn_close = dialogView.findViewById(R.id.btn_close);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        DiaryMembersAdapter diaryMembersAdapter = new DiaryMembersAdapter(context,
                membersDataArrayList);
        recycler_view.setAdapter(diaryMembersAdapter);

        alertDialog.show();

    }


    private void deleteDialog(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Oado");
        builder.setMessage("Are you sure you want to delete this Diary?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteDiary(id);

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


    private void deleteDiary(final String id){

        progressDialog.show();

        String url = ApiClient.delete_diary;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.diary_id, id);

        Log.d(Constants.TAG , "delete_diary - " + url);
        Log.d(Constants.TAG , "delete_diary - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "delete_diary- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(context,
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                            for (int i = 0; i < itemList.size(); i++){
                                if (id.matches(itemList.get(i).getId())){
                                    itemList.remove(i);
                                }
                            }

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
                Log.d(Commons.TAG, "delete_diary- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(context).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }

}
