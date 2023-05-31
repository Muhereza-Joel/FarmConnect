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
import com.moels.farmconnect.models.ProductCardItem;

import java.util.List;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {

    private List<ProductCardItem> productCardItems;
    private Context context;
    private Listener listener;


    public ProductsRecyclerViewAdapter(List<ProductCardItem> productCardItems, Context context) {
        this.productCardItems = productCardItems;
        this.context = context;
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
        holder.productNameTextView.setText(productCardItem.getProductName());
        holder.productQuantityTextView.setText(productCardItem.getQuantity());
        holder.statusTextView.setText(productCardItem.getStatus());

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
        ImageView productImageView;
        TextView productNameTextView, productQuantityTextView, createTimeTextView, statusTextView, _idTextView;
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

        }

    }

    public interface Listener{
        void onClick(int position);
    }

}
