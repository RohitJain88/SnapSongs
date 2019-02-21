package com.example.snapchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_music);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        byte[] b = extras.getByteArray("capture");

        if (b != null) {
            ImageView image = findViewById(R.id.musicaptured);

            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            Bitmap rotateBitmap = rotate(decodedBitmap);


            image.setImageBitmap(rotateBitmap);

        }
    }

        private Bitmap rotate (Bitmap decodedBitmap){
            int w = decodedBitmap.getWidth();
            int h = decodedBitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.setRotate(90);

            return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true);

        }
    }

