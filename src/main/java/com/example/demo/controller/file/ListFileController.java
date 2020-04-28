package com.example.demo.controller.file;

import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.repository.FileRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListFileController {

    private final FileRepository fileRepository;

    public ListFileController (FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @GetMapping (value = "/listFile")
    public String personList( @AuthenticationPrincipal User user,
                              Map<String, Object> model) {

        List<File> files = fileRepository.findAll();

        ArrayList<File> accessFiles = new ArrayList<>();

        String username = user.getUsername();

        for (File file: files) {

            ArrayList<String> accessList = file.getAccessList();

            for (String list : accessList) {
                if (list.equals(username)) {
                    accessFiles.add(file);
                    break;
                }
            }

            /*for (int i = 0; i < accessList.size(); i++) {
                if (accessList.get(i).get(username) != null) {
                    accessFiles.add(file);
                    break;
                }
            }*/

        }

        model.put("files", accessFiles);

        return "files/listFile";
    }


}
