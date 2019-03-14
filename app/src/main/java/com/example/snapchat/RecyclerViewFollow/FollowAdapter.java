package com.example.snapchat.RecyclerViewFollow;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.snapchat.*;
import com.example.snapchat.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowViewHolders> {

    private List<FollowObject> usersList;
    private Context context;
    private String TAG="FollowAdapter";
    private int TIME_OUT=2000;
    public String userId;
    public MediaPlayer mediaPlayer=null;

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

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d(TAG, "onClick: "+userId);

                        if (!UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())) {
                            holder.mFollow.setImageResource(R.drawable.ic_check_box_black);
                            Log.d(TAG, "I m following");
                            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).setValue(true);
                            if(mediaPlayer!=null){
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                mediaPlayer.reset();
                                mediaPlayer=null;
                            }
                        } else {
                            holder.mFollow.setImageResource(R.drawable.ic_person_add_black);
                            Log.d(TAG, "Follow");
                            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).removeValue();
                            if(mediaPlayer!=null){
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                mediaPlayer.reset();
                                mediaPlayer=null;
                            }
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
