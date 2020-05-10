package com.example.demo.forJsonObject.file.Get;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class FileForGetVersion {

    private String filename;
    private String author;
    private String version;

}
