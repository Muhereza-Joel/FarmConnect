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

import com.moels.farmconnect.R;
import com.moels.farmconnect.models.ContactCardItem;

import java.util.List;

public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.ViewHolder> {

    private List<ContactCardItem> itemList;
    private Context context;

    public ContactListRecyclerViewAdapter(List<ContactCardItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    private Listener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_card_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CardView  cardView = holder.contactListItemCardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(position);
            }
        });

        ContactCardItem contactCardItem = itemList.get(position);
        holder.usernameTextView.setText(contactCardItem.getUsername());
        holder.phoneNumberTextView.setText(contactCardItem.getPhoneNumber());
        //TODO use glide to load profile pic
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
        ImageView profilePicImageView;
        TextView usernameTextView, phoneNumberTextView;
        CardView contactListItemCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicImageView = itemView.findViewById(R.id.photoImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            contactListItemCardView = itemView.findViewById(R.id.contact_list_item_card_view);
        }
    }

    public void setListener(Listener listener){this.listener = listener;}

    public interface Listener{
        void onClick(int position);
    }
}
