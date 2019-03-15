package com.example.snapchat;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snapchat.fragment.StoryFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplaySongActivity extends AppCompatActivity {

    String userId;

    private ImageView mImage;

    //flag to check whether we have found the first image
    private boolean started = false;
    MediaPlayer mediaPlayer;
    boolean flag=false;
    private static int TIME_OUT = 15000;
    AnimationDrawable animationDrawable;
    FrameLayout frameLayout;
    Bundle b=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleeting_story);

        //Get the Bundle Information passed from StoryHolder
        b = getIntent().getExtras();
        userId = b.getString("userId");
        mImage = findViewById(R.id.image);

        if(b!=null)
        {
            listenForData();
        }

    }
    ArrayList<String> songUrlList = new ArrayList<>();
        //Getting stories of the desired user
    private void listenForData() {
            DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            followingStoryDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (b != null) {
                        //Getting Information from database and checking if the timestamp lies in between the 24 hours timestamp for the story
                        String songUrl = "";
                        String songArtist = "";
                        String songName = "";
                        long timestampBeg = 0;
                        long timestampEnd = 0;
                        onRestart();
                        for (DataSnapshot storySnapchot : dataSnapshot.child("story").getChildren()) {
                            if (storySnapchot.child("timestampBeg").getValue() != null) {
                                timestampBeg = Long.parseLong(storySnapchot.child("timestampBeg").getValue().toString());
                            }
                            if (storySnapchot.child("timestampEnd").getValue() != null) {
                                timestampEnd = Long.parseLong(storySnapchot.child("timestampEnd").getValue().toString());
                            }
                            if (storySnapchot.child("songUrl").getValue() != null && storySnapchot.child("songArtist").getValue() != null && storySnapchot.child("songName").getValue() != null) {
                                songName = storySnapchot.child("songName").getValue().toString();
                                songArtist = storySnapchot.child("songArtist").getValue().toString();
                                songUrl = storySnapchot.child("songUrl").getValue().toString();
                            }
                            long timestampCurrent = System.currentTimeMillis();
                            if (timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd) {
                                songUrlList.add(songName);
                                songUrlList.add(songArtist);
                                songUrlList.add(songUrl);
                                songUrlList.add("fragment");
                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList("fleet", songUrlList);
                                frameLayout = (FrameLayout) findViewById(R.id.myFrameLay);
                                animationDrawable = (AnimationDrawable) frameLayout.getBackground();
                                TextView song = (TextView) findViewById(R.id.SongText);
                                song.setText(songName.substring(0, songName.length() - 4));
                                TextView artist = (TextView) findViewById(R.id.ArtistText);
                                artist.setText(songArtist);
                                animationDrawable.setEnterFadeDuration(5000);
                                animationDrawable.setExitFadeDuration(2000);
                                animationDrawable.start();
                                try {
                                    if (mediaPlayer != null) {
                                        mediaPlayer.stop();
                                        mediaPlayer.reset();
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                    }

                                    mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setDataSource(songUrl);
                                    mediaPlayer.prepare();
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.start();
                                            b = null;
                                        }
                                    });
                                } catch (Exception e) {
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!flag) {
                                            Intent i = new Intent(DisplaySongActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                }, TIME_OUT);

                            }
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
    public void onBackPressed(){
        if(mediaPlayer!=null){
            flag=true;
            mediaPlayer.stop();

        }
        super.onBackPressed();
    }
}
