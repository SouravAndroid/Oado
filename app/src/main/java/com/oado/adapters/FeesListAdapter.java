package com.oado.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.models.FeesData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.UserPermissionCheck;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class FeesListAdapter extends RecyclerView.Adapter<FeesListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<FeesData> itemList;
    private ArrayList<Boolean> booleanArrayList;
    private ProgressDialog progressDialog;
    private UserPermissionCheck userPermissionCheck;

    private ViewClickListener mListener;

    public FeesListAdapter(Context context, ArrayList<FeesData> itemList) {
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
        private TextView tv_name, textView2, tv_amount;
        private RelativeLayout rel_main, rel_action_view, rel_edit, rel_delete;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            textView2 = view.findViewById(R.id.textView2);
            tv_amount = view.findViewById(R.id.tv_amount);
            rel_main = view.findViewById(R.id.rel_main);
            rel_action_view = view.findViewById(R.id.rel_action_view);
            rel_edit = view.findViewById(R.id.rel_edit);
            rel_delete = view.findViewById(R.id.rel_delete);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fees_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        FeesData feesData = itemList.get(position);

        if (feesData.getType_of_center().equals(ApiClient.coaching_center)){

            holder.tv_name.setText(feesData.getSubject_name());

            holder.textView2.setText("(" + feesData.getClass_name() + ")");

        }else {

            holder.tv_name.setText(feesData.getClass_name());
        }

        holder.tv_amount.setText(feesData.getFees_amount());


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

                FeesData feesData1 = itemList.get(position);

                editFeesDialog(feesData1);
            }
        });


        holder.rel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeesData feesData1 = itemList.get(position);

                deleteDialog(feesData1.getId());
            }
        });



        /// permission ...

        if (booleanArrayList.get(position) && (userPermissionCheck.isAdmin()
                || userPermissionCheck.isInstitute())){
            holder.rel_action_view.setVisibility(View.VISIBLE);
        }else {
            holder.rel_action_view.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface ViewClickListener {

        void onClick(String what_action);

    }

    public void setOnClickListener(ViewClickListener listener) {
        mListener = listener;
    }


    private void editFeesDialog(final FeesData feesData){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit_fees, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Update Fees Amount :");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        TextView tv_name = dialogView.findViewById(R.id.tv_name);
        final EditText edt_amount = dialogView.findViewById(R.id.edt_amount);
        RelativeLayout rel_update_fees = dialogView.findViewById(R.id.rel_update_fees);


        if (feesData.getType_of_center().equals(ApiClient.coaching_center)){

            tv_name.setText("Subject: "+feesData.getSubject_name());

            //holder.textView2.setText("(" + feesData.getClass_name() + ")");

        }else {

            tv_name.setText("Class: "+feesData.getClass_name());
        }

        edt_amount.setText(feesData.getFees_amount());
        edt_amount.setSelection(edt_amount.length());

        rel_update_fees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_amount.getText().toString().trim().length() == 0){
                    Toasty.info(context,
                            "Enter amount",
                            Toast.LENGTH_SHORT, true).show();

                    return;
                }

                alertDialog.dismiss();

                updateFees(feesData, edt_amount.getText().toString());
            }
        });


    }

    private void updateFees(FeesData feesData, String amount){

        progressDialog.show();

        String url = ApiClient.edit_fees;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.fees_id, feesData.getId());
        params.put(ApiClient.class_id, feesData.getClass_id());
        params.put(ApiClient.subject_id, feesData.getSubject_id());
        params.put(ApiClient.type_of_center, feesData.getType_of_center());
        params.put(ApiClient.fees_amount, amount);

        Log.d(Constants.TAG , "edit_fees - " + url);
        Log.d(Constants.TAG , "edit_fees - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "edit_fees- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(context,
                                    message,
                                    Toast.LENGTH_LONG, true).show();


                            mListener.onClick("update");


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
                Log.d(Commons.TAG, "edit_fees- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(context).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }



    private void deleteDialog(final String id) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Oado");
        builder.setMessage("Are you sure you want to delete this Guardian?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteFees(id);

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


    private void deleteFees(final String id){

        progressDialog.show();

        String url = ApiClient.delete_fees;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.fees_id, id);

        Log.d(Constants.TAG , "delete_fees - " + url);
        Log.d(Constants.TAG , "delete_fees - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "delete_fees- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(context,
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                            mListener.onClick("delete");

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
                Log.d(Commons.TAG, "delete_fees- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(context).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }



}
