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
import com.oado.activity.SubjectAdd;
import com.oado.models.SubjectData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.UserPermissionCheck;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private ArrayList<SubjectData> originalList;
    private ArrayList<SubjectData> filteredArrayList;
    private ArrayList<Boolean> booleanArrayList;
    private ProgressDialog progressDialog;
    private UserPermissionCheck userPermissionCheck;


    public SubjectListAdapter(Context context, ArrayList<SubjectData> itemList) {
        this.context = context;
        this.originalList = itemList;
        this.filteredArrayList = itemList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        userPermissionCheck = new UserPermissionCheck(context);


        initState();

    }

    private void initState(){
        booleanArrayList = new ArrayList<>();
        for (int i = 0; i < filteredArrayList.size(); i++){
            booleanArrayList.add(false);
        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_head, textView2;
        private RelativeLayout rel_action_view, rel_main, rel_edit, rel_delete;

        public MyViewHolder(View view) {
            super(view);
            tv_head = view.findViewById(R.id.tv_head);
            tv_name = view.findViewById(R.id.tv_name);
            textView2 = view.findViewById(R.id.textView2);
            rel_action_view = view.findViewById(R.id.rel_action_view);
            rel_main = view.findViewById(R.id.rel_main);
            rel_edit = view.findViewById(R.id.rel_edit);
            rel_delete = view.findViewById(R.id.rel_delete);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        SubjectData subjectData = filteredArrayList.get(position);

        holder.tv_name.setText(subjectData.getSubject_name());
        holder.tv_head.setText(Commons.getFirstChar(subjectData.getSubject_name()).toUpperCase());
        holder.textView2.setText("Code: "+subjectData.getSubject_code());


        if (booleanArrayList.get(position)){
            holder.rel_action_view.setVisibility(View.VISIBLE);
        }else {
            holder.rel_action_view.setVisibility(View.GONE);
        }

        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //// permission
                if (userPermissionCheck.isAdmin() || userPermissionCheck.isInstitute()){

                    if (booleanArrayList.get(position)){
                        booleanArrayList.set(position, false);
                    }else {
                        initState();
                        booleanArrayList.set(position, true);
                    }

                    notifyDataSetChanged();
                }

            }
        });


        holder.rel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubjectData subjectData1 = filteredArrayList.get(position);

                Intent intent = new Intent(context, SubjectAdd.class);
                intent.putExtra("user_data", subjectData1); //pass bundle to your intent
                intent.putExtra("type", "edit");

                context.startActivity(intent);

            }
        });

        holder.rel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubjectData subjectData1 = filteredArrayList.get(position);

                deleteDialog(subjectData1.getId(), subjectData1.getInstitute_id());
            }
        });


       /* //// permission
        if (userPermissionCheck.isAdmin() || userPermissionCheck.isInstitute()){
            holder.rel_action_view.setVisibility(View.VISIBLE);
        }else {
            holder.rel_action_view.setVisibility(View.GONE);
        }*/


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
                    ArrayList<SubjectData> filteredList = new ArrayList<>();
                    for (SubjectData row : originalList) {

                        // here we are looking for name or phone number match
                        if (row.getSubject_name().toLowerCase().contains(charString.toLowerCase())
                                || row.getSubject_name().contains(charSequence)) {
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

                    filteredArrayList = (ArrayList<SubjectData>) filterResults.values;

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



    private void deleteDialog(final String id, final String institute_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Oado");
        builder.setMessage("Are you sure you want to delete this Subject?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteSubject(id, institute_id);

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

    private void deleteSubject(final String id, String institute_id){

        progressDialog.show();

        String url = ApiClient.delete_subject;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.subject_id, id);
        params.put(ApiClient.institute_id, institute_id);

        Log.d(Constants.TAG , "delete_subject - " + url);
        Log.d(Constants.TAG , "delete_subject - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "delete_subject- " + response.toString());

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
                Log.d(Commons.TAG, "delete_subject- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(context).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }

}
