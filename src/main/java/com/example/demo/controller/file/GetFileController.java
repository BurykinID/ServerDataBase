package com.example.demo.controller.file;

import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Base64;

public class GetFileController {

    private final FileRepository fileRepository;
    private final AccessRepository accessRepository;
    private final UserRepository userRepository;

    public GetFileController (FileRepository fileRepository, AccessRepository accessRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.accessRepository = accessRepository;
        this.userRepository = userRepository;
    }

    // success, но надо обсудить
    @GetMapping (value = {"/getfile/{filename}"})
    public String getFile(@RequestBody Username username,
                          @PathVariable (name = "filename") String filename) {

        File file = fileRepository.findByFilename(filename);
        User user = userRepository.findByUsername(username.getUsername());
        // допилить ещё проверку на то, что у него есть доступ к данному файлу
        Gson gson = new Gson();
        Response response = new Response();

        if (file != null) {
            if (user != null) {
                Access access = accessRepository.findByUsernameAndFilename(username.getUsername(), filename);
                if (access != null) {
                    if (Integer.parseInt(access.getAccess()) >= 1) {
                        try {
                            byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                            String encodedString = Base64.getEncoder().encodeToString(fileContent);
                            String responseString = gson.toJson(encodedString);
                            return responseString;
                        } catch (IOException e) {
                            response.setStatus("error");
                            response.setDescription("error with encode Base 64");
                        }
                    }
                    else {
                        response.setStatus("error");
                        response.setDescription("У пользователя нет доступа к данному файлу");
                    }
                }
                else {
                    response.setStatus("error");
                    response.setDescription("У пользователя нет доступа к данному файлу");
                }
            }
            else {
                response.setStatus("error");
                response.setDescription("Такого пользователя не существует");
            }
        }
        else {
            response.setStatus("error");
            response.setDescription("Файла с таким именем не существует");
        }

        String responseString = gson.toJson(response);
        return responseString;

    }

    /*
    only for test with docker

    @GetMapping(value = {"/check"})
    public String checkFile() {

        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader("/var/lib/postgresql/data/aaaa.txt"));
            try {
                String s = null;
                while ((s = bufferedReader.readLine()) != null) {
                    System.out.println(s);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "check.html";
    }*/

}
