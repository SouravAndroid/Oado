package com.oado.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oado.R;
import com.oado.activity.DiaryMessageSingle;
import com.oado.activity.InboxMessages;
import com.oado.utils.PrefManager;

import java.util.ArrayList;

public class HomeFrag_Group_Adapter extends RecyclerView.Adapter<HomeFrag_Group_Adapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> listTitle;
    private ArrayList<Integer> listIcon;
    private PrefManager prefManager;

    public HomeFrag_Group_Adapter(Context context, ArrayList<String> listTitle,
                                  ArrayList<Integer> listIcon) {
        this.context = context;
        this.listTitle = listTitle;
        this.listIcon = listIcon;

        prefManager = new PrefManager(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView, textView_counter;
        private ImageView imageView;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            textView = view.findViewById(R.id.textView);
            cardView = view.findViewById(R.id.cardView);
            textView_counter = view.findViewById(R.id.textView_counter);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.frag_1_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@Nullable MyViewHolder holder, final int position) {

        holder.textView.setText(listTitle.get(position));
        holder.imageView.setImageResource(listIcon.get(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((position + 1) <= 5){

                    Intent intent1 = new Intent(context, DiaryMessageSingle.class);
                    intent1.putExtra("name", listTitle.get(position));
                    intent1.putExtra("type", String.valueOf(position+1));
                    context.startActivity(intent1);

                }else {

                    Intent intent2 = new Intent(context, InboxMessages.class);
                    context.startActivity(intent2);
                }

            }
        });


        setCount(position, holder.textView_counter);



    }

    @Override
    public int getItemCount() {
        return listTitle.size();
    }

    private void setCount(int pos, TextView textView){
        switch (pos){
            case 0:
                textView.setText(""+prefManager.getKey1());

                if (prefManager.getKey1() == 0){
                    textView.setVisibility(View.INVISIBLE);
                }else {
                    textView.setVisibility(View.VISIBLE);
                }

                break;
            case 1:
                textView.setText(""+prefManager.getKey2());

                if (prefManager.getKey2() == 0){
                    textView.setVisibility(View.INVISIBLE);
                }else {
                    textView.setVisibility(View.VISIBLE);
                }

                break;

            case 2:
                textView.setText(""+prefManager.getKey3());

                if (prefManager.getKey3() == 0){
                    textView.setVisibility(View.INVISIBLE);
                }else {
                    textView.setVisibility(View.VISIBLE);
                }

                break;

            case 3:
                textView.setText(""+prefManager.getKey4());

                if (prefManager.getKey4() == 0){
                    textView.setVisibility(View.INVISIBLE);
                }else {
                    textView.setVisibility(View.VISIBLE);
                }

                break;

            case 4:
                textView.setText(""+prefManager.getKey5());

                if (prefManager.getKey5() == 0){
                    textView.setVisibility(View.INVISIBLE);
                }else {
                    textView.setVisibility(View.VISIBLE);
                }

                break;

            case 5:
                textView.setText(""+prefManager.getKey6());

                if (prefManager.getKey6() == 0){
                    textView.setVisibility(View.INVISIBLE);
                }else {
                    textView.setVisibility(View.VISIBLE);
                }

                break;

        }

    }

}
