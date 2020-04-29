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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.role.Role.ADMIN;

@RestController
public class ListFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final AccessRepository accessRepository;

    public ListFileController (FileRepository fileRepository,
                               UserRepository userRepository,
                               AccessRepository accessRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
    }

    @GetMapping (value = "/listFile")
    public String getListFile(@RequestBody Username username) {

        User user = userRepository.findByUsername(username.getUsername());
        Gson gson = new Gson();
        Response response = new Response();

        if (user != null) {

            List<File> files = fileRepository.findAll();
            ArrayList<File> accessFiles = new ArrayList<>();
            String usernameStr = username.getUsername();

            if (user.getRoles().contains(ADMIN)) {
                accessFiles.addAll(files);
                ArrayList<FileJsonOutput> jsonFiles = new ArrayList<>();

                for (File file : accessFiles) {
                    jsonFiles.add(new FileJsonOutput(file.getFilename(), file.getAuthor(), file.getAuthor(), file.getDate(), file.getTag()));
                }

                String responseString = gson.toJson(jsonFiles);
                return responseString;
            }
            else {
                for (File file: files) {
                    Access access = accessRepository.findByUsernameAndFilename(usernameStr, file.getFilename());
                    if (access != null && (Integer.parseInt(access.getAccess()) >= 1)) {
                        accessFiles.add(file);
                    }
                }

                if (accessFiles != null && accessFiles.size() > 0) {

                    ArrayList<FileJsonOutput> jsonFiles = new ArrayList<>();

                    for (File file : accessFiles) {
                        jsonFiles.add(new FileJsonOutput(file.getFilename(), file.getAuthor(), file.getAuthor(), file.getDate(), file.getTag()));
                    }

                    String responseString = gson.toJson(jsonFiles);
                    return responseString;

                }
                else {
                    response.setStatus("error");
                    response.setStatus("Для данного пользователя нет доступных файлов");
                    String responseString = gson.toJson(response);
                    return responseString;
                }
            }




        }
        else {
            response.setStatus("error");
            response.setStatus("Пользователя с таким именем не существует");
            String responseString = gson.toJson(response);
            return responseString;
        }


    }

    /*@GetMapping (value = "/listFile")
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

            *//*for (int i = 0; i < accessList.size(); i++) {
                if (accessList.get(i).get(username) != null) {
                    accessFiles.add(file);
                    break;
                }
            }*//*

        }

        model.put("files", accessFiles);

        return "files/listFile";
    }*/


}
