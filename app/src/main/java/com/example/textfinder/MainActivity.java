 package com.example.textfinder;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

 public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab1, fab2;
    private Bitmap imageBitmap;

    private DBManager dbManager;

    static final int PICK_IMAGE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    private List<Note> notes = new ArrayList<Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();

//        System.out.println("Length of cursor1-" + cursor.getString(cursor.getColumnIndexOrThrow("title")));
//        System.out.println("Length of cursor2-" + cursor.getString(cursor.getColumnIndexOrThrow("content")));
//        System.out.println("Length of cursor3-" + cursor.getString(cursor.getColumnIndexOrThrow("date")));
//        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            System.out.println("Inside cursor-");
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            notes.add(new Note(title,content,date));
        }
//        System.out.println("Cursor--" + cursor);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermissionGallery()){
                    pickImage();
                }else{
                    requestPermissionGallery();
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    captureImage();
                }else{
                    requestPermission();
                }
            }
        });



        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        NotesAdapter myNotesAdapter = new NotesAdapter(notes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myNotesAdapter);
    }

     @Override
     public void onBackPressed() {
         finishAffinity();
     }

    private boolean checkPermission(){
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }
    private boolean checkPermissionGallery(){
        int galleryPermission = ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);
        return galleryPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_CODE);
    }

    private void requestPermissionGallery(){
        int PERMISSION_CODE = 202;
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
    }

    private void captureImage(){
        Intent takephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takephoto.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takephoto, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void pickImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

//        Intent chooserIntent = Intent.createChooser(intent, "Select Image");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intent});

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean galleryPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if(cameraPermission){
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
                captureImage();
            }else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
                return;
            }
            if(galleryPermission){
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
                pickImage();
            }else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Intent i = new Intent(this, ConfirmPhoto.class);
            i.putExtra("image", imageBitmap);
            startActivity(i);


        }else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Bitmap imageBitmap;
            try {

                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Intent i = new Intent(this, ConfirmPhoto.class);
                i.putExtra("image", imageBitmap);
                startActivity(i);
            } catch (IOException e) {

                e.printStackTrace();
            }


        }
    }
 }