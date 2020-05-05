package com.example.demo.controller.user;

import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.user.UserJSON;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
public class RegistrationController {

    private final UserService userService;
    private final UserRepository userRepository;
//    private HashData hashData = new HashData();

    public RegistrationController (UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @PostMapping("/registration")
    public ResponseEntity create(@RequestBody UserJSON user) throws NoSuchAlgorithmException {
        String username = user.getUsername();
        String password = user.getPassword();
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
//        String hashedPassword = hashData.get_SHA_512_SecurePassword(password);

        User user1 = new User();
        user1.setUsername(username);
        user1.setPassword(encodedPassword);
        user1.setEmail(user.getEmail());

        if (!userService.addUser(user1)) {
            return new ResponseEntity<>("User already exists", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("User is create", HttpStatus.OK);
    }

}