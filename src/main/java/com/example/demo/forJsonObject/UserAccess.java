package com.example.demo.forJsonObject;

import java.util.ArrayList;

public class UserAccess {

    String username;
    ArrayList<Username> usernameForShare;

    public UserAccess () {
    }

    public UserAccess (String username, ArrayList<Username> usernameForShare) {
        this.username = username;
        this.usernameForShare = usernameForShare;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public ArrayList<Username> getUsernameForShare () {
        return usernameForShare;
    }

    public void setUsernameForShare (ArrayList<Username> usernameForShare) {
        this.usernameForShare = usernameForShare;
    }
}
