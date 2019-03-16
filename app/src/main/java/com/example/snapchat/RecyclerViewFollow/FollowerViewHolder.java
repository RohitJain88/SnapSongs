package com.example.snapchat.RecyclerViewFollow;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snapchat.R;

public class FollowerViewHolder extends RecyclerView.ViewHolder {
    public TextView mFollowers;
    public TextView mUsername;
    public ImageView mFollowing;

    public FollowerViewHolder(View itemView) {
        super(itemView);
        mUsername = itemView.findViewById(R.id.followingUser);
        //mFollowers = itemView.findViewById(R.id.followingUser);
        mFollowing = itemView.findViewById(R.id.followingActivity);
    }
}
