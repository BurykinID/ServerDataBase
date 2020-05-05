package com.example.demo.forJsonObject;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Response {

    private String status;
    private String description;

    public Response (String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String userNotFound() {
        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("User does not found");
        return gson.toJson(response);
    }

    public String userAlreadyExists() {
        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("User already exists");
        return gson.toJson(response);
    }

    public String userCreateSuccess () {
        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("ok");
        response.setDescription("User create success");
        return gson.toJson(response);
    }

    public String userNotActive() {
        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("ok");
        response.setDescription("User does not active");
        return gson.toJson(response);
    }

    public String fileWithThisTagAndPermissionNotFound() {
        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("You have not file with this tag and permission");
        return gson.toJson(response);
    }

    public String fileWithThisTagNotFound() {
        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("File with this tag does not found");
        return gson.toJson(response);
    }

    public String fileErrorEncode () {

        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("Error with encode Base 64");
        return gson.toJson(response);

    }

    public String fileAccessDenied () {

        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("Access denied");
        return gson.toJson(response);

    }

    public String fileNotFound () {

        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("File does not found");
        return gson.toJson(response);

    }

    public String fileWithPermissionNotFound () {

        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("For this user file not found");
        return gson.toJson(response);

    }

    public String userListIsEmpty () {

        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("Userlist is empty");
        return gson.toJson(response);

    }

    public String userAndFileNotFound () {

        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("User and file does not found");
        return gson.toJson(response);

    }

    public String emptyInfo () {

        Gson gson = new Gson();
        Response response = new Response();
        response.setStatus("error");
        response.setDescription("Empty info");
        return gson.toJson(response);

    }
}
