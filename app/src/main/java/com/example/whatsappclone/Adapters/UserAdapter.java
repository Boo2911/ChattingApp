package com.example.whatsappclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.ChatDetailsActivity;
import com.example.whatsappclone.Models.Users;
import com.example.whatsappclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder>{

    ArrayList<Users> lists;
    Context context;

    public UserAdapter(ArrayList<Users> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_sample_user, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Users models = lists.get(position);
        Picasso.get().load(models.getProfilePic()).placeholder(R.drawable.avatar).into(holder.profilePic);
        holder.userName.setText(models.getUsername());

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(FirebaseAuth.getInstance().getUid()+models.getUserId())
                        .orderByChild("timeStamp")
                                .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.hasChildren()){
                                                    for(DataSnapshot sshot: snapshot.getChildren()){
                                                        holder.lastMsg.setText(sshot.child("message").getValue().toString());
                                                        Date date = new Date(sshot.child("timeStamp").getValue(Long.class));
                                                        SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                                        holder.lastMsgTime.setText(formatDate.format(date));
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatDetailsActivity.class);
                i.putExtra("userId", models.getUserId());
                i.putExtra("username", models.getUsername());
                i.putExtra("profilePic", models.getProfilePic());
                context.startActivity(i);

               }
        });



    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView userName, lastMsg, lastMsgTime;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.chat_profile);
            userName = itemView.findViewById(R.id.userName);
            lastMsg = itemView.findViewById(R.id.userLastMessage);
            lastMsgTime = itemView.findViewById(R.id.userLastMessageTime);
        }
    }
}
