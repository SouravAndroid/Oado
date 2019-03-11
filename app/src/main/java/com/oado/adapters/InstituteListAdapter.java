package com.oado.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.activity.ComposeMessage;
import com.oado.activity.InstituteAdd;
import com.oado.models.InstituteData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.DialogImage;
import com.oado.utils.PrefManager;
import com.oado.utils.UserPermissionCheck;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class InstituteListAdapter extends RecyclerView.Adapter<InstituteListAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private ArrayList<InstituteData> originalList;
    private ArrayList<InstituteData> filteredArrayList;
    private ArrayList<Boolean> booleanArrayList;
    private ProgressDialog progressDialog;

    private PrefManager prefManager;
    private UserPermissionCheck userPermissionCheck;


    public InstituteListAdapter(Context context, ArrayList<InstituteData> itemList) {
        this.context = context;
        this.originalList = itemList;
        this.filteredArrayList = itemList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        prefManager = new PrefManager(context);
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
        private TextView tv_name, textView2;
        private CircleImageView item_image;
        private LinearLayout linear_main;
        private RelativeLayout rel_action_view, rel_view, rel_edit, rel_delete, rel_send_message;
        private ImageView iv_lock_unlock;

        public MyViewHolder(View view) {
            super(view);
            tv_name =  view.findViewById(R.id.tv_name);
            textView2 =  view.findViewById(R.id.textView2);
            item_image =  view.findViewById(R.id.item_image);
            linear_main =  view.findViewById(R.id.linear_main);
            rel_action_view =  view.findViewById(R.id.rel_action_view);
            rel_view = view.findViewById(R.id.rel_view);
            rel_edit = view.findViewById(R.id.rel_edit);
            rel_delete = view.findViewById(R.id.rel_delete);
            rel_send_message = view.findViewById(R.id.rel_send_message);
            iv_lock_unlock = view.findViewById(R.id.iv_lock_unlock);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.institute_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final InstituteData instituteData = filteredArrayList.get(position);

        final int pos = holder.getAdapterPosition();

        holder.tv_name.setText(instituteData.getName());
        holder.textView2.setText("Code: "+instituteData.getCode() + "\n"
                + "Board: "+ instituteData.getBoard_name());


        if (!instituteData.getImage().isEmpty()) {
            Picasso.with(context).load(instituteData.getImage()).placeholder(R.mipmap.no_image)
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
        }

        if (instituteData.getLock_status() == 1){
            holder.iv_lock_unlock.setImageResource(R.mipmap.lock_red);
        }else {
            holder.iv_lock_unlock.setImageResource(R.mipmap.unlock);
        }



        if (booleanArrayList.get(pos)){
            holder.rel_action_view.setVisibility(View.VISIBLE);
        }else {
            holder.rel_action_view.setVisibility(View.GONE);
        }

        holder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (booleanArrayList.get(pos)){
                    booleanArrayList.set(pos, false);
                }else {
                    initState();
                    booleanArrayList.set(pos, true);
                }

                notifyDataSetChanged();

            }
        });


        holder.rel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InstituteData instituteData1 = filteredArrayList.get(position);

                Intent intent = new Intent(context, InstituteAdd.class);
                intent.putExtra("user_data", instituteData1);
                intent.putExtra("type", "edit");

                context.startActivity(intent);

            }
        });

        holder.rel_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InstituteData instituteData1 = filteredArrayList.get(position);

                Intent intent = new Intent(context, InstituteAdd.class);
                intent.putExtra("user_data", instituteData1); //pass bundle to your intent
                intent.putExtra("type", "view");

                context.startActivity(intent);
            }
        });

        holder.rel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InstituteData instituteData1 = filteredArrayList.get(position);

                deleteDialog(instituteData1.getId());
            }
        });



        holder.item_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogImage dialogImage = new DialogImage(context, instituteData.getImage());
                dialogImage.show();
            }
        });


        holder.rel_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InstituteData instituteData1 = filteredArrayList.get(position);

                Intent intent = new Intent(context, ComposeMessage.class);
                intent.putExtra("id", instituteData1.getId());
                intent.putExtra("name", instituteData1.getName());
                intent.putExtra("type", instituteData1.getInstitute_type());
                context.startActivity(intent);
            }
        });


        holder.iv_lock_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (instituteData.getLock_status() == 0){

                    lockUnlockInstitute(instituteData.getId(), 1);

                }else {

                    lockUnlockInstitute(instituteData.getId(), 0);
                }

            }
        });



        ///// permission

        if (userPermissionCheck.isAdmin()){
            holder.rel_view.setVisibility(View.VISIBLE);
            holder.rel_edit.setVisibility(View.VISIBLE);
            holder.rel_delete.setVisibility(View.VISIBLE);
            holder.rel_send_message.setVisibility(View.VISIBLE);
            holder.iv_lock_unlock.setVisibility(View.VISIBLE);
        }else if (userPermissionCheck.isInstitute()){
            holder.rel_view.setVisibility(View.VISIBLE);
            holder.rel_edit.setVisibility(View.GONE);
            holder.rel_delete.setVisibility(View.GONE);
            holder.rel_send_message.setVisibility(View.GONE);
            holder.iv_lock_unlock.setVisibility(View.GONE);
        }else {
            holder.rel_view.setVisibility(View.VISIBLE);
            holder.rel_edit.setVisibility(View.GONE);
            holder.rel_delete.setVisibility(View.GONE);
            holder.rel_send_message.setVisibility(View.VISIBLE);
            holder.iv_lock_unlock.setVisibility(View.GONE);
        }

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
                    ArrayList<InstituteData> filteredList = new ArrayList<>();
                    for (InstituteData row : originalList) {

                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getCode().toLowerCase().contains(charString.toLowerCase())
                                || row.getBoard_name().toLowerCase().contains(charString.toLowerCase())) {

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

                    filteredArrayList = (ArrayList<InstituteData>) filterResults.values;

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
        builder.setMessage("Are you sure you want to delete this institute?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteInstitute(id);

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

    private void deleteInstitute(final String id){

        progressDialog.show();

        String url = ApiClient.delete_institute;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, id);

        Log.d(Constants.TAG , "delete_institute - " + url);
        Log.d(Constants.TAG , "delete_institute - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "delete_institute- " + response.toString());

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
                Log.d(Commons.TAG, "delete_institute- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(context).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });





    }

    private void lockUnlockInstitute(String institute_id, final int inst_status){

        progressDialog.show();

        String url = ApiClient.lock_unlock_status;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, institute_id);
        params.put("lock_unlock_status", inst_status);

        Log.d(Constants.TAG , "lock_unlock_status - " + url);
        Log.d(Constants.TAG , "lock_unlock_status - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "lock_unlock_status- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(context,
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                            mViewClickListener.onStatusChange(inst_status);

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
                Log.d(Commons.TAG, "lock_unlock_status- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(context).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }


    private ViewClickListener mViewClickListener;

    public interface ViewClickListener {
        void onStatusChange(int status);
    }

    public void setViewClickListener (ViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }

}
