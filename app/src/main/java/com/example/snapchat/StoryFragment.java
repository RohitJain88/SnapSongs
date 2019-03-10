package com.example.snapchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.snapchat.RecyclerViewStory.StoryAdapter;
import com.example.snapchat.RecyclerViewStory.StoryObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StoryFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "StoryFragmentt";
    Button mRefresh;

    public static StoryFragment newInstance(){
        StoryFragment fragment = new StoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StoryAdapter(getDataSet(), getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRefresh = view.findViewById(R.id.Refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                listenForData();
            }
        });

        return view;
    }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        mAdapter.notifyItemRangeChanged(0,size);
    }


    //Will return all the users in the recycler view
    private ArrayList<StoryObject> results = new ArrayList<>();
    //In the beginning of the search load up every single data
    private ArrayList<StoryObject> getDataSet() {
        listenForData();
        return results;
    }

    private void listenForData() {
        for (int i =0; i < UserInformation.listFollowing.size(); i++){
            DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));

            followingStoryDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Getting Information from database and checking if the timestamp lies in between the 24 hours timestamp for the story
                    Log.d(TAG, "listenForData: "+dataSnapshot);
                    String email = dataSnapshot.child("email").getValue().toString();
                    String uid = dataSnapshot.getRef().getKey();
                    long timestampBeg = 0;
                    long timestampEnd = 0;
                    for (DataSnapshot storySnapchot : dataSnapshot.child("story").getChildren()){
                        if(storySnapchot.child("timestampBeg").getValue() != null){
                            timestampBeg = Long.parseLong(storySnapchot.child("timestampBeg").getValue().toString());
                        }
                        if(storySnapchot.child("timestampEnd").getValue() != null){
                            timestampEnd = Long.parseLong(storySnapchot.child("timestampEnd").getValue().toString());
                        }
                        long timestampCurrent = System.currentTimeMillis();
                        if(timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd){
                            StoryObject obj = new StoryObject(email, uid);
                            Log.d(TAG, "onDataChange: "+obj);
                            //If more than one story is present for the story
                            if(!results.contains(obj)){
                                Log.d(TAG, "onDataAdd: "+obj);
                                results.add(obj);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

}