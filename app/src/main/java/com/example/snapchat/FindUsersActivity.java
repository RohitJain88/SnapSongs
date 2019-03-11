package com.example.snapchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.snapchat.RecyclerViewFollow.FollowAdapter;
import com.example.snapchat.RecyclerViewFollow.FollowObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class FindUsersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    EditText mInput;
    private FirebaseAuth auth;
    private static final String TAG = "FindUserActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        mInput = findViewById(R.id.input);
        Button mSearch = findViewById(R.id.search);
        auth = FirebaseAuth.getInstance();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FollowAdapter(getDataSet(), getApplication());
        mRecyclerView.setAdapter(mAdapter);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                listenForData();
            }
        });
    }


    //This method is used to search emails based on the entered text
    private void listenForData() {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = userDb.orderByChild("email").startAt(mInput.getText().toString()).endAt(mInput.getText().toString() + "\uf8ff");
        Log.d(TAG, "listenForData: "+mInput.getText());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String email ="";
                //Gives uid of each users returned to us
                String uid = dataSnapshot.getRef().getKey();
                System.out.println("Uid is:"+uid);
                Log.d(TAG, "onChildAdded Uid: "+uid);
                // A precautionary check as query will always return childs with email
                if(dataSnapshot.child("email").getValue() !=null){
                    email = dataSnapshot.child("email").getValue().toString();
                    Log.d(TAG, "onChildAdded email: "+email);
                }

                Log.d(TAG, "onChildAdded id: "+auth.getUid());
                if(!email.equals(auth.getCurrentUser().getEmail())){
                    FollowObject obj = new FollowObject(email,uid);
                    results.add(obj);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //This method clears the search result on each search done
    private void clear() {
        int size = this.results.size();
        this.results.clear();
        mAdapter.notifyItemRangeChanged(0,size);
    }


    //Will return all the users in the recycler view
    private ArrayList<FollowObject> results = new ArrayList<>();
    //In the beginning of the search load up every single data
    private ArrayList<FollowObject> getDataSet() {
        listenForData();
        return results;
    }

}
