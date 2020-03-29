package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Size;
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
    @Size(min = 4, max = 6, message = "Тип создаваемого объекта файл или папка")
    private String type;
    @Size(min = 4, message = "Длина минимум 4 символа")
    private String size;
    @Size(min = 13)
    @Column(length = 15)
    private String date;
    @NonNull
    private String parent;
    @Size(min = 5, message = "Автор не может быть короче")
    private String author;
    @Size(min = 5, message = "")
    private String editor;
    private String path;

    public File (String filename, String type, String size,String date, String parent, String author, String editor, String path) {
        this.filename = filename;
        this.type = type;
        this.size = size;
        this.date = date;
        this.parent = parent;
        this.author = author;
        this.editor = editor;
        this.path = path;
    }

}