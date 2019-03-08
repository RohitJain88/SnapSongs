package com.example.snapchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Share extends AppCompatActivity {

    ListView listView;
    List<String> list;
    String str;
    ListAdapter listAdapter;
    MediaPlayer mediaPlayer;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Bundle p = getIntent().getExtras();
        final ArrayList<String> arrayList =p.getStringArrayList("info");
        //String command[]=p.getStringArray("cmd");
        //execute(command);
        listView=(ListView)findViewById(R.id.listView);
        list=new ArrayList<String>();
        list.add(arrayList.get(0));
        listAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if(mediaPlayer!=null){
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer=null;
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(arrayList.get(2));
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                }catch (Exception e){}
            }
        });

        EditText song=(EditText)findViewById(R.id.song);
        EditText artist=(EditText)findViewById(R.id.artist);
        EditText url=(EditText)findViewById(R.id.url);
        song.setText(arrayList.get(0));
        artist.setText(arrayList.get(1));
        url.setText(arrayList.get(2));
        Button back=(Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(Share.this,ShowMusicActivity.class);
                startActivity(intent);
            }
        });
        CheckPermission();
    }

    private void CheckPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            return;
        }
    }

}
