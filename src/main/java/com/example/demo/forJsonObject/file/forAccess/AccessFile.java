package com.example.demo.forJsonObject.file.forAccess;

import com.example.demo.forJsonObject.file.forUpload.Tag;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@Getter
@Setter
public class AccessFile {

    private String id;
    private String filename;
    private ArrayList<Option> options;
    private ArrayList<String> tag;

    public AccessFile (String id, String filename, ArrayList<Option> options, ArrayList<String> tag) {
        this.id = id;
        this.filename = filename;
        this.options = options;
        this.tag = tag;
    }

}
