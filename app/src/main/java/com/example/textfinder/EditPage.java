package com.example.textfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditPage extends AppCompatActivity {

    private EditText editText;
    private ImageButton backBtn;
    private Button copyBtn;
    private Button cancelBtn;
    private Button saveBtn;
    Bitmap imageBitmap;

    private DBManager dbManager;

    private StringBuilder result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        imageBitmap = (Bitmap) extras.get("image");

        editText = findViewById(R.id.resultText);
        copyBtn = findViewById(R.id.copy_btn);
        backBtn = findViewById(R.id.back_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        saveBtn = findViewById(R.id.save_btn);

        dbManager = new DBManager(this);
        dbManager.open();

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copiedText", result);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(EditPage.this, "Text Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                finish();
                startActivity(i);

            }
        });
        
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = getApplicationContext();
                final View view1 = getLayoutInflater().inflate(R.layout.edit_text_box, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPage.this);
                builder.setTitle("Enter title");
    //                builder.setCancelable(false);

                final EditText titleText = (EditText) view1.findViewById(R.id.editTitle);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date();
                        Note note = new Note(titleText.getText().toString(),result.toString(),date.toString());

                        dbManager.insert(note.getTitle(), note.getContent(), note.getDate());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    }
                });


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.setView(view1);
                alertDialog.show();
            }
        });

        detectText();



    }

    private void detectText(){
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> resultText = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                result = new StringBuilder();
                for(Text.TextBlock block : text.getTextBlocks()){
                    String blockText  = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    System.out.println("blockText:" + blockText);
                    System.out.println("point:" + blockCornerPoint);
                    System.out.println("rect:" + blockFrame);

                    for(Text.Line line : block.getLines()){
                        String lineText = line.getText();
                        Point[] lineCornerPoint = line.getCornerPoints();
                        Rect lineFrame = line.getBoundingBox();

                        System.out.println("lineText:" + lineText);
                        System.out.println("linepoint:" + lineCornerPoint);
                        System.out.println("linerect:" + lineFrame);

                        for(Text.Element element: line.getElements()){
                            String elementText = element.getText();
                            result.append(elementText);

                            System.out.println("elementText:" + elementText);
                            result.append(" ");

                        }
                        result.append("\n");
                    }
                }
//                String s = editText.getText().toString();
                System.out.println("RESULT : " + result.toString());
                editText.setText(result.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to detect text from image. " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}