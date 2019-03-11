package com.oado.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.oado.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.MyViewHolder>
        implements Filterable{

    private Context context;
    private ArrayList<String> itemList;
    private ArrayList<String> listFiltered;

    public FavouriteListAdapter(Context context, ArrayList<String> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.listFiltered = itemList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_message;
        private CircleImageView item_image;
        private ImageView iv_delete, iv_starred;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_message = view.findViewById(R.id.tv_message);
            item_image = view.findViewById(R.id.item_image);
            iv_delete = view.findViewById(R.id.iv_delete);
            iv_starred = view.findViewById(R.id.iv_starred);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);




        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_name.setText("Abc Xyz");

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = itemList;
                } else {
                    ArrayList<String> filteredList = new ArrayList<>();
                    for (String row : itemList) {

                        // here we are looking for name or phone number match
                        if (row.toLowerCase().contains(charString.toLowerCase())
                                || row.contains(charSequence)) {
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
                listFiltered = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
