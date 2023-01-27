package com.example.textfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class ConfirmPhoto extends AppCompatActivity {

    private Button extractBtn;
    private ImageButton backBtn;

    byte[] byteArray;
    Uri uriPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_photo);

        backBtn = findViewById(R.id.back_btn);

//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
//        imageUri = Uri.parse(extras.getString("imagepath"));

        uriPath = getIntent().getParcelableExtra("imagepath");

//        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        ImageView imageView = findViewById(R.id.cameraImage);
        imageView.setImageURI(uriPath);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        extractBtn = findViewById(R.id.extractBtn);
        extractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(getApplicationContext(), EditPage.class);
                imageIntent.putExtra("imagepath", uriPath);
                startActivity(imageIntent);
            }
        });


    }
}