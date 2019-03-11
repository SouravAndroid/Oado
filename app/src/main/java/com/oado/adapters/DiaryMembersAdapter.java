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
import com.oado.models.DiaryMembersData;
import com.oado.utils.ApiClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiaryMembersAdapter extends
        RecyclerView.Adapter<DiaryMembersAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DiaryMembersData> originalList;
    private LayoutInflater inflater;

    public DiaryMembersAdapter(Context context, ArrayList<DiaryMembersData> itemList) {
        this.context = context;
        this.originalList = itemList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, textView2;
        private CircleImageView item_image;


        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            textView2 = view.findViewById(R.id.textView2);
            item_image = view.findViewById(R.id.item_image);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.diary_members_listitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DiaryMembersData diaryMembersData = originalList.get(position);

        holder.tv_name.setText(diaryMembersData.getName());

        if (diaryMembersData.getUser_type().equalsIgnoreCase(ApiClient.institute)){

            holder.textView2.setText("(Institute)");

        }else if (diaryMembersData.getUser_type().equalsIgnoreCase(ApiClient.teacher)){

            holder.textView2.setText("(Teacher)");

        }else if (diaryMembersData.getUser_type().equalsIgnoreCase(ApiClient.student)){

            holder.textView2.setText("(Student)");

        }else if (diaryMembersData.getUser_type().equalsIgnoreCase(ApiClient.guardian)){

            holder.textView2.setText("(Guardian)");

        }else if (diaryMembersData.getUser_type().equalsIgnoreCase(ApiClient.other_staff)){

            holder.textView2.setText("(Staff)");
        }


        if (!diaryMembersData.getImage().isEmpty()){

            Picasso.with(context).load(diaryMembersData.getImage()).placeholder(R.mipmap.no_image)
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



    }

    @Override
    public int getItemCount() {
        return originalList.size();
    }

}
