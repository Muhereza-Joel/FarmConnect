package com.moels.farmconnect.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

    public ChatMessageAdapter(List<Message> messageList, String currentUserPhoneNumber) {
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
        holder.textMessageContent.setText(message.getContent());

        // Set the layout gravity based on the message sender
        if (message.getSender().equals(currentUserPhoneNumber)) {
            holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        } else {
            holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
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
        TextView textMessageContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageContent = itemView.findViewById(R.id.text_message_content);
        }
    }
}

