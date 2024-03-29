package com.moels.farmconnect.view.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.moels.farmconnect.utils.models.ZoneCardItem;
import com.moels.farmconnect.model.database.ContactsTable;
import com.moels.farmconnect.model.database.ContactsTableUtil;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Globals;
import com.moels.farmconnect.utils.preferences.Preferences;

import java.util.List;

public class ZoneListRecyclerViewAdapter extends RecyclerView.Adapter<ZoneListRecyclerViewAdapter.ViewHolder>{

    private List<ZoneCardItem> itemList;
    private Context context;
    private Listener listener;
    private ContactsTable contactsDatabase;
    private Preferences preferences;

    public ZoneListRecyclerViewAdapter(List<ZoneCardItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
        contactsDatabase = ContactsTableUtil.getInstance(context);
        preferences = FarmConnectAppPreferences.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zones_list_card_rectcler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ZoneCardItem zoneCardItem = itemList.get(position);
        CardView cardView = holder.zoneListItemCardView;

        holder._idTextView.setText(zoneCardItem.get_id());
        holder.zoneNameTextView.setText(zoneCardItem.getZoneName());
        holder.zoneLocationTextView.setText(zoneCardItem.getLocation());
        holder.createTimeTextView.setText(zoneCardItem.getCreateTime());

        if (zoneCardItem.getStatus().equals(Globals.CLOSED)){
        holder.statusTextView.setText(zoneCardItem.getStatus());
        holder.statusTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));

        if (preferences.isBuyerAccount()){
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onClick(position);
                }
            });
        }
        } else {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onClick(position);
                }
            });
            holder.statusTextView.setText(zoneCardItem.getStatus());
        }

        holder.zoneCreatorTextView.setText(contactsDatabase.getOwnerUsername(zoneCardItem.getOwner()));
        Glide.with(context).load(contactsDatabase.getOwnerImageUrl(zoneCardItem.getOwner())).circleCrop().into(holder.zoneOwnerImageView);
    }
    //TODO set card background for light and dark mode

    @Override
    public int getItemCount() {
        if (itemList == null){
            return 0;
        }
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView zoneLogoImageView, zoneOwnerImageView;
        TextView zoneNameTextView, zoneLocationTextView, createTimeTextView, statusTextView, _idTextView, zoneCreatorTextView;
        CardView zoneListItemCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            zoneLogoImageView = itemView.findViewById(R.id.photoImageView);
            zoneNameTextView = itemView.findViewById(R.id.zone_name_text_view);
            zoneLocationTextView = itemView.findViewById(R.id.zone_location_text_view);
            zoneListItemCardView = itemView.findViewById(R.id.zone_list_item_card_view);
            createTimeTextView = itemView.findViewById(R.id.create_time_text_view);
            statusTextView = itemView.findViewById(R.id.status_text_view);
            _idTextView = itemView.findViewById(R.id.zone_id);
            zoneCreatorTextView = itemView.findViewById(R.id.zone_owner_text_view);
            zoneOwnerImageView = itemView.findViewById(R.id.zone_creator_image_view);
        }
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }
    public interface Listener{
        void onClick(int position);
    }
}
