package com.example.demo.controller.user;

import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.user.UserLogin;
import com.example.demo.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
/*@RequestMapping("/auth")*/
public class LoginController {

    private final UserRepository userRepository;

    public LoginController ( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/login")
    public String loginPage(@RequestBody UserLogin userLogin) {

        Gson gson = new Gson();
        Response response = new Response();

        User user = userRepository.findByUsername(userLogin.getUsername());

        if (user != null) {
            if (user.getActivationCode() != null) {
                return response.printError("error", "User is inactive!", response);
            }
            if (user.getPassword().equals(userLogin.getPassword())) {
                return response.printError("ok", "User login success!", response);
            }
            else {
                return response.printError("error", "Password is incorrect!", response);
            }
        }
        else {
            return response.printError("error", "User doesn't found!", response);
        }

    }

}
