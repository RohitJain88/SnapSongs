package com.example.snapchat.RecyclerViewFollow;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snapchat.R;

public class FollowerViewHolder extends RecyclerView.ViewHolder {
    public ImageView mFollowers;
    public TextView mEmail;
    public ImageView mFollow;

    public FollowerViewHolder(View itemView) {
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mFollowers = itemView.findViewById(R.id.following);
        mFollow = itemView.findViewById(R.id.follow);
    }
}
