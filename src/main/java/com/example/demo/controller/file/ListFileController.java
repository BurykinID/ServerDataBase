package com.example.demo.controller.file;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.Token;
import com.example.demo.forJsonObject.file.FileJsonOutput;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.role.Role.ADMIN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class ListFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final AccessRepository accessRepository;
    private final JwtToken jwtToken;

    public ListFileController (FileRepository fileRepository, UserRepository userRepository, AccessRepository accessRepository, JwtToken jwtToken) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
        this.jwtToken = jwtToken;
    }

    @GetMapping (value = "/listFile")
    public ResponseEntity getListFile(@RequestHeader ("Authorization") String token) {

        String username = jwtToken.getUsernameFromToken(token.substring(7));

        User user = userRepository.findByUsername(username);
        Gson gson = new Gson();

        if (user != null) {

            List<File> files = fileRepository.findAll();
            ArrayList<File> accessFiles = new ArrayList<>();

            if (user.getRoles().contains(ADMIN)) {
                accessFiles.addAll(files);
                ArrayList<FileJsonOutput> jsonFiles = new ArrayList<>();

                for (File file : accessFiles) {
                    jsonFiles.add(new FileJsonOutput(file.getFilename(), file.getAuthor(), file.getAuthor(), file.getDate(), file.getTag()));
                }

                String responseString = gson.toJson(jsonFiles);
                return new ResponseEntity<>(responseString, OK);
            }
            else {
                for (File file: files) {
                    Access access = accessRepository.findByUsernameAndFilename(username, file.getFilename());
                    if (access != null && (Integer.parseInt(access.getAccess()) >= 1)) {
                        accessFiles.add(file);
                    }
                }

                if (accessFiles != null && accessFiles.size() > 0) {

                    ArrayList<FileJsonOutput> jsonFiles = new ArrayList<>();

                    for (File file : accessFiles) {
                        jsonFiles.add(new FileJsonOutput(file.getFilename(), file.getAuthor(), file.getAuthor(), file.getDate(), file.getTag()));
                    }

                    String responseString = gson.toJson(jsonFiles);
                    return new ResponseEntity<>(responseString, OK);

                }
                else {
                    // уточняю
                    return new ResponseEntity<>("", OK);
                }
            }

        }
        else {
            return new ResponseEntity<>("User does not found", NOT_FOUND);
        }

    }

}