package com.example.demo.forJsonObject.file.forUpload;

public class FileJson {

    private String username;
    private String filename;
    private String content;
    private String tag;

    public FileJson () {
    }

    public FileJson (String username, String filename, String content, String tag) {
        this.username = username;
        this.filename = filename;
        this.content = content;
        this.tag = tag;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getFilename () {
        return filename;
    }

    public void setFilename (String filename) {
        this.filename = filename;
    }

    public String getContent () {
        return content;
    }

    public void setContent (String content) {
        this.content = content;
    }

    public String getTag () {
        return tag;
    }

    public void setTag (String tag) {
        this.tag = tag;
    }
}
