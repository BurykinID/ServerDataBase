package com.example.demo.controller.file;

import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.file.FileJson;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class FilterFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FilterFileController (FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "listFile/{tag}")
    public String getFileWithTag(@AuthenticationPrincipal User user,
                                 @PathVariable("tag") String tag) {
        String responseString = null;
        Gson gson = new Gson();
        Response response = new Response();

        ArrayList<File> allFile = fileRepository.findAll();

        ArrayList<FileJson> fileJson = new ArrayList<>();

        ArrayList<File> allFileWithPermission = new ArrayList<>();

        for (File file : allFile) {
            for (String accessUser : file.getAccessList()) {
                if (accessUser.equals(user.getUsername())) {
                    allFileWithPermission.add(file);
                    break;
                }
            }
        }

        if (allFileWithPermission != null && allFileWithPermission.size() > 0) {
            for (File file : allFileWithPermission) {

                for (String tagInFile : file.getTag()) {
                    if (tagInFile.equals(tag)) {
                        fileJson.add(new FileJson(file.getAuthor(), file.getEditor(), file.getDate(), file.getTag()));
                        break;
                    }
                }

            }

            if (fileJson != null && fileJson.size() > 0) {
                responseString = gson.toJson(fileJson);
                return responseString;
            }
            else {
                response.setStatus("error");
                response.setDescription("Files with this tag does not found");
                responseString = gson.toJson(response);
                return responseString;
            }
        }
        else {
            response.setStatus("error");
            response.setDescription("You have not file with this tag and permission");
            return responseString;
        }



    }

}
