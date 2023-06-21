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
import com.example.whatsappclone.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupChatActivity.this, MainActivity.class));
                finish();
            }
        });

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ArrayList<MessageModels> messageModels = new ArrayList<>();
        final String senderId = auth.getUid();
        final MessageAdapter adapter = new MessageAdapter(messageModels, this);


        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.chatUsername.setText("Family Group");

        database.getReference().child("Group Chat")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messageModels.clear();
                                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                    MessageModels models = snapshot1.getValue(MessageModels.class);
                                    models.setMessageId(snapshot1.getKey());
                                    if(models.getMessage().equals("")){
                                        Toast.makeText(GroupChatActivity.this, "write a message first..", Toast.LENGTH_SHORT).show();
                                    }else {
                                        messageModels.add(models);
                                    }

                                }
                                adapter.notifyDataSetChanged();
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
                final MessageModels model = new MessageModels(senderId, msg);
                model.setTimeStamp(new Date().getTime());
                model.setMsgTime(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date()));
                binding.chatMsg.setText("");

                database.getReference().child("Group Chat")
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
}