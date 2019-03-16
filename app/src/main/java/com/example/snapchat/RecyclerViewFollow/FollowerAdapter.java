package com.example.snapchat.RecyclerViewFollow;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.snapchat.R;
import com.example.snapchat.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.List;

public class FollowerAdapter extends  RecyclerView.Adapter<FollowerViewHolder>{

    private List<FollowerObject> usersList;
    private Context context;
    private String TAG="FollowAdapter";
    private int TIME_OUT=2000;
    public String userId;
    public MediaPlayer mediaPlayer=null;
    private HashMap<String, String> mapToUpload;
    DatabaseReference userFollowersDb = null;
    String followerName = "";
    DatabaseReference selfDb = null;

    public FollowerAdapter(List<FollowerObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_view_followers, null);
        FollowerViewHolder rcv1 = new FollowerViewHolder(layoutView);
        return rcv1;
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowerViewHolder holder, int position) {
        holder.mUsername.setText(usersList.get(position).getuserName());
        holder.mFollowing.setImageResource(R.drawable.ic_check_box_black);



        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        selfDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        selfDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followerName = dataSnapshot.child("name").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        holder.mFollow.setOnClickListener(new View.OnClickListener() {
//
//        });
    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}
