package com.example.textfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ViewNote extends AppCompatActivity {

    private TextView title;
    private EditText editContent;
    private ImageButton backBtn;
    private Button copyBtn, cancelBtn, saveBtn, shareBtn;

    private String titleView;
    private String content;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        title = findViewById(R.id.titleView);
        editContent = findViewById(R.id.resultText);
        copyBtn = findViewById(R.id.copy_btn);
        backBtn = findViewById(R.id.back_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        saveBtn = findViewById(R.id.save_btn);
        shareBtn = findViewById(R.id.shareBtn);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        titleView = (String) extras.get("title");
        content = (String) extras.get("content");

        title.setText(titleView);
        editContent.setText(content);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copiedText", content);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ViewNote.this, "Text Copied!", Toast.LENGTH_SHORT).show();
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

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Insert Subject here");
                String note = titleView + " : \n" + content;
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,note);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });




    }
}