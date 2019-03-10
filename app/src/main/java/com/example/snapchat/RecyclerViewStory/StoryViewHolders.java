package com.example.snapchat.RecyclerViewStory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.snapchat.DisplaySongActivity;
import com.example.snapchat.*;

public class StoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mEmail;

    public StoryViewHolders(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);
        mEmail = itemView.findViewById(R.id.email);
    }

    //This method will be called when user clicks on one of list of users in Story fragment
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), DisplaySongActivity.class);
        //Use bundle to send id to DisplaySongActivity for whom we want to see the story
        Bundle b = new Bundle();
        b.putString("userId", mEmail.getTag().toString()); //here we get Emailtag from the Story Adapter
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
