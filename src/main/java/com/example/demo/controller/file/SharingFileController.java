package com.example.demo.controller.file;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.file.AccessAnwerUser;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.user.ArrayUsers;
import com.example.demo.forJsonObject.user.UserAccess;
import com.example.demo.forJsonObject.user.UserAccessAll;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.form.FileForm;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.google.gson.Gson;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

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

    // по id
    @PostMapping(value = "/file/read/{id}")
    public ResponseEntity updReadForFile(@RequestHeader("Authorization") String token,
                                              @RequestBody UserAccess userAccess,
                                              @PathVariable("id") String id) {
        return updReadPermit(userAccess, id, "1", token);
    }

    @PostMapping(value = "/file/write/{id}")
    public ResponseEntity updWriteForFile(@RequestHeader("Authorization") String token,
                                               @RequestBody UserAccess userAccess,
                                               @PathVariable("id") String id) {
        return updPermit(userAccess, id, "2", token);
    }

    @PostMapping(value = "/file/delete/{id}")
    public ResponseEntity updDeleteForFile(@RequestHeader("Authorization") String token,
                                                @RequestBody UserAccess userAccess,
                                                @PathVariable("id") String id) {
        return updPermit(userAccess, id, "3", token);
    }

    @PostMapping(value = "/file/drop/read/{id}")
    public ResponseEntity dropReadForFile(@RequestHeader("Authorization") String token,
                                              @RequestBody UserAccess userAccess,
                                              @PathVariable("id") String id) {
        return deletePermit(userAccess, id, "0", token);
    }

    @PostMapping(value = "/file/drop/write/{id}")
    public ResponseEntity dropWriteForFile(@RequestHeader("Authorization") String token,
                                               @RequestBody UserAccess userAccess,
                                               @PathVariable("id") String id) {
        return deletePermit(userAccess, id, "1", token);
    }

    @PostMapping(value = "/file/drop/delete/{id}")
    public ResponseEntity dropDeleteForFile(@RequestHeader("Authorization") String token,
                                               @RequestBody UserAccess userAccess,
                                               @PathVariable("id") String id) {
        return deletePermit(userAccess, id, "2", token);
    }

    // по filename

    @PostMapping(value = "/file/read/all/{filename}")
    public ResponseEntity updAllReadForFile(@RequestHeader("Authorization") String token,
                                              @RequestBody UserAccessAll userAccessAll,
                                              @PathVariable("filename") String filename) {
        return updAllReadPermit(userAccessAll, filename, "1", token);
    }

    @PostMapping(value = "/file/write/all/{filename}")
    public ResponseEntity updAllWriteForFile(@RequestHeader("Authorization") String token,
                                                 @RequestBody UserAccessAll userAccessAll,
                                                 @PathVariable("filename") String filename) {
        return updAllPermit(userAccessAll, filename, "2", token);
    }

    @PostMapping(value = "/file/delete/all/{filename}")
    public ResponseEntity updAllDeleteForFile(@RequestHeader("Authorization") String token,
                                                 @RequestBody UserAccessAll userAccessAll,
                                                 @PathVariable("filename") String filename) {
        return updAllPermit(userAccessAll, filename, "3", token);
    }

    @PostMapping(value = "/file/drop/read/all/{filename}")
    public ResponseEntity dropAllReadForFile(@RequestHeader("Authorization") String token,
                                                 @RequestBody UserAccessAll userAccessAll,
                                                 @PathVariable("filename") String filename) {
        return deleteAllPermit(userAccessAll, filename, "0", token);
    }

    @PostMapping(value = "/file/drop/write/all/{filename}")
    public ResponseEntity dropAllWriteForFile(@RequestHeader("Authorization") String token,
                                                 @RequestBody UserAccessAll userAccessAll,
                                                 @PathVariable("filename") String filename) {
        return deleteAllPermit(userAccessAll, filename, "1", token);
    }

    @PostMapping(value = "/file/drop/delete/all/{filename}")
    public ResponseEntity dropAllDeleteForFile(@RequestHeader("Authorization") String token,
                                                 @RequestBody UserAccessAll userAccessAll,
                                                 @PathVariable("filename") String filename) {
        return deleteAllPermit(userAccessAll, filename, "2", token);
    }

    // для расшаривания по filename и author
    public ResponseEntity updAllReadPermit(UserAccessAll userAccessAll, String filename, String lvlAccessInput, String token) {

        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccessAll != null) {
            String tokenStr = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenStr);

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            ArrayList<File> files = fileRepository.findByFilenameAndAuthor(filename, userAccessAll.getAuthor(), Sort.by("date").ascending());

            if (user != null) {

                if (files .size() >= 1) {
                    for (File file : files) {

                        boolean isAuthor = file.getAuthor().equals(username);

                        if (!isAuthor) {
                            isAdmin = checkAdmin(username);
                        }

                        Access accessUser = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));

                        int lvlUser = Integer.parseInt(accessUser.getAccess());

                        if (isAuthor || isAdmin || lvlUser >= 1) {
                            // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                            ArrayList<Username> usernameForShare = userAccessAll.getUsernameForShare();

                            if (usernameForShare != null) {
                                for (int i = 0; i < usernameForShare.size(); i++) {

                                    User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                    // проверка на то, что пользователь из списка вообще существует
                                    if (newUser != null) {

                                        if (!checkAdmin(newUser.getUsername())) {

                                            Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                            if (access != null) {
                                                int lvlAccess = Integer.parseInt(access.getAccess());
                                                int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                                if (lvlAccess < lvlAccessInt)
                                                    access.setAccess(lvlAccessInput);
                                            }
                                            else {
                                                access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
                                            }
                                            accessRepository.save(access);
                                            // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
                                                responseWithoutError.add(usernameForShare.get(i));

                                        }
                                        else {
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
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

                            }
                            else {
                                return new ResponseEntity<>("List of user is empty", OK);
                            }
                        }
                        else {
                            return new ResponseEntity<>("Access denied", FORBIDDEN);
                        }

                }
                }

                else {
                    return new ResponseEntity<>("File does not found", NOT_FOUND);
                }

                return new ResponseEntity<>(gson.toJson(accessAnwerUser), OK);

            }
            else if (files != null) {
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

    public ResponseEntity updAllPermit(UserAccessAll userAccessAll, String filename, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccessAll != null) {
            String tokenStr = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenStr);

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            ArrayList<File> files = fileRepository.findByFilenameAndAuthor(filename, userAccessAll.getAuthor(), Sort.by("date").ascending());

            if (user != null) {
                if (files.size() >= 1) {

                    for (File file : files) {
                        boolean isAuthor = file.getAuthor().equals(username);

                        if (!isAuthor) {
                            isAdmin = checkAdmin(username);
                        }

                        if (isAuthor || isAdmin) {
                            // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                            ArrayList<Username> usernameForShare = userAccessAll.getUsernameForShare();

                            if (usernameForShare != null) {
                                for (int i = 0; i < usernameForShare.size(); i++) {

                                    User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                    // проверка на то, что пользователь из списка вообще существует
                                    if (newUser != null) {

                                        if (!checkAdmin(newUser.getUsername())) {

                                            Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                            if (access != null) {
                                                int lvlAccess = Integer.parseInt(access.getAccess());
                                                int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                                if (lvlAccess < lvlAccessInt)
                                                    access.setAccess(lvlAccessInput);
                                            }
                                            else {
                                                access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
                                            }
                                            accessRepository.save(access);
                                            // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
                                                responseWithoutError.add(usernameForShare.get(i));

                                        }
                                        else {
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
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

                            }
                            else {
                                return new ResponseEntity<>("List of user is empty", OK);
                            }
                        }
                        else {
                            return new ResponseEntity<>("Access denied", FORBIDDEN);
                        }
                    }

                    return new ResponseEntity<>(gson.toJson(accessAnwerUser), OK);

                }
                else {
                    return new ResponseEntity<>("File does not found", NOT_FOUND);
                }
            }
            else if (files != null) {
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

    public ResponseEntity deleteAllPermit(UserAccessAll userAccessAll, String filename, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccessAll != null) {
            String tokenStr = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenStr);

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            ArrayList<File> files = fileRepository.findByFilenameAndAuthor(filename, userAccessAll.getAuthor(), Sort.by("date").ascending());

            if (user != null) {
                if (files.size() >= 1) {

                    for (File file : files) {
                        boolean isAuthor = file.getAuthor().equals(username);

                        if (!isAuthor) {
                            isAdmin = checkAdmin(username);
                        }

                        if (isAuthor || isAdmin) {
                            // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                            ArrayList<Username> usernameForShare = userAccessAll.getUsernameForShare();

                            if (usernameForShare != null) {
                                for (int i = 0; i < usernameForShare.size(); i++) {

                                    User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                    // проверка на то, что пользователь из списка вообще существует
                                    if (newUser != null) {

                                        if (!checkAdmin(newUser.getUsername())) {

                                            Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                            if (access != null) {
                                                int lvlAccess = Integer.parseInt(access.getAccess());
                                                int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                                if (lvlAccess > lvlAccessInt)
                                                    access.setAccess(lvlAccessInput);
                                            }
                                            else {
                                                access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
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
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
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

                            }
                            else {
                                return new ResponseEntity<>("List of user is empty", OK);
                            }
                        }
                        else {
                            return new ResponseEntity<>("Access denied", FORBIDDEN);
                        }
                    }

                    return new ResponseEntity<>(gson.toJson(accessAnwerUser), OK);

                }
                else {
                    return new ResponseEntity<>("File does not found", NOT_FOUND);
                }
            }
            else if (files != null) {
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

    // для расшаривания по ID
    public ResponseEntity updPermit(UserAccess userAccess, String id, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {
            String tokenStr = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenStr);

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            File file = fileRepository.findById(UUID.fromString(id));

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

                                        Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                        if (access != null) {
                                            int lvlAccess = Integer.parseInt(access.getAccess());
                                            int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                            if (lvlAccess < lvlAccessInt)
                                                access.setAccess(lvlAccessInput);
                                        }
                                        else {

                                            access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);;
                                        }
                                        accessRepository.save(access);
                                        // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));

                                    }
                                    else {
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
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

    public ResponseEntity updReadPermit(UserAccess userAccess, String id, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {
            String tokenStr = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenStr);

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            File file = fileRepository.findById(UUID.fromString(id));

            if (user != null) {
                if (file != null) {
                    boolean isAuthor = file.getAuthor().equals(username);

                    if (!isAuthor) {
                        isAdmin = checkAdmin(username);
                    }

                    Access accessUser = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));

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

                                        Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                        if (access != null) {
                                            int lvlAccess = Integer.parseInt(access.getAccess());
                                            int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                            if (lvlAccess < lvlAccessInt)
                                                access.setAccess(lvlAccessInput);
                                        }
                                        else {
                                            access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
                                        }
                                        accessRepository.save(access);
                                        // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));

                                    }
                                    else {
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
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

    public ResponseEntity deletePermit(UserAccess userAccess, String id, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {
            String tokenStr = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenStr);

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            File file = fileRepository.findById(UUID.fromString(id));

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

                                        Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                        if (access != null) {
                                            int lvlAccess = Integer.parseInt(access.getAccess());
                                            int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                            if (lvlAccess > lvlAccessInt)
                                                access.setAccess(lvlAccessInput);
                                        }
                                        else {
                                            access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
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
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
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
