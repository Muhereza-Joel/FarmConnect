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
import com.moels.farmconnect.models.ProductCardItem;
import com.moels.farmconnect.utility_classes.ContactsDatabaseHelper;

import java.util.List;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {

    private List<ProductCardItem> productCardItems;
    private Context context;
    private Listener listener;
    private ContactsDatabaseHelper contactsDatabaseHelper;


    public ProductsRecyclerViewAdapter(List<ProductCardItem> productCardItems, Context context) {
        this.productCardItems = productCardItems;
        this.context = context;
        contactsDatabaseHelper = new ContactsDatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_list_card_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CardView cardView = holder.productListItemCardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(position);
                }
            }
        });

        ProductCardItem productCardItem = productCardItems.get(position);
        holder._idTextView.setText(productCardItem.get_id());
        Glide.with(context).load(productCardItem.getPhotoUrl()).circleCrop().into(holder.productImageView);
        holder.productNameTextView.setText(productCardItem.getProductName());
        holder.productQuantityTextView.setText(productCardItem.getQuantity());
        holder.createTimeTextView.setText(productCardItem.getCreateTime());
        holder.statusTextView.setText(productCardItem.getStatus());
        holder.ownerTextView.setText(contactsDatabaseHelper.getOwnerUsername(productCardItem.getOwner()));
        Glide.with(context).load(contactsDatabaseHelper.getOwnerImageUrl(productCardItem.getOwner())).circleCrop().into(holder.productCreatorImageView);

    }

    @Override
    public int getItemCount() {
        if (productCardItems == null){
            return 0;
        }
        return productCardItems.size();
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView, productCreatorImageView;
        TextView productNameTextView, productQuantityTextView, createTimeTextView, statusTextView, _idTextView, ownerTextView;
        CardView productListItemCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImageView = itemView.findViewById(R.id.photoImageView);
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            productQuantityTextView = itemView.findViewById(R.id.product_quantity_text_view);
            createTimeTextView = itemView.findViewById(R.id.create_time_text_view);
            statusTextView = itemView.findViewById(R.id.status_text_view);
            _idTextView = itemView.findViewById(R.id.product_id);
            productListItemCardView = itemView.findViewById(R.id.product_list_item_card_view);
            ownerTextView = itemView.findViewById(R.id.owner_text_view);
            productCreatorImageView = itemView.findViewById(R.id.product_creator_image_view);

        }
        //TODO set card background for light and dark mode

    }

    public interface Listener{
        void onClick(int position);
    }

}
