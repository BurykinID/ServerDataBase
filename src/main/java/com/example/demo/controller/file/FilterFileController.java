package com.example.demo.controller.file;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.file.FileJsonOutput;
import com.example.demo.forJsonObject.file.filter.Filter;
import com.example.demo.forJsonObject.file.forUpload.Tag;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static com.example.demo.role.Role.ADMIN;

@RestController
public class FilterFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final AccessRepository accessRepository;
    private final JwtToken jwtToken;

    public FilterFileController (FileRepository fileRepository, UserRepository userRepository, AccessRepository accessRepository, JwtToken jwtToken) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
        this.jwtToken = jwtToken;
    }

    @PostMapping (value = "listFile/filter")
    public ResponseEntity getFileWithTag(@RequestHeader("Authorization") String token,
                                         @RequestBody Filter filter) {
        String responseString = null;
        Gson gson = new Gson();

        String username = jwtToken.getUsernameFromToken(token.substring(7));

        User user = userRepository.findByUsername(username);

        if (user != null) {
            ArrayList<File> allFile = fileRepository.findAll(Sort.by("date").descending());
            ArrayList<FileJsonOutput> fileJson = new ArrayList<>();
            ArrayList<File> allFileWithPermission = new ArrayList<>();

            if (user.getRoles().contains(ADMIN)) {
                allFileWithPermission.addAll(allFile);
            }
            else {
                for (File file : allFile) {
                    Access access = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));
                    if (access != null) {
                        allFileWithPermission.add(file);
                    }
                }
            }

            if (allFileWithPermission.size() > 0) {
                for (File file : allFileWithPermission) {

                    for (Tag tag : filter.getTag())

                    if (file.getTag().contains(tag.getTag())) {

                        //проверка на то, что файлов с таким именем и автором уже нет
                        if (!isExist(fileJson, file)) {
                            fileJson.add(new FileJsonOutput(String.valueOf(file.getId()),file.getFilename(), file.getAuthor(), file.getEditor(), file.getDate(), file.getTag()));
                            break;
                        }

                    }

                }
                if (fileJson.size() > 0) {
                    responseString = gson.toJson(fileJson);
                    return new ResponseEntity<>(responseString, HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>("File with this tag does not found", HttpStatus.NOT_FOUND);
                }
            }
            else {
                return new ResponseEntity<>("File with this tag and permission does not found", HttpStatus.NOT_FOUND);
            }
        }
        else {
            return new ResponseEntity<>("User does not found", HttpStatus.NOT_FOUND);
        }

    }

    public boolean isExist(ArrayList<FileJsonOutput> fileJson, File file) {

        if (fileJson != null) {
            for (FileJsonOutput fileJsonOutput : fileJson) {
                if (fileJsonOutput.getAuthor().equals(file.getAuthor()) && fileJsonOutput.getFilename().equals(file.getFilename())) {
                    return true;
                }
            }
        }

        return false;

    }


}
