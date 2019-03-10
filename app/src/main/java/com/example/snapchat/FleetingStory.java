package com.example.snapchat;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;

public class FleetingStory extends AppCompatActivity {

    private static int TIME_OUT = 15000;
    AnimationDrawable animationDrawable;
    FrameLayout frameLayout;
    ArrayList<String> arrayList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleeting_story);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(FleetingStory.this, MainActivity.class);
                Bundle bundle=new Bundle();
                //Intent intent= new Intent(FleetingStory.this, MainActivity.class);
                ArrayList<String> al = new ArrayList<>();
                al.add(arrayList.get(0));
                al.add(arrayList.get(1));
                al.add(arrayList.get(2));
                bundle.putStringArrayList("info",al);
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        }, TIME_OUT);
    }


}
