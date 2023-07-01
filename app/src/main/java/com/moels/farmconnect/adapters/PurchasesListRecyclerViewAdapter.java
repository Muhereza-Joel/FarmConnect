package com.moels.farmconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moels.farmconnect.R;
import com.moels.farmconnect.models.PurchasesCard;

import java.util.List;

public class PurchasesListRecyclerViewAdapter extends RecyclerView.Adapter<PurchasesListRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<PurchasesCard> purchasesCards;

    public PurchasesListRecyclerViewAdapter(Context context, List<PurchasesCard> purchasesCards) {
        this.context = context;
        this.purchasesCards = purchasesCards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchases_recyler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurchasesCard purchasesCard = purchasesCards.get(position);
        holder.productNameTextView.setText(purchasesCard.getProductName());
        holder.productQuantityTextView.setText(purchasesCard.getQuantity());
        holder.timeTextView.setText(purchasesCard.getTime());
        holder.dateTextView.setText(purchasesCard.getDate());
        holder.productOwnerNameTextView.setText(purchasesCard.getSeller());
        Glide.with(context).load(purchasesCard.getImageUrl()).circleCrop().into(holder.productOwnerImageView);
    }

    @Override
    public int getItemCount() {
        if (purchasesCards == null){
            return 0;
        }
        return purchasesCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView productNameTextView, productQuantityTextView, productOwnerNameTextView, dateTextView, timeTextView;
        private ImageView productOwnerImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            productQuantityTextView = itemView.findViewById(R.id.product_quantity_text_view);
            productOwnerNameTextView = itemView.findViewById(R.id.seller_name_text_view);
            dateTextView = itemView.findViewById(R.id.create_date_text_view);
            timeTextView = itemView.findViewById(R.id.create_time_text_view);
            productOwnerImageView = itemView.findViewById(R.id.product_owner_image_view);
        }
    }
}
