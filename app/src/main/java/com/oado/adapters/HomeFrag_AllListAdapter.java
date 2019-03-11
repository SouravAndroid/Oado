package com.oado.adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.widget.ShareDialog;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.oado.R;
import com.oado.activity.YoutubePlayerActivity;
import com.oado.models.DiaryMessage;
import com.oado.utils.Constants;
import com.oado.utils.DialogImage;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFrag_AllListAdapter extends RecyclerView.Adapter<HomeFrag_AllListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DiaryMessage> itemList;
    private ArrayList<Boolean> booleanArrayList;

   // String you_link = "https://www.youtube.com/watch?v=yncVs39wY_4";
    String you_link = "https://www.youtube.com/watch?v=tI8ijLpZaHk";


    public HomeFrag_AllListAdapter(Context context, ArrayList<DiaryMessage> itemList) {
        this.context = context;
        this.itemList = itemList;


        facebookSDKInitialize();

        booleanArrayList = new ArrayList<>();
        initState();
    }

    private void initState(){
        //booleanArrayList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++){
            booleanArrayList.add(false);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, textView2, textView3, tv_sender_dairy_name, tv_like;
        private CircleImageView item_image;
        private LinearLayout linear_announcement, linear_events, linear_images, linear_links;
        private TextView tv_announcement_msg, tv_event_msg, tv_start_date, tv_end_date, tv_image_msg;
        private ImageView iv_image, iv_play_on_youtube;
        private TextView tv_links_msg, tv_links;
        private YouTubeThumbnailView youtubeImageView;
        private RelativeLayout rel_youtube, rel_like, rel_fb_share;


        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            textView2 = view.findViewById(R.id.textView2);
            textView3 = view.findViewById(R.id.textView3);
            item_image = view.findViewById(R.id.item_image);

            tv_sender_dairy_name = view.findViewById(R.id.tv_sender_dairy_name);

            tv_announcement_msg = view.findViewById(R.id.tv_announcement_msg);

            tv_event_msg = view.findViewById(R.id.tv_event_msg);
            tv_start_date = view.findViewById(R.id.tv_start_date);
            tv_end_date = view.findViewById(R.id.tv_end_date);

            tv_image_msg = view.findViewById(R.id.tv_image_msg);
            iv_image = view.findViewById(R.id.iv_image);

            tv_links_msg = view.findViewById(R.id.tv_links_msg);
            tv_links = view.findViewById(R.id.tv_links);

            youtubeImageView = view.findViewById(R.id.youtubeImageView);
            iv_play_on_youtube = view.findViewById(R.id.iv_play_on_youtube);

            linear_announcement = view.findViewById(R.id.linear_announcement);
            linear_events = view.findViewById(R.id.linear_events);
            linear_images = view.findViewById(R.id.linear_images);
            linear_links = view.findViewById(R.id.linear_links);

            rel_youtube = view.findViewById(R.id.rel_youtube);
            rel_like = view.findViewById(R.id.rel_like);
            rel_fb_share = view.findViewById(R.id.rel_fb_share);
            tv_like = view.findViewById(R.id.tv_like);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_all_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DiaryMessage diaryMessage = itemList.get(position);

        holder.tv_name.setText(diaryMessage.getName());
        holder.textView2.setText("(" + diaryMessage.getUser_type().toUpperCase() + ")");
        holder.tv_sender_dairy_name.setText(diaryMessage.getDiary_name());
        holder.textView3.setText(diaryMessage.getCreated_date());


        if (!diaryMessage.getImage().isEmpty()){

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

        }



        if (diaryMessage.getMessage_type().equals("1")){

            holder.linear_announcement.setVisibility(View.VISIBLE);
            holder.linear_events.setVisibility(View.GONE);
            holder.linear_images.setVisibility(View.GONE);
            holder.linear_links.setVisibility(View.GONE);

            holder.tv_announcement_msg.setText(diaryMessage.getMessage());


        } else if (diaryMessage.getMessage_type().equals("2")){

            holder.linear_announcement.setVisibility(View.GONE);
            holder.linear_events.setVisibility(View.VISIBLE);
            holder.linear_images.setVisibility(View.GONE);
            holder.linear_links.setVisibility(View.GONE);

            holder.tv_event_msg.setText(diaryMessage.getMessage());
            holder.tv_start_date.setText("Event Start: "+diaryMessage.getEvent_start_date());
            holder.tv_end_date.setText("Event End: "+diaryMessage.getEvent_end_date());

        } else if (diaryMessage.getMessage_type().equals("3")){

            holder.linear_announcement.setVisibility(View.GONE);
            holder.linear_events.setVisibility(View.GONE);
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

                    DialogImage dialogImage = new DialogImage(context, diaryMessage.getPhoto());
                    dialogImage.show();

                }
            });

        } else if (diaryMessage.getMessage_type().equals("4")){

            holder.linear_announcement.setVisibility(View.GONE);
            holder.linear_events.setVisibility(View.GONE);
            holder.linear_images.setVisibility(View.GONE);
            holder.linear_links.setVisibility(View.VISIBLE);

            holder.rel_youtube.setVisibility(View.GONE);
            holder.tv_links.setVisibility(View.VISIBLE);


            holder.tv_links_msg.setText(diaryMessage.getMessage());
            holder.tv_links.setText(diaryMessage.getLink());


        } else if (diaryMessage.getMessage_type().equals("5")){

            holder.linear_announcement.setVisibility(View.GONE);
            holder.linear_events.setVisibility(View.GONE);
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

                                //youTubeThumbnailLoader.setVideo(extractYoutubeId(you_link));
                                youTubeThumbnailLoader.setVideo(extractYTId(you_link));

                            }catch (MalformedURLException e){
                                e.printStackTrace();
                            }catch (Exception e){
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



        if (booleanArrayList.get(position)){
            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_fill,
                    0, 0, 0);
        }else {
            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_unfill,
                    0, 0, 0);
        }

        holder.rel_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!booleanArrayList.get(position)){
                    booleanArrayList.set(position, true);
                }

                notifyDataSetChanged();

            }
        });



        holder.rel_fb_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShareDialog shareDialog = new ShareDialog((Activity)context);

                if (diaryMessage.getMessage_type().equals("1")){
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("Oado")
                                .setContentDescription(diaryMessage.getMessage())
                                .build();
                        shareDialog.show(linkContent);

                    }
                }else if (diaryMessage.getMessage_type().equals("2")){
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("Oado")
                                .setContentDescription(diaryMessage.getMessage())
                                .build();
                        shareDialog.show(linkContent);

                    }
                }else if (diaryMessage.getMessage_type().equals("3")){
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("Oado")
                                .setContentDescription(diaryMessage.getMessage())
                                .setContentUrl(Uri.parse(diaryMessage.getPhoto()))
                                .build();
                        shareDialog.show(linkContent);


                    }
                }else if (diaryMessage.getMessage_type().equals("4")){
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("Oado")
                                .setContentDescription(diaryMessage.getMessage())
                                .setContentUrl(Uri.parse(diaryMessage.getLink()))
                                .build();
                        shareDialog.show(linkContent);

                    }
                }else if (diaryMessage.getMessage_type().equals("5")){
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("Oado")
                                .setContentDescription(diaryMessage.getMessage())
                                .setContentUrl(Uri.parse(diaryMessage.getYoutube_link()))
                                .build();
                        shareDialog.show(linkContent);

                    }
                }


            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
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



    CallbackManager callbackManager;
    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }
}
