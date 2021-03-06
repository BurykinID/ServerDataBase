package com.example.demo.controller.user;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.user.ArrayUsers;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

import static com.example.demo.role.Role.ADMIN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/listUser")
public class UsersController {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final AccessRepository accessRepository;
    private final JwtToken jwtToken;

    public UsersController (UserRepository userRepository, FileRepository fileRepository, AccessRepository accessRepository, JwtToken jwtToken) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.accessRepository = accessRepository;
        this.jwtToken = jwtToken;
    }

    // success
    @GetMapping ()
    public ResponseEntity getUserList(@RequestHeader ("Authorization") String token) {

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
    @GetMapping(value = "/{name}")
    public ResponseEntity getUser(@RequestHeader ("Authorization") String token,
                                  @PathVariable (name = "name") String name) {

        Gson gson = new Gson();
        ArrayUsers userByName = new ArrayUsers();
        userByName.setUserByName(userRepository.findByPartName(name));
        String response = gson.toJson(userByName);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping(value = "/file/{id}")
    public ResponseEntity getUserForFile(@RequestHeader ("Authorization") String token,
                                         @PathVariable (name = "id") String id) {

        Gson gson = new Gson();
        ArrayUsers arrayUsers = new ArrayUsers();
        ArrayList<String> userForPermit = new ArrayList<>();

        File file = fileRepository.findById(UUID.fromString(id));

        ArrayList<Access> accessUserList = accessRepository.findByIdFile(String.valueOf(file.getId()));
        for (Access access : accessUserList) {
            if (!userForPermit.contains(access.getUsername())) {
                userForPermit.add(access.getUsername());
            }
        }

        ArrayList<User> admins = userRepository.findByRoles(ADMIN);

        for (User usernameAdmin : admins) {
            userForPermit.add(usernameAdmin.getUsername());
        }
        
        arrayUsers.setUserByName(userForPermit);

        if (arrayUsers != null && userForPermit.size() > 0) {
            return new ResponseEntity(gson.toJson(arrayUsers), OK);
        }


        return new ResponseEntity("File not found", NOT_FOUND);

    }

}
