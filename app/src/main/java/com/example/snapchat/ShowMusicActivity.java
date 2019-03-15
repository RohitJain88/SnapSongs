package com.example.snapchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.snapchat.view.SnapTabsView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShowMusicActivity extends AppCompatActivity {

    private ArrayList<SongInfo> _songs=new ArrayList<>();
    RecyclerView recyclerView;
    SongAdapter songAdapter;
    MediaPlayer mediaPlayer;
    ImageView prev;
    Handler handler=new Handler();
    FFmpeg ffmpeg;
    String s="";
    String songName="",artistName="";
    StorageReference songRef;
    String Uid;
    private static int TIME_OUT=2000;
    String TAG ="ShowMusicActivity";
    Map<String, Object> mapToUpload;
    int flag=1,flag1=1;
    public ImageView Action;
    public ImageView Share;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_music);
        ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
        recyclerView= (RecyclerView)findViewById(R.id.recyclerView);
        mediaPlayer =new MediaPlayer();

        songAdapter=new SongAdapter(this, _songs);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration =  new DividerItemDecoration(recyclerView.getContext(),linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songAdapter);
         Action=(ImageView)findViewById(R.id.btnAction);
        Share=(ImageView)findViewById(R.id.btnShare);


        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final ImageView b, View v, final SongInfo obj, int position) {
                Log.d(TAG, "onItemClick: "+b.getId());
                Log.d(TAG, "onItemClick: "+R.id.btnAction);


                Runnable r=new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(b.getId()==R.id.btnAction && flag==0 && prev.equals(b)){
                                flag=1;
                                b.setImageResource(R.drawable.play);
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                mediaPlayer=null;
                            }else if(b.getId()==R.id.btnShare){
                                Intent intent= new Intent(ShowMusicActivity.this, MainActivity.class);
                                Bundle bundle=new Bundle();
                                if(mediaPlayer!= null && mediaPlayer.isPlaying()){
                                    mediaPlayer.stop();
                                    mediaPlayer.reset();
                                    mediaPlayer.release();
                                    mediaPlayer=null;
                                    flag=1;
                                    prev.setImageResource(R.drawable.play);
                                }
                               MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                               metaRetriever.setDataSource(obj.songUrl);
                               String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                               long dur = Long.parseLong(duration);
                               String seconds = String.valueOf((dur % 60000) / 1000);
                               int sec=Integer.parseInt(seconds);
                               ArrayList<String> al = new ArrayList<>();
                               String minutes = String.valueOf(dur / 60000);
                                int min=Integer.parseInt(minutes);
                                   al.add(obj.songName);
                                   al.add(obj.artistName);

                                   s = obj.songUrl.substring(0, obj.songUrl.length() - 4) + "-1.mp3";
                                    songName=obj.songName;
                                    artistName=obj.artistName;
                                   al.add(s);

                                   if(min==0 && sec<=30){
                                       String[] command = {"-y", "-i", obj.songUrl, "-ss", "00:00", "-to",minutes+":"+seconds, "-c", "copy", s};
                                       execute(command);
                                   }else{
                                String[] command = {"-y", "-i", obj.songUrl, "-ss", "00:15", "-to", "00:30", "-c", "copy", s};
                                   execute(command);}
                                bundle.putStringArrayList("info",al);
                                intent.putExtras(bundle);
                               // MusicFragment f = new MusicFragment();
                              //  f.setArguments(bundle);
                                startActivity(intent);
                            }
                            else if(mediaPlayer==null || (b.getId()==R.id.btnAction && flag==1 && !mediaPlayer.isPlaying())) {
                                Log.d(TAG, "iii: "+R.id.btnAction);
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(obj.songUrl);
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                    }
                                });
                                flag=0;
                                flag1=0;
                                prev=b;
                                b.setImageResource(R.drawable.stop);
                                prev.setTag(R.drawable.stop);
                            }else if(mediaPlayer!=null && b.getId()==R.id.btnAction && mediaPlayer.isPlaying()){
                                prev.setImageResource(R.drawable.play);
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                mediaPlayer=null;
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(obj.getSongUrl());
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                    }
                                });
                                //prev=b;
                                flag=0;
                                prev=b;
                                //b.setText("Stop");
                                b.setImageResource(R.drawable.stop);
                            }

                        }catch (IOException e){}
                    }
                };
                handler.postDelayed(r, 100);

            }
        });


        CheckPermission();
        CheckPermission1();

        Thread t= new MyThread();
        t.start();


    }

    private void saveToStories(final String songName, final String artistName, String s) {
        Uid = FirebaseAuth.getInstance().getUid();
        final DatabaseReference userStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("story");
        final DatabaseReference userStoryDb1 = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
        final String key = userStoryDb.push().getKey();
        Uri file = Uri.fromFile(new File(s));
        songRef = FirebaseStorage.getInstance().getReference().child("captureSongs/"+file.getLastPathSegment());
        UploadTask uploadTask = songRef.putFile(file);


// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri songUrl = uri;

                        Long currentTimestamp = System.currentTimeMillis();
                        Long endTimestamp = currentTimestamp + (24*60*60*1000);


                        cal.setTimeInMillis(currentTimestamp);
                        String starttime = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                        cal.setTimeInMillis(endTimestamp);
                        String endtime = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();

                        mapToUpload = new HashMap<>();
                        mapToUpload.put("songUrl",songUrl.toString());
                        mapToUpload.put("songName",songName);
                        mapToUpload.put("songArtist",artistName.toString());
                        mapToUpload.put("timestampBeg",currentTimestamp);
                        mapToUpload.put("timestampEnd",endTimestamp);
                        mapToUpload.put("timeBeg",starttime);
                        mapToUpload.put("timeEnd",endtime);
                        userStoryDb1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("story")){
                                    Log.d(TAG, "onDataChange: Coming");
                                    userStoryDb.removeValue();
                                    userStoryDb.child(key).setValue(mapToUpload);
                                }
                                else {
                                    Log.d(TAG, "onDataChange: No stories");
                                    userStoryDb.child(key).setValue(mapToUpload);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                });
            }
        });



    }

    private void pathtToSong(String path, StorageReference sRef, final String songName, final String artistName ) {

    }


    public void execute(String[] cmd){
        ffmpeg = FFmpeg.getInstance(ShowMusicActivity.this);
        try {
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    //  Toast.makeText(MainActivity.this,cmd, Toast.LENGTH_LONG);
                }

                @Override
                public void onProgress(String message) {
                    // Toast.makeText(MainActivity.this,message, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ShowMusicActivity.this,"Failed", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String message) {
                    saveToStories(songName,artistName,s);
                    Toast.makeText(ShowMusicActivity.this,"Trimmed", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

 public void onBackPressed(){
     if(mediaPlayer!=null){
        // flag=true;
         mediaPlayer.stop();
         mediaPlayer.reset();
         mediaPlayer.release();
         mediaPlayer=null;

     }
     super.onBackPressed();
     this.finish();
 }

    public class MyThread extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                if(mediaPlayer!=null){
                  //  seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void CheckPermission1(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            return;
        }
    }

    private void CheckPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            return;
        }
        loadSongs();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 123:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    loadSongs();
                }else{
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void loadSongs(){
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection= MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor= getContentResolver().query(uri,null, selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    SongInfo s=new SongInfo(name,artist,url);
                    _songs.add(s);
                }while(cursor.moveToNext());
            }
            cursor.close();
            songAdapter=new SongAdapter(this,_songs);
        }
    }
}
