package com.example.demo.forJsonObject.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// для Json конструкции вида [
// { "username" : "value"},
// { "username" : "value"} ...
// ]
@Data
@NoArgsConstructor
@Getter
@Setter
public class Username {

    private String username;

}
