package com.example.demo.forJsonObject.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

// для
@Data
@NoArgsConstructor
@Getter
@Setter
public class ArrayUsers {

    private ArrayList<String> userByName;

}
