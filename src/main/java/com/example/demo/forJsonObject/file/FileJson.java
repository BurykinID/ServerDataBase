package com.example.demo.forJsonObject.file;

import java.util.ArrayList;

public class FileJson {

    private String author;
    private String editor;
    private String date;
    private ArrayList<String> tag;

    public FileJson () {
    }

    public FileJson (String author, String editor, String date, ArrayList<String> tag) {
        this.author = author;
        this.editor = editor;
        this.date = date;
        this.tag = tag;
    }

    public String getAuthor () {
        return author;
    }

    public void setAuthor (String author) {
        this.author = author;
    }

    public String getEditor () {
        return editor;
    }

    public void setEditor (String editor) {
        this.editor = editor;
    }

    public String getDate () {
        return date;
    }

    public void setDate (String date) {
        this.date = date;
    }

    public ArrayList<String> getTag () {
        return tag;
    }

    public void setTag (ArrayList<String> tag) {
        this.tag = tag;
    }
}
