package com.moels.farmconnect.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moels.farmconnect.R;
import com.moels.farmconnect.models.ChatCardItem;

import java.util.List;


public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ViewHolder> {

    //List of items to pass to the card view
    private List<ChatCardItem> itemList;
    private Context context;
    private Listener listener;

    public ChatListRecyclerViewAdapter(List<ChatCardItem> itemList, Context context){
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_card_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CardView cardView = holder.cardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(position);
            }
        });

        ChatCardItem item = itemList.get(position);
        holder.usernameTextView.setText(item.getUsername());
        Glide.with(context).load(item.getPhotoUrl()).circleCrop().into(holder.photoImageView);
        //TODO set card background for light and dark mode
    }

    @Override
    public int getItemCount() {
        if (itemList == null) {
            return 0;
        }
        return itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView photoImageView;
        public TextView usernameTextView;
        public CardView cardView;
        public ViewHolder(View itemView){
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            cardView = itemView.findViewById(R.id.chat_list_item_card_view);
        }

    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public interface Listener {
        void onClick(int position);
    }
}
