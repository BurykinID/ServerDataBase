package com.example.demo.forJsonObject.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@Getter
@Setter
public class UserAccess {

    private ArrayList<Username> usernameForShare;

}
