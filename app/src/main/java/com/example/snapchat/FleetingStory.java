package com.example.snapchat;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.snapchat.fragment.StoryFragment;

import java.util.ArrayList;

public class FleetingStory extends AppCompatActivity {

    private static int TIME_OUT = 15000;
    AnimationDrawable animationDrawable;
    FrameLayout frameLayout;
    ArrayList<String> arrayList = null;
    MediaPlayer mediaPlayer;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleeting_story);
        frameLayout = (FrameLayout) findViewById(R.id.myFrameLay);
        animationDrawable = (AnimationDrawable) frameLayout.getBackground();
        Bundle p = getIntent().getExtras();

        if(p!=null)
            arrayList =p.getStringArrayList("fleet");
        TextView song=(TextView)findViewById(R.id.SongText);
        song.setText(arrayList.get(0).substring(0,arrayList.get(0).length()-4));
        TextView artist=(TextView)findViewById(R.id.ArtistText);
        artist.setText(arrayList.get(1));
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
        try {
            if(mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer=null;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(arrayList.get(2));
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }catch (Exception e){}
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!flag) {
                    Bundle bundle = new Bundle();
                    Intent intent= new Intent(FleetingStory.this, MainActivity.class);
                   final ArrayList<String> al = new ArrayList<>();
                    al.add(arrayList.get(0));//Song Name
                    al.add(arrayList.get(1));//Artist Name
                    al.add(arrayList.get(2));//Trimmed Song
                    bundle.putStringArrayList("info", al);
                    intent.putExtras(bundle);
                    startActivity(intent);//}
                }
            }
        }, TIME_OUT);
    }

    public void onBackPressed(){
        if(mediaPlayer!=null){
            flag=true;
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        super.onBackPressed();
    }

}
