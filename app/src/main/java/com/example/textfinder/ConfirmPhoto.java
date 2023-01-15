package com.example.textfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

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
                Intent i = new Intent(getApplicationContext(), EditPage.class);
                i.putExtra("imagepath", uriPath);
                startActivity(i);
            }
        });


    }
}