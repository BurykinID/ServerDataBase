package com.example.demo.controller.user;

import com.example.demo.entity.User;
import com.example.demo.form.UserForm;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class RegistrationController {

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/registration")
    public String registration(Model model) {

        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        return "users/registration";
    }

    @PostMapping(value = "/registration")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          Map<String, Object> model) {
        User userFromDb =  userRepository.findByUsername(username);

        if (userFromDb != null) {
            // нужно сделать отображение на экране при помощи thymeleaf
            // model.put("message", "User already exists!");
            System.out.println("user already exists!");
            return "redirect:/registration";
        }

        User newUser = new User(username, password, email);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
        newUser.setRoles(roles);
        userRepository.save(newUser);

        return "redirect:/login";
    }

}
