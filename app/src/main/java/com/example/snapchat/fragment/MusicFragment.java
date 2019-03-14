package com.example.snapchat.fragment;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snapchat.R;
import com.example.snapchat.ShowMusicActivity;
import com.example.snapchat.loginRegistration.SplashScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class MusicFragment extends Fragment {
    private static final String TAG = "MusicFragment";
    private String userName;

    public static MusicFragment newInstance(){
        MusicFragment fragment = new MusicFragment();
        return fragment;
    }

    // ListView listView;
    List<String> list;
    String str;
   // ListAdapter listAdapter;
    MediaPlayer mediaPlayer;

    Button back;
    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music,container,false);
        //Bundle p = getActivity().getIntent().getExtras();
        //ShowMusicActivity activity=(ShowMusicActivity)getActivity();
      //  Bundle bundle=this.getArguments();
   //   final ArrayList<String> arrayList;
      //if(bundle!=null)
    //      arrayList=bundle.getStringArrayList("info");
//       // if (getArguments() != null) {
 //          arrayList = this.getArguments().getStringArrayList("info");
//      //  }
//         System.out.println(arrayList);
//        //final ArrayList<String> arrayList =p.getStringArrayList("info");
//        ///System.out.println(arrayList);
      //  EditText v = view.findViewById(R.id.song1);
     //   v.setText(Html.fromHtml(v.getText().toString()));
    //    v.setMovementMethod(LinkMovementMethod.getInstance());
        ImageView mPost = view.findViewById(R.id.post);
        ImageView mlogout = view.findViewById(R.id.logout);
        final TextView mtext = view.findViewById(R.id.userName);
        //Button mPost = view.findViewById(R.id.post);
//        Button mCapture= view.findViewById(R.id.capture);


     //   EditText artist=view.findViewById(R.id.artist1);

      //  v.setText(arrayList.get(0));
     //   artist.setText(arrayList.get(1));
        CheckPermission();
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer();
            }
        });
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
        try{
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference("users").child(userId).child("name");

            userDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Log.d(TAG, "Name: " + dataSnapshot.getValue().toString());
                    userName = dataSnapshot.getValue().toString();
                    mtext.setText(userName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch(Exception e){
            Toast.makeText(getActivity().getApplicationContext(), "User Not logged In", Toast.LENGTH_LONG).show();
        }

        //Button mFindUsers = view.findViewById(R.id.findUsers);
        //mFindUsers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FindUsers();
//            }
//        });
        return view;
    }

//    private void FindUsers() {
//        Intent intent = new Intent(getContext(), FindUsersActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        return;
//    }

    private void CheckPermission(){

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            return;
        }
    }

    private void MusicPlayer(){
        Intent intent = new Intent(getContext() ,ShowMusicActivity.class);

        //clear all other activities which were on top of stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return;
    }


    //sig
    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(),SplashScreenActivity.class);

        //clear all other activities which were on top of stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return;

    }
}