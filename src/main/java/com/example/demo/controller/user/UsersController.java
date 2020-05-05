package com.example.demo.controller.user;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.forJsonObject.user.ArrayUsers;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("listUser")
public class UsersController {

    private final UserRepository userRepository;
    private final JwtToken jwtToken;

    public UsersController (UserRepository userRepository, JwtToken jwtToken) {
        this.userRepository = userRepository;
        this.jwtToken = jwtToken;
    }

    // success
    @GetMapping
    public ResponseEntity getUserList(@RequestBody String token) {

        jwtToken.getUsernameFromToken(token);

        Gson gson = new Gson();
        ArrayList<User> users = userRepository.findAll();
        ArrayUsers arrayUsers = new ArrayUsers();
        ArrayList<String> userByName = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            userByName.add(users.get(i).getUsername());
        }
        arrayUsers.setUserByName(userByName);
        String response = gson.toJson(arrayUsers);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    //succes
    @GetMapping(value = "{name}")
    public ResponseEntity getUser(@PathVariable (name = "name") String name) {

        Gson gson = new Gson();
        ArrayUsers userByName = new ArrayUsers();
        userByName.setUserByName(userRepository.findByPartName(name));
        String response = gson.toJson(userByName);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }



}
