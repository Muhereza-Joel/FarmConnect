package com.moels.farmconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.moels.farmconnect.R;

public class ChatActivity extends AppCompatActivity {
    ImageView profilePic;
    TextView activeChatTextView;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUI();

        String activeChatUsername = getIntent().getStringExtra("activeChatUsername");
        String profilePictureUrl = getIntent().getStringExtra("profilePicture");
        Glide.with(this).load(profilePictureUrl).circleCrop().into(profilePic);
        activeChatTextView.setText(activeChatUsername);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initUI(){
        profilePic = findViewById(R.id.profilePic);
        activeChatTextView = findViewById(R.id.active_chat_username);
        backButton = findViewById(R.id.back_button);
    }


}