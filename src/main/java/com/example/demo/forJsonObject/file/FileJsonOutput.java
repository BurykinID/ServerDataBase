package com.example.demo.forJsonObject.file;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@Getter
@Setter
public class FileJsonOutput {

    private String filename;
    private String author;
    private String editor;
    private String date;
    private ArrayList<String> tag;

    public FileJsonOutput (String filename, String author, String editor, String date, ArrayList<String> tag) {
        this.filename = filename;
        this.author = author;
        this.editor = editor;
        this.date = date;
        this.tag = tag;
    }
}
