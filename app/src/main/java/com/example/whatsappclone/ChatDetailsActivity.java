package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.example.whatsappclone.Adapters.MessageAdapter;
import com.example.whatsappclone.Models.MessageModels;
import com.example.whatsappclone.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatDetailsActivity extends AppCompatActivity {

    ActivityChatDetailsBinding binding ;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        final String uId = auth.getUid();
        final String receiveId = getIntent().getStringExtra("userId");
        String username = getIntent().getStringExtra("username");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.chatUsername.setText(username);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.chatProfile);


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatDetailsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        final String senderRoom = uId + receiveId;
        final String receiverRoom = receiveId+uId;

        final ArrayList<MessageModels> messageModels = new ArrayList<>();

        final MessageAdapter msgAdapter = new MessageAdapter(messageModels, this, receiveId);

        binding.recyclerView.setAdapter(msgAdapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));


        database.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for(DataSnapshot ss1: snapshot.getChildren()){
                            MessageModels models = ss1.getValue(MessageModels.class);
                            models.setMessageId(ss1.getKey());

                            if(models.getMessage().toString().equals("")){
                                Toast.makeText(ChatDetailsActivity.this, "write a message first..", Toast.LENGTH_SHORT).show();
                            }else {
                                messageModels.add(models);
                            }

                        }
                        msgAdapter.notifyDataSetChanged();

                        try{
                            binding.recyclerView.smoothScrollToPosition(binding.recyclerView.getAdapter().getItemCount()-1);
                        }catch (Exception e){

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        binding.chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.chatMsg.getText().toString();
                if(msg.equals("")){
                    return;
                }
                final MessageModels model = new MessageModels(uId, msg);
                model.setTimeStamp(new Date().getTime());

                model.setMsgTime(new SimpleDateFormat("hh:mm aa").format(new Date()));

                binding.chatMsg.setText("");

                database.getReference().child("Chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("Chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ChatDetailsActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}