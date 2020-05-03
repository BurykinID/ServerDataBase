package com.example.demo.forJsonObject.file.filter;

import com.example.demo.forJsonObject.file.forUpload.Tag;
import com.example.demo.forJsonObject.user.Username;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Data
public class Filter {

    private String username;
    private ArrayList<Tag> tag;

}
