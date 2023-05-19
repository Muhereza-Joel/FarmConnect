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
import com.moels.farmconnect.models.ZoneCardItem;

import java.util.List;

public class ZoneListRecyclerViewAdapter extends RecyclerView.Adapter<ZoneListRecyclerViewAdapter.ViewHolder>{

    private List<ZoneCardItem> itemList;
    private Context context;
    private Listener listener;

    public ZoneListRecyclerViewAdapter(List<ZoneCardItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zones_list_card_rectcler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CardView cardView = holder.zoneListItemCardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(position);
            }
        });

        ZoneCardItem zoneCardItem = itemList.get(position);
        holder._idTextView.setText(zoneCardItem.get_id());
        holder.zoneNameTextView.setText(zoneCardItem.getZoneName());
        holder.zoneLocationTextView.setText(zoneCardItem.getLocation());
        holder.createTimeTextView.setText(zoneCardItem.getCreateTime());
        holder.statusTextView.setText(zoneCardItem.getStatus());
    }

    @Override
    public int getItemCount() {
        if (itemList == null){
            return 0;
        }
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView zoneLogoImageView;
        TextView zoneNameTextView, zoneLocationTextView, createTimeTextView, statusTextView, _idTextView;
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
        }
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }
    public interface Listener{
        void onClick(int position);
    }
}
