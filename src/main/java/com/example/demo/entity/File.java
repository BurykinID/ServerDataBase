package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
public class File {

    @Id
    private UUID id;
    private String filename;
    private String size;
    @Size(min = 13)
    @Column(length = 15)
    private String date;
    private String author;
    private String editor;
    private String path;
    private ArrayList<String> tag;

    public File (UUID id,
                 String filename,
                 String size,
                 String date,
                 String author,
                 String editor,
                 String path,
                 ArrayList<String> tag
                 ) {
        this.id = id;
        this.filename = filename;
        this.size = size;
        this.date = date;
        this.author = author;
        this.editor = editor;
        this.path = path;
        this.tag = tag;
    }

}