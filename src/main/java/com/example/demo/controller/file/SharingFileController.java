package com.example.demo.controller.file;

import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.file.AccessAnwerUser;
import com.example.demo.forJsonObject.Response;
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
                        Set<Role> roles = user.getRoles();
                        if (roles.contains(Role.ADMIN)) {
                            isAdmin = true;
                        }
                    }

                    if (isAuthor || isAdmin) {
                        // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                        ArrayList<Username> usernameForShare = userAccess.getUsernameForShare();

                        if (usernameForShare != null) {
                            for (int i = 0; i < usernameForShare.size(); i++) {

                                User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                // проверка на то, что пользователь из списка вообще существует
                                if (newUser != null) {
                                    // если пользователя нет в списке тех, кому разрешён доступ, то его вносят в список доступа.
                                    Access access = accessRepository.findByUsernameAndFilename(newUser.getUsername(), filename);

                                    if (access != null) {
                                        int lvlAccess = Integer.parseInt(access.getAccess());
                                        if (lvlAccess < 1)
                                            access.setAccess("1");
                                    }
                                    else {
                                        String lvlAccess = "1";
                                        access = new Access(file.getFilename(), newUser.getUsername(), lvlAccess);
                                    }
                                    accessRepository.save(access);

                                    // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                    responseWithoutError.add(usernameForShare.get(i)) ;

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
                            response.setStatus("error");
                            response.setDescription("userlist is empty");
                            String responseString = gson.toJson(response);
                            return responseString;
                        }

                    }
                    else {
                        response.setStatus("error");
                        response.setDescription("not enough right");
                        String responseString = gson.toJson(response);
                        return responseString;
                    }
                }
                else {
                    response.setStatus("error");
                    response.setDescription("File does not exists");
                    String responseString = gson.toJson(response);
                    return responseString;
                }
            }
            else if (file != null) {
                response.setStatus("error");
                response.setDescription("Username does not found.");
                String responseString = gson.toJson(response);
                return responseString;
            }
            else {
                response.setStatus("error");
                response.setDescription("Username and file do not found.");
                String responseString = gson.toJson(response);
                return responseString;
            }

        }
        else {
            response.setStatus("error");
            response.setDescription("Я не получил никакой информации");
            String responseString = gson.toJson(response);
            return responseString;
        }

    }


    /*@PostMapping(value = "/file/write/{filename}")
    public String updAccessListWithJson(@ResponseBody ArrayUsers arrayUsers) {
        return "";
    }

    @PostMapping(value = "/file/delete/{filename}")
    public String updAccessListWithJson(@ResponseBody ArrayUsers arrayUsers) {
        return "";
    }*/

}
