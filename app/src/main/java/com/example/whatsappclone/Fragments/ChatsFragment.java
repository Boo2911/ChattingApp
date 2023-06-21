package com.example.whatsappclone.Fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatsappclone.Adapters.UserAdapter;
import com.example.whatsappclone.Models.Users;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatsFragment extends Fragment {

 FragmentChatsBinding binding;
    public ChatsFragment() {
        // Required empty public constructor
    }
    ArrayList<Users> lists = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;

    @Override
    public void onResume() {

        firebaseDatabase = FirebaseDatabase.getInstance();

        UserAdapter userAdapter = new UserAdapter(lists, getContext());
        userAdapter.notifyDataSetChanged();
        binding.recyclerView.setAdapter(userAdapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseDatabase.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lists.clear();
                for(DataSnapshot dss: snapshot.getChildren()){
                    Users user = dss.getValue(Users.class);
                    user.setUserId(dss.getKey());
                    user.setLastMessageTime(new SimpleDateFormat("hh:mm aa").format(new Date()));
                    if(!user.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        lists.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Not available", Toast.LENGTH_SHORT).show();
            }
        });

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentChatsBinding.inflate(inflater, container, false);



       firebaseDatabase = FirebaseDatabase.getInstance();

        UserAdapter userAdapter = new UserAdapter(lists, getContext());
        userAdapter.notifyDataSetChanged();
        binding.recyclerView.setAdapter(userAdapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseDatabase.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lists.clear();
                for(DataSnapshot dss: snapshot.getChildren()){
                    Users user = dss.getValue(Users.class);
                    user.setUserId(dss.getKey());
                    user.setLastMessageTime(new SimpleDateFormat("hh:mm aa").format(new Date()));
                    if(!user.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        lists.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Not available", Toast.LENGTH_SHORT).show();
            }
        });


        return binding.getRoot();
    }



}