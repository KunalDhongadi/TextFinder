package com.example.textfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;

import android.graphics.Point;
import android.graphics.Rect;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.view.WindowManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;

import java.io.IOException;

import java.util.Date;
import java.text.SimpleDateFormat;


public class EditPage extends AppCompatActivity {

    private EditText editTextVar;
    private ImageButton backBtn;
    private Button copyBtn, cancelBtn, saveBtn, smartBtn;
    private Uri uriPath;

    byte[] byteArray;

    private DBManager dbManager;

    private StringBuilder result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        uriPath = getIntent().getParcelableExtra("imagepath");

        editTextVar = findViewById(R.id.resultText);
        copyBtn = findViewById(R.id.copy_btn);
        smartBtn = findViewById(R.id.smart_btn);
        backBtn = findViewById(R.id.back_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        saveBtn = findViewById(R.id.save_btn);

        dbManager = new DBManager(this);
        dbManager.open();


        smartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SmartDetect.class);
                startActivity(i);
            }
        });



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
                AlertDialog.Builder aDBuilder = new AlertDialog.Builder(EditPage.this);
                aDBuilder.setTitle("Enter title");
                aDBuilder.setCancelable(false);

                final EditText titleText = (EditText) view1.findViewById(R.id.editTitle);

                aDBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogVar, int whichElement) {

                        if(TextUtils.isEmpty(titleText.getText().toString())) {
                            Toast.makeText(context, "Title cannot be empty. Please try again", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa E MMM dd, yyyy");
                        Date dateObj = new Date();
                        Note note = new Note(titleText.getText().toString(),editTextVar.getText().toString(),formatter.format(dateObj));

                        dbManager.insert(note.getTitle(), note.getContent(), note.getDate());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    }
                });


                aDBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogObj, int whichBtn) {
                        dialogObj.dismiss();
                    }
                });

                AlertDialog alertDialogObj = aDBuilder.create();
                alertDialogObj.setView(view1);
                alertDialogObj.show();
            }
        });

        try {
            detectText();
        } catch (IOException error) {
            error.printStackTrace();
        }


    }

    private void detectText() throws IOException {
        InputImage inputImage = InputImage.fromFilePath(getApplicationContext(),uriPath);
//        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        TextRecognizer recognizerObject =
                TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());


        Task<Text> resultText = recognizerObject.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
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

                if(result.toString().isEmpty()){
                    Toast.makeText(EditPage.this, "No Text found in the image. Please try again.", Toast.LENGTH_LONG).show();
                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                }

                editTextVar.setText(result.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to detect text from image. " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}