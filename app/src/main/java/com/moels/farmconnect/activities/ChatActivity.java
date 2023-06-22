package com.moels.farmconnect.activities;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moels.farmconnect.R;
import com.moels.farmconnect.adapters.ChatMessageAdapter;
import com.moels.farmconnect.models.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ImageView profilePic;
    private TextView activeChatTextView;
    private ImageButton backButton, searchIcon;
    private RecyclerView recyclerView;
    private RelativeLayout topBar;
    private ChatMessageAdapter chatMessageAdapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUI();
        setUPTopBarForDarkMode();
        setUpStatusBar();

        String activeChatUsername = getIntent().getStringExtra("activeChatUsername");
        String profilePictureUrl = getIntent().getStringExtra("profilePicture");
        Glide.with(this).load(profilePictureUrl).circleCrop().into(profilePic);
        activeChatTextView.setText(activeChatUsername);


        messages = new ArrayList<>();
        messages.add(new Message("07765796388", "How are you", "4:20 am"));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatMessageAdapter = new ChatMessageAdapter(messages, "0776579631");
        recyclerView.setAdapter(chatMessageAdapter);

    }

    private void setUPTopBarForDarkMode(){
        UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        int currentMode = uiModeManager.getNightMode();
        if (currentMode == UiModeManager.MODE_NIGHT_YES) {
            topBar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            backButton.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            searchIcon.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        }
    }

    private void initUI(){
        profilePic = findViewById(R.id.profilePic);
        activeChatTextView = findViewById(R.id.active_chat_username);
        backButton = findViewById(R.id.back_button);
        searchIcon = findViewById(R.id.search_icon);
        topBar = findViewById(R.id.top_bar);
        recyclerView = findViewById(R.id.messages_recycler_view);
    }
    private void setUpStatusBar() {
        Window window = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            int currentMode = uiModeManager.getNightMode();
            if (currentMode == UiModeManager.MODE_NIGHT_YES) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
            }else {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        }

    }

}