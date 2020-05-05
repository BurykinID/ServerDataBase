package com.example.demo.controller.file;

import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Base64;

import static com.example.demo.role.Role.ADMIN;
import static org.springframework.http.HttpStatus.*;

public class GetFileController {

    private final FileRepository fileRepository;
    private final AccessRepository accessRepository;
    private final UserRepository userRepository;

    public GetFileController (FileRepository fileRepository, AccessRepository accessRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.accessRepository = accessRepository;
        this.userRepository = userRepository;
    }

    // success, но надо обсудить
    @GetMapping (value = {"/getfile/{filename}"})
    public ResponseEntity getFile(@RequestBody Username username,
                          @PathVariable (name = "filename") String filename) {

        File file = fileRepository.findByFilename(filename);
        User user = userRepository.findByUsername(username.getUsername());
        Gson gson = new Gson();

        if (file != null) {
            if (user != null) {

                boolean isAdmin = user.getRoles().contains(ADMIN);

                if (isAdmin) {
                    try {
                        byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                        String encodedString = Base64.getEncoder().encodeToString(fileContent);
                        String responseString = gson.toJson(encodedString);
                        return new ResponseEntity<>(responseString, OK);
                    } catch (IOException e) {
                        return new ResponseEntity<>("Error Base64 encode", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }
                else {
                    Access access = accessRepository.findByUsernameAndFilename(username.getUsername(), filename);
                    if (access != null) {
                        if (Integer.parseInt(access.getAccess()) >= 1) {
                            try {
                                byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                                String responseString = gson.toJson(encodedString);
                                return new ResponseEntity<>(responseString, OK);
                            } catch (IOException e) {
                                return new ResponseEntity<>("Error Base64 encode", HttpStatus.UNPROCESSABLE_ENTITY);
                            }
                        }
                        else {
                            return new ResponseEntity<>("Access denied", FORBIDDEN);
                        }
                    }
                    else {
                        return new ResponseEntity<>("Access denied", FORBIDDEN);
                    }
                }

            }
            else {
                return new ResponseEntity<>("User does not found", NOT_FOUND);
            }
        }
        else {
            return new ResponseEntity<>("File does not found", NOT_FOUND);
        }

    }

}
