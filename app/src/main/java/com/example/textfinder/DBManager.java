package com.example.textfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String title, String content, String date) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TITLE, title);
        contentValue.put(DatabaseHelper.CONTENT, content);
        contentValue.put(DatabaseHelper.DATE, date);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] tablecolumns = new String[] { DatabaseHelper.ID, DatabaseHelper.TITLE, DatabaseHelper.CONTENT, DatabaseHelper.DATE};
        Cursor cursorVar = database.query(DatabaseHelper.TABLE_NAME, tablecolumns, null, null, null, null, null);
        if (cursorVar != null) {
            cursorVar.moveToFirst();
        }

        return cursorVar;
    }

    public int updateTable(long id, String title, String content) {
        ContentValues contentValuesVar = new ContentValues();
        contentValuesVar.put(DatabaseHelper.TITLE, title);
        contentValuesVar.put(DatabaseHelper.CONTENT, content);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValuesVar, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public void delete(long id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
    }

}
