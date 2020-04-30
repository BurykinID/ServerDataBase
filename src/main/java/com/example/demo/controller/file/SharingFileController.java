package com.example.demo.controller.file;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static com.example.demo.role.Role.ADMIN;

@RestController
public class SharingFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final AccessRepository accessRepository;
    private ArrayList<String> errorUser;

    public SharingFileController(FileRepository fileRepository,
                                 UserRepository userRepository,
                                 AccessRepository accessRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
    }

    /*@GetMapping(value = "/file/{filename}")
    public String getFile(@PathVariable("filename") String filename,
                          Model model) {

        File file = fileRepository.findByFilename(filename);
        FileForm fileForm = new FileForm();

        model.addAttribute("file", file);
        model.addAttribute("fileForm", fileForm);

        return "access/insertUserInAccessList";
    }*/

    @PostMapping(value = "/file/read/{filename}")
    public String updReadListWithJson( @RequestBody UserAccess userAccess,
                                       @PathVariable("filename") String filename) {

        return updPermit(userAccess, filename, "1");

    }

    @PostMapping(value = "/file/write/{filename}")
    public String updWriteListWithJson(@RequestBody UserAccess userAccess,
                                       @PathVariable("filename") String filename) {

        return updPermit(userAccess, filename, "2");

    }

    @PostMapping(value = "/file/delete/{filename}")
    public String updDeleteListWithJson(@RequestBody UserAccess userAccess,
                                        @PathVariable("filename") String filename) {
        return updPermit(userAccess, filename, "3");
    }

    public String updPermit(UserAccess userAccess, String filename, String lvlAccessInput) {
        Gson gson = new Gson();
        Response response = new Response();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {
            String username = userAccess.getUsername();

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
                                        responseWithoutError.add(usernameForShare.get(i));
                                    }
                                    else {
                                        responseWithoutError.add(usernameForShare.get(i));
                                    }

                                }
                                else {
                                    responseWithError.add(usernameForShare.get(i));
                                }
                            }

                            accessAnwerUser.setUserWithError(responseWithError);
                            accessAnwerUser.setUserWithoutError(responseWithoutError);
                            String responseString = gson.toJson(accessAnwerUser);
                            return responseString;

                        }
                        else {
                            return printError("error", "Userlist пуст", response);
                        }
                    }
                    else {
                        return printError("error", "Не достаточно прав", response);
                    }
                }
                else {
                    return printError("error", "Файл не существует", response);
                }
            }
            else if (file != null) {
                return printError("error", "Пользователь не существуют", response);
            }
            else {
                return printError("error", "Пользователь и файл не существуют", response);
            }
        }
        else {
            return printError("error", "Я не получил никакой информации", response);
        }
    }

    public boolean checkAdmin(String username) {
        return userRepository.findByUsername(username).getRoles().contains(ADMIN);
    }

    public String printError(String status, String description, Response response) {

        Gson gson = new Gson();
        response.setStatus(status);
        response.setDescription(description);
        String responseString = gson.toJson(response);

        return responseString;

    }

}
