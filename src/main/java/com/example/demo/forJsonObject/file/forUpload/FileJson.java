package com.example.demo.forJsonObject.file.forUpload;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class FileJson {

    private String filename;
    private String content;
    private String tag;

}
