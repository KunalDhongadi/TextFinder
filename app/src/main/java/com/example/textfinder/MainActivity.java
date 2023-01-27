 package com.example.textfinder;

import static android.Manifest.permission.CAMERA;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.io.File;

import java.io.IOException;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

 public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FloatingActionButton fab1, fab2;
    private Bitmap imageBitmap;
    private Uri image;

    private Button backupBtn;


    private DBManager dbManager;

    static final int PICK_IMAGE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int REQUEST_STORAGE = 3;

    private List<Note> notes = new ArrayList<Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);

//        backupBtn = findViewById(R.id.backupBtn);

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursorObject = dbManager.fetch();

//        System.out.println("Length of cursor1-" + cursor.getString(cursor.getColumnIndexOrThrow("title")));
//        System.out.println("Length of cursor2-" + cursor.getString(cursor.getColumnIndexOrThrow("content")));
//        System.out.println("Length of cursor3-" + cursor.getString(cursor.getColumnIndexOrThrow("date")));
//        cursor.moveToFirst();
        for (cursorObject.moveToLast(); !cursorObject.isBeforeFirst(); cursorObject.moveToPrevious()) {
            System.out.println("Inside cursor-");
            int id = cursorObject.getInt(cursorObject.getColumnIndexOrThrow("id"));
            String titleText = cursorObject.getString(cursorObject.getColumnIndexOrThrow("title"));
            String content = cursorObject.getString(cursorObject.getColumnIndexOrThrow("content"));
            String currentDate = cursorObject.getString(cursorObject.getColumnIndexOrThrow("date"));
            notes.add(new Note(id,titleText,content,currentDate));
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
                if(checkPermission() && checkPermissionStorage()){
                    try {
                        captureImage();
                    } catch (IOException errorIO) {
                        errorIO.printStackTrace();
                    }
                }else if(checkPermission()){
                    requestPermissionStorage();
                }else{
                    requestPermissionStorage();
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

     private boolean checkPermissionStorage(){
         int storagePermission = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
         return storagePermission == PackageManager.PERMISSION_GRANTED;
     }


    private void requestPermission(){
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_CODE);
    }

    private void requestPermissionGallery(){
        int PERMISSION_CODE = 202;
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
    }

     private void requestPermissionStorage(){
         int PERMISSION_CODE = 204;
         ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
     }

    private void captureImage() throws IOException {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager()) != null){
            File photoFileObj = createImageFile();
            if (photoFileObj != null) {
                Uri photoURIObj = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFileObj);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURIObj);
                startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void pickImage(){

        Intent galleryPickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(galleryPickIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(galleryPickIntent, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCodeInt, @NonNull String[] permissions, @NonNull int[] grantResultsList) {
        super.onRequestPermissionsResult(requestCodeInt , permissions, grantResultsList);
        if (requestCodeInt == REQUEST_IMAGE_CAPTURE) {
            if (grantResultsList.length > 0 && grantResultsList[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    captureImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCodeInt == REQUEST_IMAGE_CAPTURE) {
            if (grantResultsList.length > 0 && grantResultsList[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCodeInt == REQUEST_STORAGE) {
            if (grantResultsList.length > 0 && grantResultsList[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission accepted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
//
    }

     String imageFilePath;
     private File createImageFile() throws IOException {
         String timeStampStr =
                 new SimpleDateFormat("yyyyMMdd_HHmmss",
                         Locale.getDefault()).format(new Date());
         String imageFileNameStr = "TFA_" + timeStampStr + "_";
         File cacheDir =
                 getCacheDir();
         File imageFileObj = File.createTempFile(
                 imageFileNameStr,  /* prefix */
                 ".jpg",         /* suffix */
                 cacheDir      /* directory */
         );

         imageFilePath = imageFileObj.getAbsolutePath();
         return imageFileObj;
     }


    @Override
    protected void onActivityResult(int requestCode, int resultCodeInt, @Nullable Intent dataIntent) {
        super.onActivityResult(requestCode, resultCodeInt, dataIntent);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCodeInt == RESULT_OK){


            Uri uri = Uri.fromFile(new File(imageFilePath));


            Intent nextActivityIntent = new Intent(this, ConfirmPhoto.class);
            nextActivityIntent.putExtra("imagepath", uri);
            startActivity(nextActivityIntent);


        }else if(requestCode == PICK_IMAGE && resultCodeInt == RESULT_OK){
            Uri uri = dataIntent.getData();

            Intent nextActivityIntent = new Intent(this, ConfirmPhoto.class);
            nextActivityIntent.putExtra("imagepath", uri);
            startActivity(nextActivityIntent);

        }
    }
 }