package com.example.demo.forJsonObject.file;

import java.util.ArrayList;

public class FileJsonOutput {

    private String filename;
    private String author;
    private String editor;
    private String date;
    private ArrayList<String> tag;

    public FileJsonOutput () {
    }

    public FileJsonOutput (String filename, String author, String editor, String date, ArrayList<String> tag) {
        this.filename = filename;
        this.author = author;
        this.editor = editor;
        this.date = date;
        this.tag = tag;
    }

    public String getFilename () {
        return filename;
    }

    public void setFilename (String filename) {
        this.filename = filename;
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
