package com.example.demo.controller.user;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String getLoginForm() {
        return "users/login";
    }

    @PostMapping ("/login")
    public String postLoginForm( @RequestParam String username,
                                @RequestParam String password) {

        User user = userRepository.findByUsername(username);

        if ((user.getActivationCode() == null) && (user.getPassword().equals(password))) {
            /*System.out.println("я тут");*/
            return "redirect:/listFile";
        }

        return "redirect:/login";
    }

}
