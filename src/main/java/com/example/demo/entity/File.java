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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private String filename;
    //private String type;
    private String size;
    @Size(min = 13)
    @Column(length = 15)
    private String date;
    /*@NonNull
    private String parent;*/
    private String author;
    private String editor;
    private String path;
    private ArrayList<String> tag;
    //private ArrayList<String> accessList;

    public File (String filename,
                 //String type,
                 String size,
                 String date,
                 //String parent,
                 String author,
                 String editor,
                 String path,
                 ArrayList<String> tag
                 //ArrayList<String> accessList
                 ) {
        this.filename = filename;
        //this.type = type;
        this.size = size;
        this.date = date;
        //this.parent = parent;
        this.author = author;
        this.editor = editor;
        this.path = path;
        this.tag = tag;
        //this.accessList = accessList;
    }

}