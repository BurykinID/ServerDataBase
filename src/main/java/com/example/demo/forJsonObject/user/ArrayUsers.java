package com.example.demo.forJsonObject.user;

import java.util.ArrayList;

// для
public class ArrayUsers {

    private ArrayList<String> userByName;

    public ArrayUsers () {
    }

    public ArrayUsers (ArrayList<String> userByName) {
        this.userByName = userByName;
    }

    public ArrayList<String> getUserList () {
        return userByName;
    }

    public void setUserList (ArrayList<String> userByName) {
        this.userByName = userByName;
    }
}
