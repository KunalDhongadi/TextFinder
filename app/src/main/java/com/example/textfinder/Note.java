package com.example.textfinder;

public class Note {
    private int id;
    private String title;
    private String content;
    private String dateStr;

    public Note(int id, String title, String content, String dateStr) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dateStr = dateStr;
    }

    public Note(String title, String content, String dateStr) {
        this.title = title;
        this.content = content;
        this.dateStr = dateStr;
    }

    public int getId() {
        return id;
    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return dateStr;
    }

    public void setDate(String date) {
        this.dateStr = date;
    }
}


