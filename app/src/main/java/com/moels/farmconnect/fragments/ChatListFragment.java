package com.moels.farmconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.activities.ChatActivity;
import com.moels.farmconnect.adapters.ChatListRecyclerViewAdapter;
import com.moels.farmconnect.models.ChatCardItem;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatListRecyclerViewAdapter recyclerViewAdapter;
    private Context context;
    private List<ChatCardItem> chatCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.farmers_page_fragment, container, false);

        //Crete adapter and set it on the recycler view
        ChatListRecyclerViewAdapter recyclerViewAdapter = new ChatListRecyclerViewAdapter(chatCard, context);

        context = getContext();
        // Return the inflated view
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialise the recycler view
        recyclerView = getView().findViewById(R.id.recyclerView);

        //setLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // load data into the RecyclerView
        initializeCardItemList();
        recyclerViewAdapter = new ChatListRecyclerViewAdapter(chatCard, getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        context = getContext();

        recyclerViewAdapter.setListener(new ChatListRecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("activeChatUsername", chatCard.get(position).getUsername());
                intent.putExtra("profilePicture", chatCard.get(position).getPhotoUrl());
                getActivity().startActivity(intent);
            }
        });
    }

    private void initializeCardItemList() {
        // initialize the itemList
        chatCard = new ArrayList<>();

        // load sample data and images from URLs
        String[] farmerUsernames = {"Muhereza Joel", "Kobusinge Stella" };
        String[] farmerProfilePicUrls = {"data:image/jpeg;base64,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","data:image/jpeg;base64,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" };

        for (int i = 0; i < farmerUsernames.length; i++) {
            String username = farmerUsernames[i];
            String imageUrl = farmerProfilePicUrls[i];

            // create an Item object with the name and image URL
            ChatCardItem chatCardItem = new ChatCardItem(username, imageUrl);

            // add the Item to the itemList
            chatCard.add(chatCardItem);
        }
    }
}
