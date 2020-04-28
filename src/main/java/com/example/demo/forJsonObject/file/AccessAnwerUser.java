package com.example.demo.forJsonObject.file;

import com.example.demo.forJsonObject.user.Username;

import java.util.ArrayList;

/* для возврата положительного ответа на запрос о выделении прав вида:
    [
        { "userWithoutError" :
            [
                {"username" : "value"}
            ]
        },
        { "userWithError" :
            [
                {"username" : "value"}
            ]
        }
    ]
*/
public class AccessAnwerUser {

    private ArrayList<Username> userWithoutError;
    private ArrayList<Username> userWithError;

    public AccessAnwerUser () {
    }

    public AccessAnwerUser (ArrayList<Username> userWithoutError, ArrayList<Username> userWithError) {
        this.userWithoutError = userWithoutError;
        this.userWithError = userWithError;
    }

    public ArrayList<Username> getUserWithoutError () {
        return userWithoutError;
    }

    public void setUserWithoutError (ArrayList<Username> userWithoutError) {
        this.userWithoutError = userWithoutError;
    }

    public ArrayList<Username> getUserWithError () {
        return userWithError;
    }

    public void setUserWithError (ArrayList<Username> userWithError) {
        this.userWithError = userWithError;
    }
}