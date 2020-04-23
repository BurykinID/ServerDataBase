package com.example.demo.forJsonObject;

import java.util.ArrayList;

public class ArrayUsers {

    ArrayList<String> userByName;

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
