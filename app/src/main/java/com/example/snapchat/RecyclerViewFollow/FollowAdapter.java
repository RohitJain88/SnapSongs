package com.example.snapchat.RecyclerViewFollow;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.snapchat.*;
import com.example.snapchat.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FollowAdapter extends RecyclerView.Adapter<FollowViewHolders> {

    private List<FollowObject> usersList;
    private Context context;
    private String TAG="FollowAdapter";
    private int TIME_OUT=2000;
    public String userId;
    public MediaPlayer mediaPlayer=null;
    private HashMap<String, String> mapToUpload;
    private HashMap<String, Integer> countMap = new HashMap<>();
    private HashMap<String, Integer> countFollowersMap;

    int count = 0;
    DatabaseReference userFollowersDb = null;
    DatabaseReference userCount = null;
    String followerName = "";
    DatabaseReference selfDb = null;
    int currentFollowercount;

    public FollowAdapter(List<FollowObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }
    @Override
    public FollowViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_followers_item, null);
        FollowViewHolders rcv = new FollowViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final FollowViewHolders holder, int position) {
        holder.mEmail.setText(usersList.get(position).getEmail());


        //Changing the button text according to logged-in user following's
        if(UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())){
            holder.mFollow.setImageResource(R.drawable.ic_check_box_black);
        }else{
            holder.mFollow.setImageResource(R.drawable.ic_person_add_black);
        }
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        selfDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        selfDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followerName = dataSnapshot.child("name").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d(TAG, "onClick: "+userId);

                        //FollowerAdapter.updateFollowers();
                        if (!UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())) {
                            holder.mFollow.setImageResource(R.drawable.ic_check_box_black);
                            Log.d(TAG, "I m following");
                            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).setValue(true);
                            FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("followers").child(userId).setValue(true);

                            if (followerName!="" ||followerName!=null ) {
                                mapToUpload = new HashMap<>();
                                mapToUpload.put("name", followerName);


                                userFollowersDb = FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("countFollowers");
                                if (userFollowersDb!=null){

                                    userFollowersDb.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            try {
                                                if (dataSnapshot.getValue() != null) {
                                                    try {
                                                        Log.d("TAG", "hihhugJJJJ" + dataSnapshot.getValue()); // your name values you will get here
                                                        currentFollowercount = (int)(dataSnapshot.child("count").getValue());
                                                        //System.out.println(dataSnapshot.getValue());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    Log.e("TAG", " it's null.");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("countFollowers").removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("countFollowers").setValue(true);
                                    userFollowersDb = FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("countFollowers");
                                    userFollowersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            countFollowersMap = new HashMap<>();
                                            countFollowersMap.put("count",currentFollowercount+1);
                                            userFollowersDb.setValue(countFollowersMap);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }else{

                                    FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("countFollowers").setValue(true);
                                    userFollowersDb = FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("countFollowers");
                                    userFollowersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            countFollowersMap = new HashMap<>();
                                            countFollowersMap.put("count",1);
                                            userFollowersDb.setValue(countFollowersMap);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }


                                count+=1;
                                countMap.put("count", count);
                                userCount = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("countFollowing");
                                if (userCount!=null){
                                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("countFollowing").removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("countFollowing").setValue(true);
                                    userCount = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("countFollowing");
                                    userCount.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            userCount.setValue(countMap);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }else{
                                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("countFollowing").setValue(true);
                                    userCount = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("countFollowing");
                                    userCount.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            userCount.setValue(countMap);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }

                        } else {
                            holder.mFollow.setImageResource(R.drawable.ic_person_add_black);
                            Log.d(TAG, "Follow");
                            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("followers").child(userId).removeValue();


                            userFollowersDb.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    try {
                                        if (dataSnapshot.child("count").getValue() != null) {
                                            try {
                                                Log.e("TAG", "" + dataSnapshot.child("count").getValue()); // your name values you will get here
                                                currentFollowercount = (int)(dataSnapshot.getValue());

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Log.e("TAG", " it's null.");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("countFollowers").removeValue();
                            FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("countFollowers").setValue(true);
                            userFollowersDb = FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("countFollowers");
                            userFollowersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    countFollowersMap = new HashMap<>();
                                    countFollowersMap.put("count",currentFollowercount-1);
                                    userFollowersDb.setValue(countFollowersMap);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            count-=1;
                            countMap.put("count", count);
                            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("countFollowing").removeValue();
                            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("countFollowing").setValue(true);
                            userCount = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("countFollowing");
                            userCount.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    userCount.setValue(countMap);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                    }
                },TIME_OUT);

            }

        });

    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}
