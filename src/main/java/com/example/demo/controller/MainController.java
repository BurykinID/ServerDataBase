package com.example.demo.controller;

import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private FileRepository fileRepository;

    @GetMapping(value = { "/"})
    public String index(Model model) {
        return "index";
    }

    @GetMapping(value = "/listFile")
    public String personList( @AuthenticationPrincipal User user,
                              Map<String, Object> model) {
        List<File> files = fileRepository.findAll();

        ArrayList<File> accessFiles = new ArrayList<>();

        String username = user.getUsername();

        for (File file: files) {

            ArrayList<String> accessList = file.getAccessList();
            for (String accessUsername: accessList) {
                if (username.equals(accessUsername)) {
                    accessFiles.add(file);
                    break;
                }
            }

        }

        model.put("files", accessFiles);

        return "files/listFile";
    }

}
