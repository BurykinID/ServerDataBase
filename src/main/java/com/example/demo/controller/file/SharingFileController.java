package com.example.demo.controller.file;

import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.AccessAnwerUser;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.UserAccess;
import com.example.demo.forJsonObject.Username;
import com.example.demo.form.FileForm;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Set;

@Controller
public class SharingFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private ArrayList<String> errorUser;

    public SharingFileController(FileRepository fileRepository,
                                 UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/file/{filename}")
    public String getFile(@PathVariable("filename") String filename,
                          Model model) {

        File file = fileRepository.findByFilename(filename);
        FileForm fileForm = new FileForm();

        model.addAttribute("file", file);
        model.addAttribute("fileForm", fileForm);

        return "access/insertUserInAccessList";
    }

   /* @PostMapping(value = "/file/{filename}")
    public String updateAccessList( @AuthenticationPrincipal User user,
                                    @PathVariable("filename") String filename,
                                    @RequestParam(name = "read", required = false) String accessList,
                                    @RequestParam(name = "write", required = false) String writeList,
                                    @RequestParam(name = "delete", required = false) String deleteList) {

        // права доступа read, write, shared, delete.

        File file = fileRepository.findByFilename(filename);

        errorUser = new ArrayList<>();

        String author = user.getUsername();

        ArrayList<HashMap<String, Rules>> newAccessList = file.getAccessList();
        // список пользователей, которым разрешено читать



        if (accessList != null && accessList.length() > 0) {

            ArrayList<String> updAccessListUser = new ArrayList<>();

            try{
                String[] updAccessList = accessList.split(", ");
                updAccessListUser.addAll(updAccessList(updAccessList, author, updAccessListUser));
            }
            catch (NullPointerException exp) {
                updAccessListUser.addAll(updAccessString(accessList, updAccessListUser, author));
            }

            if (updAccessListUser.size() > 0)
                newAccessList.addAll(updAccessListUser);
        }
        // список пользователей, которым разрешено изменять
        if (writeList != null && writeList.length() > 0) {

            ArrayList<String> updAccessListUser = new ArrayList<>();

            try {
                String[] updWriteList = writeList.split(", ");
                updAccessListUser.addAll(updAccessList(updWriteList, author, updAccessListUser));
            }
            catch (NullPointerException e) {
                updAccessListUser.addAll(updAccessString(author, updAccessListUser, writeList));
            }

            if (updAccessListUser.size() > 0)
                newAccessList.addAll(updAccessListUser);

        }
        // список пользователей, которым разрешено удалять
        if (deleteList != null && deleteList.length() > 0) {

            ArrayList<String> updAccessListUser = new ArrayList<>();

            try{
                String[] updDeleteList  = deleteList.split(", ");
                updAccessList(updDeleteList, author, updAccessListUser);
            }
            catch (NullPointerException exp) {
                updAccessListUser = updAccessString(author, updAccessListUser, deleteList);
            }

            if (updAccessListUser.size() > 0)
                newAccessList.addAll(updAccessListUser);

        }

            *//*boolean isExists = false;

            if (updAccessList.length > 1) {

                for (String list : updAccessList) {

                    User findUser = userRepository.findByUsername(list);

                    if (findUser != null) {
                        if (!list.equals(author)) {
                            for (String exAccessList : newAccessList) {
                                if (list.equals(exAccessList)) {
                                    isExists = true;
                                    break;
                                }
                            }
                            if (!isExists) {
                                newAccessList.add(list);
                                isExists = false;
                            }

                        }

                    }
                    else {
                        System.out.println("пользователя нет");
                        return "redirect:/file/{filename}";
                    }

                }

            }
            else {

                User findUser = userRepository.findByUsername(accessList);

                if (findUser != null) {

                    if (!accessList.equals(author)) {
                        for (String exAccessList : newAccessList) {
                            if (accessList.equals(exAccessList)) {
                                isExists = true;
                                break;
                            }
                        }
                        if (!isExists) {
                            newAccessList.add(accessList);
                            isExists = false;
                        }

                    }

                }

                else {
                    System.out.println("пользователя нет");
                    return "redirect:/file/{filename}";
                }

            }*//*


            file.setAccessList(newAccessList);
            fileRepository.save(file);

        return "redirect:/listFile";
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

            Set<Role> roles = user.getRoles();
            for (Role role : roles) {
                if (role.equals(Role.ADMIN)) {
                    isAdmin = true;
                    break;
                }
            }
            if (file != null) {
                if (file.getAuthor().equals(username) || isAdmin) {

                    ArrayList<String> existsUserInAccessList = file.getAccessList();
                    // проверка того, что список пользователей, имеющих доступ к файлу уже существует
                    if (existsUserInAccessList != null) {
                        // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                        ArrayList<Username> usernameForShare = userAccess.getUsernameForShare();

                        boolean userInAccessList = false;

                        if (usernameForShare != null) {
                            for (int i = 0; i < usernameForShare.size(); i++) {

                                User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                // проверка на то, что пользователь из списка вообще существует
                                if (newUser != null) {

                                    for (String userInAccess : existsUserInAccessList) {
                                        // проверка на то, что пользователя нет в списке тех, кому разрешён доступ
                                        if (usernameForShare.get(i).getUsername().equals(userInAccess)) {
                                            userInAccessList = true;
                                            break;
                                        }
                                    }
                                    // если пользователя нет в списке тех, кому разрешён доступ, то его вносят в список доступа.
                                    if (!userInAccessList) {
                                        existsUserInAccessList.add(newUser.getUsername());
                                    }
                                    // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                    responseWithoutError.add(usernameForShare.get(i)) ;
                                    userInAccessList = false;

                                }

                                else {
                                    responseWithError.add(usernameForShare.get(i));
                                }

                            }
                        }
                        else {
                            response.setStatus("error");
                            response.setDescription("userlist is empty");
                            String responseString = gson.toJson(response);
                            return responseString;
                        }

                    }
                    else {

                        existsUserInAccessList = new ArrayList<>();
                        existsUserInAccessList.add(file.getAuthor());
                        for (User user1 : userRepository.findAll()) {
                            Set<Role> roleSet = user1.getRoles();
                            for (Role role : roleSet) {
                                if (role.equals(Role.ADMIN)) {
                                    existsUserInAccessList.add(user1.getUsername());
                                    break;
                                }
                            }
                        }

                        ArrayList<Username> usernameForShare = userAccess.getUsernameForShare();

                        boolean userInAccessList = false;

                        if (usernameForShare != null) {
                            for (int i = 0; i < usernameForShare.size(); i++) {

                                User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                // проверка на то, что пользователь из списка вообще существует
                                if (newUser != null) {

                                    for (String userInAccess : existsUserInAccessList) {
                                        // проверка на то, что пользователя нет в списке тех, кому разрешён доступ
                                        if (usernameForShare.get(i).getUsername().equals(userInAccess)) {
                                            userInAccessList = true;
                                            break;
                                        }
                                    }
                                    // если пользователя нет в списке тех, кому разрешён доступ, то его вносят в список доступа.
                                    if (!userInAccessList) {
                                        existsUserInAccessList.add(newUser.getUsername());
                                    }
                                    // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                    responseWithoutError.add(usernameForShare.get(i)) ;
                                    userInAccessList = false;

                                }

                                else {
                                    responseWithError.add(usernameForShare.get(i));
                                }

                            }
                        }
                        else {
                            response.setStatus("error");
                            response.setDescription("userlist is empty");
                            String responseString = gson.toJson(response);
                            return responseString;
                        }


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
                response.setDescription("file doesn't exists");
                String responseString = gson.toJson(response);
                return responseString;
            }


        }

        String responseString = gson.toJson(accessAnwerUser);

        return responseString;

    }

    /*@PostMapping(value = "/file/write/{filename}")
    public String updAccessListWithJson(@ResponseBody ArrayUsers arrayUsers) {
        return "";
    }

    @PostMapping(value = "/file/delete/{filename}")
    public String updAccessListWithJson(@ResponseBody ArrayUsers arrayUsers) {
        return "";
    }*/

    //когда 1 юзер
    public ArrayList<String> updAccessString(String author, ArrayList<String> newAccessList, String accessList) {

        boolean isExists = false;

        User findUser = userRepository.findByUsername(accessList);

        if (findUser != null) {

            if (!accessList.equals(author)) {
                for (String exAccessList : newAccessList) {
                    if (accessList.equals(exAccessList)) {
                        isExists = true;
                        break;
                    }
                }
                if (!isExists) {
                    newAccessList.add(accessList);
                }
            }

        }
        else {
            System.out.println("пользователя нет");
            errorUser.add(accessList);
        }

        return newAccessList;

    }

    //когда несколько юзеров
    public ArrayList<String> updAccessList(String[] updAccessList, String author, ArrayList<String> newAccessList ) {

        boolean isExists = false;

        for (String list : updAccessList) {

            User findUser = userRepository.findByUsername(list);

            if (findUser != null) {
                if (!list.equals(author)) {
                    for (String exAccessList : newAccessList) {
                        if (list.equals(exAccessList)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (!isExists) {
                        newAccessList.add(list);
                        isExists = false;
                    }

                }

            }
            else {
                System.out.println("пользователя нет");
                errorUser.add(list);
            }

        }

        return newAccessList;

    }


}
