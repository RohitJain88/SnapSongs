package com.example.snapchat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserInformation {
    public static ArrayList<String> listFollowing = new ArrayList<>();
    public static ArrayList<String> listFollowers = new ArrayList<>();

    public void startFetching(){
        listFollowing.clear();
        getUserFollowing();
        getUserFollowers();

    }

    private void getUserFollowers() {
        final DatabaseReference userFollowingDB = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("followers");
        userFollowingDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null && !listFollowers.contains(uid)){
                        listFollowers.add(uid);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null){
                        listFollowers.remove(uid);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserFollowing() {
        DatabaseReference userFollowingDB = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("following");
        userFollowingDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null && !listFollowing.contains(uid)){
                        listFollowing.add(uid);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null){
                        listFollowing.remove(uid);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
