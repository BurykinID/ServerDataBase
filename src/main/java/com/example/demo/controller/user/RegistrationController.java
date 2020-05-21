package com.example.demo.controller.user;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.user.UserJSON;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.example.demo.service.UserService;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;

import static com.example.demo.role.Role.ADMIN;
import static org.springframework.http.HttpStatus.*;

@RestController
public class RegistrationController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtToken jwtToken;
//    private HashData hashData = new HashData();


    public RegistrationController (UserService userService, UserRepository userRepository, JwtToken jwtToken) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtToken = jwtToken;
    }

    @PostMapping("/createAdmin")
    public ResponseEntity createFirstAdmin (@RequestBody UserJSON userJSON) {

        String username = userJSON.getUsername();
        String password = userJSON.getPassword();
        String encodedPassword = new BCryptPasswordEncoder().encode(password);

        User user1 = new User();
        user1.setUsername(username);
        user1.setPassword(encodedPassword);
        user1.setEmail(userJSON.getEmail());

        if (!userService.addUser(user1, Role.ADMIN)) {
            return new ResponseEntity<>("User already exists", FORBIDDEN);
        }

        return new ResponseEntity<>("User is create", OK);

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
            return new ResponseEntity<>("User already exists", FORBIDDEN);
        }
        return new ResponseEntity<>("User is create", OK);
    }

    //success
    @GetMapping (value = "/activate/{code}")
    public ResponseEntity activate(@PathVariable (name = "code") String code) {

        boolean isActivated = userService.activateUser(code);

        if(isActivated) {
            return new ResponseEntity<>("User has activate", OK);
        }

        else {
            return new ResponseEntity<>("Activate code does not found", NOT_FOUND);
        }

    }

    @PostMapping("/admin/registration")
    public ResponseEntity createAdmin(@RequestHeader ("Authorization") String token,
                                      @RequestBody UserJSON userJSON) {

        User admin = userRepository.findByUsername(jwtToken.getUsernameFromToken(token.substring(7)));

        if (admin.getRoles().contains(ADMIN)) {
            String username = userJSON.getUsername();
            String password = userJSON.getPassword();
            String encodedPassword = new BCryptPasswordEncoder().encode(password);

            User user1 = new User();
            user1.setUsername(username);
            user1.setPassword(encodedPassword);
            user1.setEmail(userJSON.getEmail());

            if (!userService.addUser(user1, Role.ADMIN)) {
                return new ResponseEntity<>("User already exists", FORBIDDEN);
            }
            return new ResponseEntity<>("User is create", OK);
        }

        return new ResponseEntity("Access denied", FORBIDDEN);



    }

    @PostMapping("/admin/deactivate")
    public ResponseEntity deactivateAdmin(@RequestHeader ("Authorization") String token,
                                          @RequestBody Username username) {

        User GMadmin = userRepository.findByUsername(jwtToken.getUsernameFromToken(token.substring(7)));

        if (GMadmin.getRoles().contains(ADMIN)) {
            User user = userRepository.findByUsername(username.getUsername());

            if (!user.getUsername().equals(GMadmin.getUsername())) {
                user.setActivationCode(String.valueOf(UUID.randomUUID()));
                userRepository.save(user);
                return new ResponseEntity("User has deactivate", OK);
            }
            else {
                return new ResponseEntity("Access denied", FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity("Access denied", FORBIDDEN);
        }

    }

    @PostMapping("/admin/clear")
    public ResponseEntity clearAdmin(@RequestHeader ("Authorization") String token,
                                     @RequestBody Username username) {

        User GMadmin = userRepository.findByUsername(jwtToken.getUsernameFromToken(token.substring(7)));

        if (GMadmin.getRoles().contains(ADMIN)) {
            User user = userRepository.findByUsername(username.getUsername());
            if (user.getRoles().contains(ADMIN)) {
                Set<Role> roles = user.getRoles();
                roles.remove(ADMIN);
                user.setRoles(roles);
            }
            userRepository.save(user);
            return new ResponseEntity("Admin account has deactivate", OK);
        }
        else {
            return new ResponseEntity("Access denied", FORBIDDEN);
        }

    }

}