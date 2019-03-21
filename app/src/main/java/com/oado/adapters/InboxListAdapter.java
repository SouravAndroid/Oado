package com.oado.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.models.DiaryMessage;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.DialogImage;
import com.oado.utils.PrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class InboxListAdapter extends RecyclerView.Adapter<InboxListAdapter.MyViewHolder>
        implements Filterable{

    private Context context;
    private ArrayList<DiaryMessage> originalList;
    private ArrayList<DiaryMessage> listFiltered;

    //private String you_link = "https://www.youtube.com/watch?v=tI8ijLpZaHk";
    private String you_link = "";

    private ProgressDialog progressDialog;
    private PrefManager prefManager;


    public InboxListAdapter(Context context, ArrayList<DiaryMessage> itemList) {
        this.context = context;
        this.originalList = itemList;
        this.listFiltered = itemList;

        prefManager = new PrefManager(context);

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, textView2, textView3;
        private CircleImageView item_image;
        private LinearLayout linear_announcement, linear_images, linear_links;
        private TextView tv_announcement_msg, tv_image_msg;
        private ImageView iv_image, iv_play_on_youtube;
        private TextView tv_links_msg, tv_links;
        private YouTubeThumbnailView youtubeImageView;
        private RelativeLayout rel_youtube;
        private ImageView iv_delete;

        public MyViewHolder(View view) {
            super(view);

            tv_name = view.findViewById(R.id.tv_name);
            textView2 = view.findViewById(R.id.textView2);
            textView3 = view.findViewById(R.id.textView3);
            item_image = view.findViewById(R.id.item_image);
            tv_announcement_msg = view.findViewById(R.id.tv_announcement_msg);
            tv_image_msg = view.findViewById(R.id.tv_image_msg);
            iv_image = view.findViewById(R.id.iv_image);
            tv_links_msg = view.findViewById(R.id.tv_links_msg);
            tv_links = view.findViewById(R.id.tv_links);
            youtubeImageView = view.findViewById(R.id.youtubeImageView);
            iv_play_on_youtube = view.findViewById(R.id.iv_play_on_youtube);

            linear_announcement = view.findViewById(R.id.linear_announcement);
            linear_images = view.findViewById(R.id.linear_images);
            linear_links = view.findViewById(R.id.linear_links);

            rel_youtube = view.findViewById(R.id.rel_youtube);
            iv_delete = view.findViewById(R.id.iv_delete);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inbox_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final DiaryMessage diaryMessage = listFiltered.get(position);

        holder.tv_name.setText(diaryMessage.getName());
        holder.textView2.setText("(" + diaryMessage.getUser_type().toUpperCase() + ")");
        holder.textView3.setText(getShowDateFormat(diaryMessage.getCreated_date()));


        Picasso.with(context).load(diaryMessage.getImage()).placeholder(R.mipmap.no_image)
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



        if (diaryMessage.getMessage_type().equals("1")){

            holder.linear_announcement.setVisibility(View.VISIBLE);
            holder.linear_images.setVisibility(View.GONE);
            holder.linear_links.setVisibility(View.GONE);

            holder.tv_announcement_msg.setText(diaryMessage.getMessage());

        } else if (diaryMessage.getMessage_type().equals("2")){

            holder.linear_announcement.setVisibility(View.GONE);
            holder.linear_images.setVisibility(View.VISIBLE);
            holder.linear_links.setVisibility(View.GONE);

            holder.tv_image_msg.setText(diaryMessage.getMessage());

            if (!diaryMessage.getPhoto().isEmpty()){

                Picasso.with(context).load(diaryMessage.getPhoto()).placeholder(R.mipmap.no_image)
                        .into(holder.iv_image, new com.squareup.picasso.Callback() {
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



            holder.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, DialogImage.class);
                    intent.putExtra("url", diaryMessage.getPhoto());
                    context.startActivity(intent);

                }
            });

        } else if (diaryMessage.getMessage_type().equals("3")){

            holder.linear_announcement.setVisibility(View.GONE);
            holder.linear_images.setVisibility(View.GONE);
            holder.linear_links.setVisibility(View.VISIBLE);

            holder.rel_youtube.setVisibility(View.GONE);
            holder.tv_links.setVisibility(View.VISIBLE);


            holder.tv_links_msg.setText(diaryMessage.getMessage());
            holder.tv_links.setText(diaryMessage.getLink());


        } else if (diaryMessage.getMessage_type().equals("4")){

            holder.linear_announcement.setVisibility(View.GONE);
            holder.linear_images.setVisibility(View.GONE);
            holder.linear_links.setVisibility(View.VISIBLE);

            holder.rel_youtube.setVisibility(View.VISIBLE);
            holder.tv_links.setVisibility(View.GONE);

            holder.tv_links_msg.setText(diaryMessage.getMessage());

            you_link = diaryMessage.getYoutube_link();

            holder.youtubeImageView.initialize(Constants.DEVELOPER_KEY,
                    new YouTubeThumbnailView.OnInitializedListener() {

                        @Override
                        public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView,
                                                            final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                            //when initialization is success, set the video id to thumbnail to load

                            try {

                                youTubeThumbnailLoader.setVideo(extractYTId(you_link));

                            }catch (MalformedURLException e){
                                e.printStackTrace();
                            }


                            youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                                @Override
                                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                                    //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                                    youTubeThumbnailLoader.release();
                                }

                                @Override
                                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                                    //print or show error when thumbnail load failed
                                    Log.e("TAG", "Youtube Thumbnail Error");
                                }
                            });
                        }

                        @Override
                        public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                            //print or show error when initialization failed
                            Log.e("TAG", "Youtube Initialization Failure");

                        }
                    });


            holder.iv_play_on_youtube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        watchYoutubeVideo(context, extractYTId(you_link));
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }

                }
            });

        }



        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteDialog(diaryMessage.getId());

            }
        });

    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = originalList;
                } else {
                    ArrayList<DiaryMessage> filteredList = new ArrayList<>();
                    for (DiaryMessage row : originalList) {

                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getMessage().toLowerCase().contains(charString.toLowerCase())
                                || row.getUser_type().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    listFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (charSequence != null) {

                    listFiltered = (ArrayList<DiaryMessage>) filterResults.values;

                    if (listFiltered != null){
                        notifyDataSetChanged();
                    }else {
                        listFiltered = new ArrayList<>();
                    }

                }else {
                    listFiltered = originalList;

                    notifyDataSetChanged();
                }
            }
        };
    }


    private String extractYoutubeId(String url) throws MalformedURLException {
        String query = new URL(url).getQuery();
        String[] param = query.split("&");
        String id = null;
        for (String row : param) {
            String[] param1 = row.split("=");
            if (param1[0].equals("v")) {
                id = param1[1];
            }
        }
        return id;
    }

    private static String extractYTId(String ytUrl) throws MalformedURLException {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }


    private void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(you_link));
        try {
            context.startActivity(appIntent);

            /*Intent intent = new Intent(context, YoutubePlayerActivity.class);
            intent.putExtra("id", id);
            context.startActivity(intent);*/

        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }


    private String getShowDateFormat(String sourceDate){
        String formattedDate = "";
        try {
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            Date date = originalFormat.parse(sourceDate);
            formattedDate = targetFormat.format(date);  //
        }catch (ParseException e){
            e.printStackTrace();
        }

        return formattedDate;
    }

    private void deleteDialog(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Oado");
        builder.setMessage("Are you sure you want to delete this Message?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteMessage(id);

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


    private void deleteMessage(final String id){

        progressDialog.show();

        String url = ApiClient.delete_message;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.message_id, id);
        params.put(ApiClient.user_id, prefManager.getId());

        Log.d(Constants.TAG , "delete_message - " + url);
        Log.d(Constants.TAG , "delete_message - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "delete_message- " + response.toString());

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

                            for (int i = 0; i < listFiltered.size(); i++){
                                if (id.matches(listFiltered.get(i).getId())){
                                    listFiltered.remove(i);
                                }
                            }

                            listFiltered = originalList;

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
                Log.d(Commons.TAG, "delete_class- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(context).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }
}
