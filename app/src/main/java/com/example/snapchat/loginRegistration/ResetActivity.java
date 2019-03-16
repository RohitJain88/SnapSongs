package com.example.snapchat.loginRegistration;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.snapchat.R;
import com.example.snapchat.ShowMusicActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {
private Button mSend;
private EditText mResetbyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        mSend = findViewById(R.id.resetPassword);
        mResetbyEmail = findViewById(R.id.emailReset);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(mResetbyEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetActivity.this, "Sent Reset Email link Succesfully", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                catch (Exception e){
                    Toast.makeText(ResetActivity.this, "Please Enter valid Email Id", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
