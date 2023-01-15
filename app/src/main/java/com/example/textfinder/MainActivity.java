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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        Cursor cursor = dbManager.fetch();

//        System.out.println("Length of cursor1-" + cursor.getString(cursor.getColumnIndexOrThrow("title")));
//        System.out.println("Length of cursor2-" + cursor.getString(cursor.getColumnIndexOrThrow("content")));
//        System.out.println("Length of cursor3-" + cursor.getString(cursor.getColumnIndexOrThrow("date")));
//        cursor.moveToFirst();
        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            System.out.println("Inside cursor-");
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            notes.add(new Note(id,title,content,date));
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
                    } catch (IOException e) {
                        e.printStackTrace();
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
        Intent takephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takephoto.resolveActivity(getPackageManager()) != null){
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                takephoto.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takephoto, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void pickImage(){
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);

//        Intent chooserIntent = Intent.createChooser(intent, "Select Image");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intent});


        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 1) ;

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    captureImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission accepted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
//        if(grantResults.length > 0){
//            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//            boolean galleryPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//            boolean storagePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
//            if(cameraPermission && storagePermission){
//                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
//                try {
//                    captureImage();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }else{
//                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if(galleryPermission){
//                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
//                pickImage();
//            }else{
//                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
    }

     String imageFilePath;
     private File createImageFile() throws IOException {
         String timeStamp =
                 new SimpleDateFormat("yyyyMMdd_HHmmss",
                         Locale.getDefault()).format(new Date());
         String imageFileName = "TFA_" + timeStamp + "_";
         File cacheDir =
                 getCacheDir();
         File image = File.createTempFile(
                 imageFileName,  /* prefix */
                 ".jpg",         /* suffix */
                 cacheDir      /* directory */
         );

         imageFilePath = image.getAbsolutePath();
         return image;
     }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){


            Uri uri = Uri.fromFile(new File(imageFilePath));





//            Bitmap imageBitmap;
//            byte[] byteArray = new byte[0];

//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();

//            try {
//                InputStream inputStream = getContentResolver().openInputStream(uri);
//                FileOutputStream fileOutputStream = new FileOutputStream(createImageFile());
//                byte[] buffer = new byte[1024];
//
//                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
//
//                int bytesRead;
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    fileOutputStream.write(buffer, 0, bytesRead);
//                    byteBuffer.write(buffer, 0 ,bytesRead);
//                }
//
//
//                fileOutputStream.close();
//                byteBuffer.flush();
//                inputStream.close();
//
//                byteArray = byteBuffer.toByteArray();
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//            Log.v(TAG, "Clicked photo byteArray-" + byteArray);

            Intent i = new Intent(this, ConfirmPhoto.class);
            i.putExtra("imagepath", uri);
            startActivity(i);


        }else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            Uri uri = data.getData();
//            Bitmap imageBitmap;
//            byte[] byteArray = new byte[0];

//            try {
//                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byteArray = stream.toByteArray();
//
//                Log.v(TAG, "Picked photo byteArray-" + byteArray);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            Intent i = new Intent(this, ConfirmPhoto.class);
            i.putExtra("imagepath", uri);
            startActivity(i);

        }
    }
 }