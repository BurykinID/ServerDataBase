package com.example.demo.forJsonObject.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLogin {

    private String token;
    private String password;

}
