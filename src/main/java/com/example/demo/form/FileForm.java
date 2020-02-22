package com.example.demo.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileForm {

    private UUID id;
    private String filename;
    private String type;
    private String size;
    private String date;
    private String parent;
    private String author;
    private String editor;

}
