package com.example.demo.controller.file;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.file.Get.FileForGet;
import com.example.demo.forJsonObject.file.Get.FileForGetVersion;
import com.example.demo.forJsonObject.file.Get.ReturnFile;
import com.example.demo.forJsonObject.file.Update.FileForUpdate;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwt;
import org.apache.commons.io.FileUtils;
import org.hibernate.validator.internal.metadata.aggregated.rule.OverridingMethodMustNotAlterParameterConstraints;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

import static com.example.demo.role.Role.ADMIN;
import static org.springframework.http.HttpStatus.*;

@RestController
public class GetFileController {

    private final FileRepository fileRepository;
    private final AccessRepository accessRepository;
    private final UserRepository userRepository;
    private final JwtToken jwtToken;

    public GetFileController (FileRepository fileRepository, AccessRepository accessRepository, UserRepository userRepository, JwtToken jwtToken) {
        this.fileRepository = fileRepository;
        this.accessRepository = accessRepository;
        this.userRepository = userRepository;
        this.jwtToken = jwtToken;
    }

    @PostMapping (value = {"/getfile"})
    public ResponseEntity getFile(@RequestHeader ("Authorization") String token,
                                  @RequestBody FileForGet fileforGet) {

        ArrayList<File> result = fileRepository.findByFilenameAndAuthor(fileforGet.getFilename(), fileforGet.getAuthor(), Sort.by("date").descending());

        File file = fileRepository.findById(result.get(0).getId());
        String username = jwtToken.getUsernameFromToken(token.substring(7));
        User user = userRepository.findByUsername(username);
        Gson gson = new Gson();

        if (file != null) {
            if (user != null) {

                boolean isAdmin = user.getRoles().contains(ADMIN);

                if (isAdmin) {
                    try {
                        byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                        ReturnFile returnFile = new ReturnFile();
                        returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                        returnFile.setVersion(String.valueOf(result.size()));

                        String responseString = gson.toJson(returnFile);
                        return new ResponseEntity<>(responseString, OK);
                    } catch (IOException e) {
                        return new ResponseEntity<>("Error Base64 encode", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }
                else {
                    Access access = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));
                    if (access != null) {
                        if (Integer.parseInt(access.getAccess()) >= 1) {
                            try {
                                byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));

                                ReturnFile returnFile = new ReturnFile();
                                returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                                returnFile.setVersion(String.valueOf(result.size()));

                                String responseString = gson.toJson(returnFile);
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

    @PostMapping (value = {"/getfile/version"})
    public ResponseEntity getFileVersion(@RequestHeader ("Authorization") String token,
                                  @RequestBody FileForGetVersion fileForGetVersion) {

        ArrayList<File> result = fileRepository.findByFilenameAndAuthor(fileForGetVersion.getFilename(), fileForGetVersion.getAuthor(), Sort.by("date").descending());

        File file = fileRepository.findById(result.get(Integer.parseInt(fileForGetVersion.getVersion())-1).getId());
        String username = jwtToken.getUsernameFromToken(token.substring(7));
        User user = userRepository.findByUsername(username);
        Gson gson = new Gson();

        if (file != null) {
            if (user != null) {

                boolean isAdmin = user.getRoles().contains(ADMIN);

                if (isAdmin) {
                    try {
                        byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                        ReturnFile returnFile = new ReturnFile();
                        returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                        returnFile.setVersion(String.valueOf(result.size()));

                        String responseString = gson.toJson(returnFile);
                        return new ResponseEntity<>(responseString, OK);
                    } catch (IOException e) {
                        return new ResponseEntity<>("Error Base64 encode", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }
                else {
                    Access access = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));
                    if (access != null) {
                        if (Integer.parseInt(access.getAccess()) >= 1) {
                            try {
                                byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));

                                ReturnFile returnFile = new ReturnFile();
                                returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                                returnFile.setVersion(String.valueOf(result.size()));

                                String responseString = gson.toJson(returnFile);
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

    @PostMapping(value = "/file/{id}")
    public ResponseEntity getFileForId(@RequestHeader ("Authorization") String token,
                                       @PathVariable ("id") String idFile) {

        User user = userRepository.findByUsername(jwtToken.getUsernameFromToken(token.substring(7)));
        Gson gson = new Gson();

        if (user != null) {

            File file = fileRepository.findById(UUID.fromString(idFile));

            if (file != null) {

                boolean isAdmin = user.getRoles().contains(ADMIN);

                if (isAdmin) {
                    try {
                        byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                        ReturnFile returnFile = new ReturnFile();
                        returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));

                        String responseString = gson.toJson(returnFile);
                        return new ResponseEntity<>(responseString, OK);
                    } catch (IOException e) {
                        return new ResponseEntity<>("Error Base64 encode", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }
                else {

                    Access access = accessRepository.findByUsernameAndIdFile(user.getUsername(), idFile);
                    if (access != null) {

                        if (Integer.parseInt(access.getAccess()) > 1) {

                            try {
                                byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                                ReturnFile returnFile = new ReturnFile();
                                returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));

                                String responseString = gson.toJson(returnFile);
                                return new ResponseEntity<>(responseString, OK);
                            } catch (IOException e) {
                                return new ResponseEntity<>("Error Base64 encode", HttpStatus.UNPROCESSABLE_ENTITY);
                            }

                        }

                        return new ResponseEntity("Access denied", FORBIDDEN);

                    }
                    return new ResponseEntity("Access denied", FORBIDDEN);


                }

            }

        }

        return new ResponseEntity("User does not found", NOT_FOUND);
    }

}
