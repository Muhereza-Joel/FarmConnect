package com.moels.farmconnect.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.moels.farmconnect.R;
import com.moels.farmconnect.models.Message;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    private List<Message> messageList;
    private String currentUserPhoneNumber;
    private Context context;

    public ChatMessageAdapter(Context context, List<Message> messageList, String currentUserPhoneNumber) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserPhoneNumber = currentUserPhoneNumber;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        // Set the layout gravity based on the message sender
        if (message.getSender().equals(currentUserPhoneNumber)) {
            holder.senderMessageContentTextView.setVisibility(View.VISIBLE);
            holder.senderMessageContentTextView.setText(message.getContent());
            holder.sendTimeTextView.setText(message.getTime());
            holder.sendTimeTextView.setVisibility(View.VISIBLE);
//            holder.senderMessageContentTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBlue));
        } else {
            holder.receiverMessageContentTextView.setText(message.getContent());
            holder.receiverMessageContentTextView.setVisibility(View.VISIBLE);
            holder.receiveTimeTextView.setText(message.getTime());
            holder.receiveTimeTextView.setVisibility(View.VISIBLE);
//            holder.receiverMessageContentTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        if (messageList == null) {
            return 0;
        }
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessageContentTextView, receiverMessageContentTextView, sendTimeTextView, receiveTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageContentTextView = itemView.findViewById(R.id.sender_message_content);
            receiverMessageContentTextView = itemView.findViewById(R.id.receiver_message_content);
            sendTimeTextView = itemView.findViewById(R.id.send_time);
            receiveTimeTextView = itemView.findViewById(R.id.receive_time);
        }
    }
}

