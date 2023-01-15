package com.example.textfinder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ViewNote extends AppCompatActivity {

    private TextView title;
    private EditText editContent;
    private ImageButton backBtn;
    private Button copyBtn, deleteBtn, saveBtn, shareBtn;

    private int id;
    private String titleView;
    private String content;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        title = findViewById(R.id.titleView);
        editContent = findViewById(R.id.resultText);
        copyBtn = findViewById(R.id.copy_btn);
        backBtn = findViewById(R.id.back_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        saveBtn = findViewById(R.id.save_btn);
        shareBtn = findViewById(R.id.shareBtn);

        dbManager = new DBManager(this);
        dbManager.open();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        id = (int) extras.get("id");
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


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewNote.this);
                builder.setMessage("Do you really want to delete this text?");
                builder.setTitle("Alert!");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dbManager.delete(id);
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                });

                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbManager.update(id, titleView, editContent.getText().toString());
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });




    }
}