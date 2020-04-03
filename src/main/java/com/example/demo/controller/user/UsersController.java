package com.example.demo.controller.user;

import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userList")
public class UsersController {

    private final UserRepository userRepository;

    public UsersController (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String getUserList(Model model) {

        model.addAttribute("users", userRepository.findAll());

        return "users/userList";
    }


}
