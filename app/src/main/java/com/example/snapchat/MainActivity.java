package com.example.snapchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.snapchat.RecyclerViewStory.StoryObject;
import com.example.snapchat.fragment.ChatFragment;
import com.example.snapchat.fragment.MusicFragment;
import com.example.snapchat.fragment.StoryFragment;
import com.example.snapchat.view.SnapTabsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    ListView listView;
    List<String> list;
    String str;
    ListAdapter listAdapter;
    MediaPlayer mediaPlayer;
    Button back;
    ArrayList<String> arrayList = null;

    String Uid;
    private ArrayList<StoryObject> results = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    StorageReference songRef;
    private String songName;
    private String songArtist;
    private String songUrl;
    String TAG="MainActivity";
    int TIME_OUT=2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserInformation userInformationListener = new UserInformation();
        userInformationListener.startFetching();

        final View background = findViewById(R.id.background_view);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        SnapTabsView snapTabsView=findViewById(R.id.snap_tabs);
        snapTabsView.setUpWithViewPager(viewPager);
        Bundle p = getIntent().getExtras();
        Uid = FirebaseAuth.getInstance().getUid();
        final DatabaseReference userStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("story");
        userStoryDb.getRef().getKey();
        arrayList=new ArrayList<String>();

        arrayList=listenForData();
//        if(p!=null)
//         arrayList =p.getStringArrayList("info");

        final ImageView mCenterImage=(ImageView)findViewById(R.id.vst_center_image);
        viewPager.setCurrentItem(1);
        Log.d(TAG, "onCreate: "+arrayList);

        if(arrayList!=null){
                                              mCenterImage.setOnClickListener(new View.OnClickListener() {
                                                  @Override

                                                  public void onClick(View v) {
                                                      try {
                                                          if (viewPager.getCurrentItem() != 1)
                                                              viewPager.setCurrentItem(1);
                                                          else {
                                                              Bundle bundle = new Bundle();

                                                              Intent intent = new Intent(MainActivity.this, FleetingStory.class);
                                                              ArrayList<String> al = new ArrayList<>();
                                                              al.add(arrayList.get(0));
                                                              al.add(arrayList.get(1));
                                                              al.add(arrayList.get(2));
                                                              //al.add("main");
                                                              bundle.putStringArrayList("fleet", al);
                                                              intent.putExtras(bundle);


                                                              startActivity(intent);
                                                          }
                                                      }
                                                      catch (Exception e) {
                                                          Toast.makeText(MainActivity.this, "Please Share a Song", Toast.LENGTH_LONG).show();
                                                      }

                                                  }
                                              });


        }
//        if(p!=null){
//        }


        final int colorBlue = ContextCompat.getColor(this, R.color.lightblue);
        final int colorPurple = ContextCompat.getColor(this, R.color.purple);

        //TabLayout tabLayout=(TabLayout) findViewById(R.id.am_tab_layout);
       // tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    background.setBackgroundColor(colorBlue);
                    background.setAlpha(1 - positionOffset);
                } else if (position == 1) {
                    background.setBackgroundColor(colorPurple);
                    background.setAlpha(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(1);
        CheckPermission();
    }


    private void CheckPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            return;
        }
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ChatFragment.newInstance();
                case 1:
                    return MusicFragment.newInstance();
                case 2:
                    return StoryFragment.newInstance();

            }
            return null;

/*public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return "Chat";
                case 1:
                    return "Search";
                case 2:
                    return "Stories";

            }
            return super.getPageTitle(position);*/
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private ArrayList<String> listenForData() {
        DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
        followingStoryDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Getting Information from database and checking if the timestamp lies in between the 24 hours timestamp for the story

                for (DataSnapshot storySnapchot : dataSnapshot.child("story").getChildren()){
                        if(storySnapchot.child("songUrl").getValue() != null && storySnapchot.child("songArtist").getValue() != null && storySnapchot.child("songName").getValue() != null) {
                            songName = storySnapchot.child("songName").getValue().toString();
                            songArtist = storySnapchot.child("songArtist").getValue().toString();
                            songUrl = storySnapchot.child("songUrl").getValue().toString();
                            Log.d(TAG, "songName: "+songName);
                            Log.d(TAG, "songArtist: "+songArtist);
                            Log.d(TAG, "songUrl: "+songUrl);
                            arrayList=new ArrayList<>();
                            arrayList.add(songName);
                            arrayList.add(songArtist);
                            arrayList.add(songUrl);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        return arrayList;
    }
}


