package com.example.whatsappclone.Adapters;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.Models.MessageModels;
import com.example.whatsappclone.Models.Users;
import com.example.whatsappclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter{

    ArrayList<MessageModels> allMsgs;
    Context context;

    String recId;

    public MessageAdapter(ArrayList<MessageModels> allMsgs, Context context, String recId) {
        this.allMsgs = allMsgs;
        this.context = context;
        this.recId = recId;
    }

    public MessageAdapter(ArrayList<MessageModels> allMsgs, Context context) {
        this.allMsgs = allMsgs;
        this.context = context;
    }

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    @Override
    public int getItemViewType(int position) {
        if(allMsgs.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else return RECEIVER_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        if(viewType==SENDER_VIEW_TYPE){
             view = LayoutInflater.from(context).inflate(R.layout.sender_bubble, parent, false);
             return new SenderViewHolder(view);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.receiver_bubble, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModels models = allMsgs.get(position);
        String newtime = models.getMsgTime();




            if(holder.getClass()==SenderViewHolder.class){
                ((SenderViewHolder)holder).senderMsg.setText(models.getMessage());
                ((SenderViewHolder)holder).senderTime.setText(newtime);
            }else {
                ((ReceiverViewHolder)holder).receiverMsg.setText(models.getMessage());
                ((ReceiverViewHolder)holder).receiverTime.setText(newtime);
                try{
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(recId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Users user = snapshot.getValue(Users.class);
                                    ((ReceiverViewHolder)holder).senderUserName.setText(user.getUsername());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }catch (Exception e){

                }

            }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try{
                                    String senderRoom = FirebaseAuth.getInstance().getUid()+recId;
                                    FirebaseDatabase.getInstance().getReference().child("Chats")
                                            .child(senderRoom)
                                            .child(models.getMessageId()).setValue(null);


                                }catch (Exception e){
                                    String senderRoom = FirebaseAuth.getInstance().getUid();

                                    Log.d(TAG,models.getMessage());
                                    FirebaseDatabase.getInstance().getReference().child("Group Chat")
                                            .child(models.getMessageId())
                                            .setValue(null);
                                }


                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return allMsgs.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime, senderUserName;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiver_msg);
            receiverTime = itemView.findViewById(R.id.receiver_time);
            senderUserName = itemView.findViewById(R.id.sender_username);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.sender_msg);
            senderTime = itemView.findViewById(R.id.sender_time);
        }
    }
}
