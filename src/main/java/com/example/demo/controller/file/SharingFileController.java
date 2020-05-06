package com.example.demo.controller.file;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.file.AccessAnwerUser;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.user.ArrayUsers;
import com.example.demo.forJsonObject.user.UserAccess;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.form.FileForm;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static com.example.demo.role.Role.ADMIN;
import static org.springframework.http.HttpStatus.*;

@RestController
public class SharingFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final AccessRepository accessRepository;
    private final JwtToken jwtToken;

    public SharingFileController (FileRepository fileRepository, UserRepository userRepository, AccessRepository accessRepository, JwtToken jwtToken) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
        this.jwtToken = jwtToken;
    }

    @PostMapping(value = "/file/read/{filename}")
    public ResponseEntity updReadListWithJson(@RequestHeader("Authorization") String token,
                                              @RequestBody UserAccess userAccess,
                                              @PathVariable("filename") String filename) {
        return updReadPermit(userAccess, filename, "1", token);
    }

    @PostMapping(value = "/file/write/{filename}")
    public ResponseEntity updWriteListWithJson(@RequestHeader("Authorization") String token,
                                               @RequestBody UserAccess userAccess,
                                               @PathVariable("filename") String filename) {
        return updPermit(userAccess, filename, "2", token);
    }

    @PostMapping(value = "/file/delete/{filename}")
    public ResponseEntity updDeleteListWithJson(@RequestHeader("Authorization") String token,
                                                @RequestBody UserAccess userAccess,
                                                @PathVariable("filename") String filename) {
        return updPermit(userAccess, filename, "3", token);
    }

    @PostMapping(value = "/file/drop/read/{filename}")
    public ResponseEntity dropReadListWithJson(@RequestHeader("Authorization") String token,
                                              @RequestBody UserAccess userAccess,
                                              @PathVariable("filename") String filename) {
        return deletePermit(userAccess, filename, "0", token);
    }

    @PostMapping(value = "/file/drop/write/{filename}")
    public ResponseEntity dropWritedListWithJson(@RequestHeader("Authorization") String token,
                                               @RequestBody UserAccess userAccess,
                                               @PathVariable("filename") String filename) {
        return deletePermit(userAccess, filename, "1", token);
    }

    @PostMapping(value = "/file/drop/delete/{filename}")
    public ResponseEntity dropDeleteListWithJson(@RequestHeader("Authorization") String token,
                                               @RequestBody UserAccess userAccess,
                                               @PathVariable("filename") String filename) {
        return deletePermit(userAccess, filename, "2", token);
    }

    public ResponseEntity updPermit(UserAccess userAccess, String filename, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {
            String tokenStr = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenStr);

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            File file = fileRepository.findByFilename(filename);

            if (user != null) {
                if (file != null) {
                    boolean isAuthor = file.getAuthor().equals(username);

                    if (!isAuthor) {
                        isAdmin = checkAdmin(username);
                    }

                    if (isAuthor || isAdmin) {
                        // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                        ArrayList<Username> usernameForShare = userAccess.getUsernameForShare();

                        if (usernameForShare != null) {
                            for (int i = 0; i < usernameForShare.size(); i++) {

                                User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                // проверка на то, что пользователь из списка вообще существует
                                if (newUser != null) {

                                    if (!checkAdmin(newUser.getUsername())) {

                                        Access access = accessRepository.findByUsernameAndFilename(newUser.getUsername(), filename);

                                        if (access != null) {
                                            int lvlAccess = Integer.parseInt(access.getAccess());
                                            int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                            if (lvlAccess < lvlAccessInt)
                                                access.setAccess(lvlAccessInput);
                                        }
                                        else {
                                            access = new Access(file.getFilename(), newUser.getUsername(), lvlAccessInput);
                                        }
                                        accessRepository.save(access);
                                        // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));

                                    }
                                    else {
                                        if (responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));
                                    }

                                }
                                else {
                                    if (!responseWithError.contains(usernameForShare.get(i)))
                                        responseWithError.add(usernameForShare.get(i));
                                }
                            }

                            accessAnwerUser.setUserWithError(responseWithError);
                            accessAnwerUser.setUserWithoutError(responseWithoutError);

                            return new ResponseEntity<>(gson.toJson(accessAnwerUser), OK);

                        }
                        else {
                            return new ResponseEntity<>("List of user is empty", OK);
                        }
                    }
                    else {
                        return new ResponseEntity<>("Access denied", FORBIDDEN);
                    }
                }
                else {
                    return new ResponseEntity<>("File does not found", NOT_FOUND);
                }
            }
            else if (file != null) {
                return new ResponseEntity<>("User does not found", NOT_FOUND);
            }
            else {
                return new ResponseEntity<>("User and wile do not found", NOT_FOUND);
            }
        }
        else {
            return new ResponseEntity<>("Запрос пуст", OK);
        }
    }

    public ResponseEntity updReadPermit(UserAccess userAccess, String filename, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {
            String tokenStr = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenStr);

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            File file = fileRepository.findByFilename(filename);

            if (user != null) {
                if (file != null) {
                    boolean isAuthor = file.getAuthor().equals(username);

                    if (!isAuthor) {
                        isAdmin = checkAdmin(username);
                    }

                    Access accessUser = accessRepository.findByUsernameAndFilename(username, filename);

                    int lvlUser = Integer.parseInt(accessUser.getAccess());

                    if (isAuthor || isAdmin || lvlUser >= 1) {
                        // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                        ArrayList<Username> usernameForShare = userAccess.getUsernameForShare();

                        if (usernameForShare != null) {
                            for (int i = 0; i < usernameForShare.size(); i++) {

                                User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                // проверка на то, что пользователь из списка вообще существует
                                if (newUser != null) {

                                    if (!checkAdmin(newUser.getUsername())) {

                                        Access access = accessRepository.findByUsernameAndFilename(newUser.getUsername(), filename);

                                        if (access != null) {
                                            int lvlAccess = Integer.parseInt(access.getAccess());
                                            int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                            if (lvlAccess < lvlAccessInt)
                                                access.setAccess(lvlAccessInput);
                                        }
                                        else {
                                            access = new Access(file.getFilename(), newUser.getUsername(), lvlAccessInput);
                                        }
                                        accessRepository.save(access);
                                        // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));

                                    }
                                    else {
                                        if (responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));
                                    }

                                }
                                else {
                                    if (!responseWithError.contains(usernameForShare.get(i)))
                                        responseWithError.add(usernameForShare.get(i));
                                }
                            }

                            accessAnwerUser.setUserWithError(responseWithError);
                            accessAnwerUser.setUserWithoutError(responseWithoutError);

                            return new ResponseEntity<>(gson.toJson(accessAnwerUser), OK);

                        }
                        else {
                            return new ResponseEntity<>("List of user is empty", OK);
                        }
                    }
                    else {
                        return new ResponseEntity<>("Access denied", FORBIDDEN);
                    }
                }
                else {
                    return new ResponseEntity<>("File does not found", NOT_FOUND);
                }
            }
            else if (file != null) {
                return new ResponseEntity<>("User does not found", NOT_FOUND);
            }
            else {
                return new ResponseEntity<>("User and wile do not found", NOT_FOUND);
            }
        }
        else {
            return new ResponseEntity<>("Запрос пуст", OK);
        }
    }

    public ResponseEntity deletePermit(UserAccess userAccess, String filename, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {
            String tokenStr = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenStr);

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            File file = fileRepository.findByFilename(filename);

            if (user != null) {
                if (file != null) {
                    boolean isAuthor = file.getAuthor().equals(username);

                    if (!isAuthor) {
                        isAdmin = checkAdmin(username);
                    }

                    if (isAuthor || isAdmin) {
                        // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                        ArrayList<Username> usernameForShare = userAccess.getUsernameForShare();

                        if (usernameForShare != null) {
                            for (int i = 0; i < usernameForShare.size(); i++) {

                                User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                // проверка на то, что пользователь из списка вообще существует
                                if (newUser != null) {

                                    if (!checkAdmin(newUser.getUsername())) {

                                        Access access = accessRepository.findByUsernameAndFilename(newUser.getUsername(), filename);

                                        if (access != null) {
                                            int lvlAccess = Integer.parseInt(access.getAccess());
                                            int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                            if (lvlAccess > lvlAccessInt)
                                                access.setAccess(lvlAccessInput);
                                        }
                                        else {
                                            access = new Access(file.getFilename(), newUser.getUsername(), lvlAccessInput);
                                        }
                                        if (lvlAccessInput.equals("0"))
                                            accessRepository.delete(access);
                                        else
                                            accessRepository.save(access);
                                        // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));

                                    }
                                    else {
                                        if (responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));
                                    }

                                }
                                else {
                                    if (!responseWithError.contains(usernameForShare.get(i)))
                                        responseWithError.add(usernameForShare.get(i));
                                }
                            }

                            accessAnwerUser.setUserWithError(responseWithError);
                            accessAnwerUser.setUserWithoutError(responseWithoutError);

                            return new ResponseEntity<>(gson.toJson(accessAnwerUser), OK);

                        }
                        else {
                            return new ResponseEntity<>("List of user is empty", OK);
                        }
                    }
                    else {
                        return new ResponseEntity<>("Access denied", FORBIDDEN);
                    }
                }
                else {
                    return new ResponseEntity<>("File does not found", NOT_FOUND);
                }
            }
            else if (file != null) {
                return new ResponseEntity<>("User does not found", NOT_FOUND);
            }
            else {
                return new ResponseEntity<>("User and wile do not found", NOT_FOUND);
            }
        }
        else {
            return new ResponseEntity<>("Запрос пуст", OK);
        }

    }

    public boolean checkAdmin(String username) {
        return userRepository.findByUsername(username).getRoles().contains(ADMIN);
    }

}
