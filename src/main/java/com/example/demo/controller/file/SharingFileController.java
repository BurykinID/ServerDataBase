package com.example.demo.controller.file;

import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.ArrayUsers;
import com.example.demo.forJsonObject.ElObj.Rules;
import com.example.demo.form.FileForm;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.HashMap;

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



    /*@PostMapping(value = "/file/read/{filename}",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ArrayUsers updAccessListWithJson(@RequestBody ArrayUsers arrayUsers) {

        ArrayUsers arrayUsers1 = new ArrayUsers();


    }*/

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
