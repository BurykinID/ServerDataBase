package com.example.demo.controller.file;

import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.file.FileJsonOutput;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static com.example.demo.role.Role.ADMIN;

@RestController
public class FilterFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final AccessRepository accessRepository;

    public FilterFileController (FileRepository fileRepository, UserRepository userRepository, AccessRepository accessRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
    }

    @GetMapping(value = "listFile/{tag}")
    public String getFileWithTag(@RequestBody Username username,//@AuthenticationPrincipal User user,
                                 @PathVariable("tag") String tag) {
        String responseString = null;
        Gson gson = new Gson();
        Response response = new Response();

        User user = userRepository.findByUsername(username.getUsername());

        if (user != null) {
            ArrayList<File> allFile = fileRepository.findAll();
            ArrayList<FileJsonOutput> fileJson = new ArrayList<>();
            ArrayList<File> allFileWithPermission = new ArrayList<>();

            if (user.getRoles().contains(ADMIN)) {
                allFileWithPermission.addAll(allFile);
            }
            else {
                String usernameStr = username.getUsername();
                for (File file : allFile) {
                    Access access = accessRepository.findByUsernameAndFilename(usernameStr, file.getFilename());
                    if (access != null) {
                        allFileWithPermission.add(file);
                    }
                }
            }

            if (allFileWithPermission != null && allFileWithPermission.size() > 0) {
                for (File file : allFileWithPermission) {

                    if (file.getTag().contains(tag)) {
                        fileJson.add(new FileJsonOutput(file.getFilename(), file.getAuthor(), file.getEditor(), file.getDate(), file.getTag()));
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
        else {
            response.setStatus("error");
            response.setDescription("Пользователь с таким именем не найден");
            return responseString;
        }

    }

}
