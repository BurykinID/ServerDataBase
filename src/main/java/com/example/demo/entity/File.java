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
    @Size(min = 1, message = "Имя файла должно содерать минимум 1 символ")
    private String filename;
    @Size(min = 4, max = 6, message = "Тип создаваемого объекта файл или папка")
    private String type;
    @Size(min = 4, message = "Длина минимум 4 символа")
    private String size;
    @Size(min = 19, max = 19, message = "Пример заполнения 2020-02-04 12:32:12")
    private String date;
    @NonNull
    private String parent;
    @Size(min = 5, message = "Автор не может быть короче")
    private String author;
    @Size(min = 5, message = "")
    private String editor;

    public File (String filename, String type, String size,String date, String parent, String author, String editor) {
        this.filename = filename;
        this.type = type;
        this.size = size;
        this.date = date;
        this.parent = parent;
        this.author = author;
        this.editor = editor;
    }

}
