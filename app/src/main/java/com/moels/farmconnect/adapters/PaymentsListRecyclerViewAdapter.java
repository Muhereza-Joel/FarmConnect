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
import com.moels.farmconnect.models.PaymentCard;

import java.util.List;

public class PaymentsListRecyclerViewAdapter extends RecyclerView.Adapter<PaymentsListRecyclerViewAdapter.ViewHolder> {
    private List<PaymentCard> cardList;
    private Context context;

    public PaymentsListRecyclerViewAdapter(Context context, List<PaymentCard> cardList) {
        this.cardList = cardList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_card_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentCard paymentCard = cardList.get(position);
        holder.methodOfPaymentTextView.setText(paymentCard.getMethodOfPayment());
        holder.amountPayedTextView.setText(paymentCard.getAmountPayed());
        holder.receiverNameTextView.setText(paymentCard.getRecipientName());
        holder.dateTextView.setText(paymentCard.getDate());
        holder.timeTextView.setText(paymentCard.getTime());
        Glide.with(context).load(paymentCard.getImageUrl()).circleCrop().into(holder.receiverImageView);

    }

    @Override
    public int getItemCount() {
        if (cardList == null){
            return 0;
        }
        return cardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView methodOfPaymentTextView, amountPayedTextView,receiverNameTextView, dateTextView, timeTextView;
        private ImageView receiverImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            methodOfPaymentTextView = itemView.findViewById(R.id.method_of_payment_text_view);
            amountPayedTextView = itemView.findViewById(R.id.amount_payed_text_view);
            receiverNameTextView = itemView.findViewById(R.id.receiver_name_text_view);
            dateTextView = itemView.findViewById(R.id.create_date_text_view);
            timeTextView = itemView.findViewById(R.id.create_time_text_view);
            receiverImageView = itemView.findViewById(R.id.receiver_image_view);
        }
    }
}
