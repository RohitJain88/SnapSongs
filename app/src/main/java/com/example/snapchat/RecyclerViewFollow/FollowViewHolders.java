package com.example.snapchat.RecyclerViewFollow;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snapchat.*;

public class FollowViewHolders extends RecyclerView.ViewHolder {
    public ImageView mFollowers;
    public TextView mEmail;
    public ImageView mFollow;

    public FollowViewHolders(View itemView){
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mFollow = itemView.findViewById(R.id.follow);
        mFollowers = itemView.findViewById(R.id.following);
    }
}
