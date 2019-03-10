package com.example.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayImageActivity extends AppCompatActivity {

    String userId;

    private ImageView mImage;

    //flag to check whether we have found the first image
    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_display_image);

        //Get the Bundle Information passed from StoryHolder
        Bundle b = getIntent().getExtras();
        userId = b.getString("userId");

        mImage = findViewById(R.id.image);

        listenForData();
    }
    ArrayList<String> songUrlList = new ArrayList<>();
        //Getting stories of the desired user
    private void listenForData() {

            DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            followingStoryDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Getting Information from database and checking if the timestamp lies in between the 24 hours timestamp for the story
                    String songUrl ="";
                    String songArtist="";
                    String songName="";
                    long timestampBeg = 0;
                    long timestampEnd = 0;
                    for (DataSnapshot storySnapchot : dataSnapshot.child("story").getChildren()){
                        if(storySnapchot.child("timestampBeg").getValue() != null){
                            timestampBeg = Long.parseLong(storySnapchot.child("timestampBeg").getValue().toString());
                        }
                        if(storySnapchot.child("timestampEnd").getValue() != null){
                            timestampEnd = Long.parseLong(storySnapchot.child("timestampEnd").getValue().toString());
                        }
                        if(storySnapchot.child("songUrl").getValue() != null && storySnapchot.child("songArtist").getValue() != null && storySnapchot.child("songName").getValue() != null) {
                            songName = storySnapchot.child("songName").getValue().toString();
                            songArtist = storySnapchot.child("songArtist").getValue().toString();
                            songUrl = storySnapchot.child("songUrl").getValue().toString();
                        }
                        long timestampCurrent = System.currentTimeMillis();
                        if(timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd){
                            songUrlList.add(songName);
                            songUrlList.add(songArtist);
                            songUrlList.add(songUrl);
                            songUrlList.add("fragment");
                            Bundle bundle =new Bundle();
                            bundle.putStringArrayList("fleet",songUrlList);

                            showStory(bundle);
//                            if(!started){
//                                started = true;
//                                initializeDisplay();
//                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
    }

    private void showStory(Bundle b1) {
        Intent intent = new Intent(getApplication(),FleetingStory.class);
        intent.putExtras(b1);
        startActivity(intent);
    }

//    private int imageIterator=0;
//    private void initializeDisplay() {
//        //glide: library to get images efficiently, w/o hussle by giving image url
//        Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).into(mImage);
//
//        mImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeImage();
//            }
//        });
//        final Handler handler = new Handler();
//        //Defining the delay between the image view for 5 secs
//        final int delay = 5000;
//
//        //Use to view multiple images in the story untill all images ran out and activity kills itself
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//               changeImage();
//               handler.postDelayed(this, delay);
//            }
//        },delay);
//    }
//
//
//    private void changeImage() {
//        //Check for any remaining image and return to prev activity if not found
//        if(imageIterator == imageUrlList.size() - 1)//as imageIterator starts with 0 and not 1
//        {
//            finish();
//            return;
//        }
//        imageIterator++;
//        Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).into(mImage);
//    }
//
//    //Timer to change the image for the specified time


}
