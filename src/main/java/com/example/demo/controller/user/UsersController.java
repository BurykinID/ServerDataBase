package com.example.demo.controller.user;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.forJsonObject.ArrayUsers;
import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("userList")
public class UsersController {

    private final UserRepository userRepository;

    public UsersController (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @ResponseBody
    public String/*JsonArray*/ getUserList() {

        /*JsonArray usersInJson = new JsonArray();

        for (int i = 0; i < users.size(); i++) {
            JsonObject user = new JsonObject();
            user.addProperty("name", users.get(i).getUsername());
            user.addProperty("id", String.valueOf(users.get(i).getId()));
            usersInJson.add(user);
        }

        return usersInJson;*/

        Gson gson = new Gson();
        ArrayList<User> users = userRepository.findAll();
        ArrayUsers test = new ArrayUsers();
        ArrayList<String> userByName = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            userByName.add(users.get(i).getUsername());
        }
        test.setUserList(userByName);
        String response = gson.toJson(test);

        return response;

    }

    @RequestMapping(value = "{name}", method = RequestMethod.GET)
    @ResponseBody
    public String getUser(@PathVariable (name = "name") String name) {

        Gson gson = new Gson();
        ArrayUsers userByName = new ArrayUsers();
        userByName.setUserList(userRepository.findByPartName(name));
        String response = gson.toJson(userByName);

        return response;



    }



}
