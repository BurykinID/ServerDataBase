package com.example.demo.controller.file;

import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.form.FileForm;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class SharingFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

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

    @PostMapping(value = "/file/{filename}")
    public String updateAccessList( @AuthenticationPrincipal User user,
                                    @PathVariable("filename") String filename,
                                    @RequestParam String accessList) {

        File file = fileRepository.findByFilename(filename);

        String author = user.getUsername();

        if (author.equals(file.getAuthor())) {

            ArrayList<String> newAccessList = file.getAccessList();
            String[] updAccessList = accessList.split(", ");
            boolean isExists = false;
            if (updAccessList.length > 0) {

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

            }


            file.setAccessList(newAccessList);
            fileRepository.save(file);

        }

        return "redirect:/listFile";
    }


}
