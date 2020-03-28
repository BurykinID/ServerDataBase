package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          Map<String, Object> model) {
        User userFromDb =  userRepository.findByUsername(username);

        if (userFromDb != null) {
            //model.put("message", "User already exists!");
            System.out.println();
            return "registration";
        }

        User newUser = new User(username, password);
        newUser.setRoles(Collections.singleton(Role.USER));
        userRepository.save(newUser);

        return "redirect:/login";
    }

}
