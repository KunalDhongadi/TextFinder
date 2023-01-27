package com.example.textfinder;

public class Folder {

    private int id;
    private String title;

    public Folder(String title) {
        this.title = title;
    }


    public String getNoteTitle() {
        return title;
    }

    public void setNoteTitle(String title) {
        this.title = title;
    }
}
