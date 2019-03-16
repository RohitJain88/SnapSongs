package com.example.snapchat.RecyclerViewFollow;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.snapchat.*;
import com.example.snapchat.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FollowAdapter extends RecyclerView.Adapter<FollowViewHolders> {

    private List<FollowObject> usersList;
    private Context context;
    private String TAG="FollowAdapter";
    private int TIME_OUT=2000;
    public String userId;
    public MediaPlayer mediaPlayer=null;
    private HashMap<String, String> mapToUpload;
    DatabaseReference userFollowersDb = null;
    String followerName = "";
    DatabaseReference selfDb = null;

    public FollowAdapter(List<FollowObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }
    @Override
    public FollowViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_followers_item, null);
        FollowViewHolders rcv = new FollowViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final FollowViewHolders holder, int position) {
        holder.mEmail.setText(usersList.get(position).getEmail());


        //Changing the button text according to logged-in user following's
        if(UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())){
            holder.mFollow.setImageResource(R.drawable.ic_check_box_black);
        }else{
            holder.mFollow.setImageResource(R.drawable.ic_person_add_black);
        }
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

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d(TAG, "onClick: "+userId);

                        //FollowerAdapter.updateFollowers();
                        if (!UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())) {
                            holder.mFollow.setImageResource(R.drawable.ic_check_box_black);
                            Log.d(TAG, "I m following");
                            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).setValue(true);
                            FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("followers").child(userId).setValue(true);

                            if (followerName!="" ||followerName!=null ) {
                                mapToUpload = new HashMap<>();
                                mapToUpload.put("name", followerName);
                                userFollowersDb = FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("followers").child(userId);
                                userFollowersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        userFollowersDb.setValue(mapToUpload);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        } else {
                            holder.mFollow.setImageResource(R.drawable.ic_person_add_black);
                            Log.d(TAG, "Follow");
                            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("followers").child(userId).removeValue();

                        }

                    }
                },TIME_OUT);

            }

        });

    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}
