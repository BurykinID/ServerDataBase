package com.example.demo.forJsonObject.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class UserJSON {

    private String username;
    private String password;
    private String email;

}
