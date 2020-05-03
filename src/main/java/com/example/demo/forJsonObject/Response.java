package com.example.demo.forJsonObject;

import com.google.gson.Gson;

public class Response {

    private String status;
    private String description;

    public Response () {
    }

    public Response (String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String printError(String status, String description, Response response) {

        Gson gson = new Gson();
        response.setStatus(status);
        response.setDescription(description);
        String responseString = gson.toJson(response);

        return responseString;

    }

}
