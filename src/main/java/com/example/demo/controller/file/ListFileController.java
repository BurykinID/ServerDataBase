package com.example.demo.controller.file;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.Token;
import com.example.demo.forJsonObject.file.FileJsonOutput;
import com.example.demo.forJsonObject.file.forAccess.AccessFile;
import com.example.demo.forJsonObject.file.forAccess.Option;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.example.demo.role.Role.ADMIN;
import static org.springframework.http.HttpStatus.*;

@RestController
public class ListFileController {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final AccessRepository accessRepository;
    private final JwtToken jwtToken;

    public ListFileController (FileRepository fileRepository, UserRepository userRepository, AccessRepository accessRepository, JwtToken jwtToken) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
        this.jwtToken = jwtToken;
    }

    @GetMapping (value = "/listFile")
    public ResponseEntity getListFile(@RequestHeader ("Authorization") String token) {

        String username = jwtToken.getUsernameFromToken(token.substring(7));

        User user = userRepository.findByUsername(username);
        Gson gson = new Gson();

        if (user != null) {

            List<File> files = fileRepository.findAll();
            ArrayList<File> accessFiles = new ArrayList<>();

            if (user.getRoles().contains(ADMIN)) {

                for (File file: files) {

                    boolean isExists = false;
                    boolean isReplase = false;
                    int k = 0;

                    for (File existFile : accessFiles) {

                        if (existFile.getFilename().equals(file.getFilename()) &&
                                existFile.getAuthor().equals(file.getAuthor()) &&
                                (Long.parseLong(existFile.getDate()) < Long.parseLong(file.getDate()))) {
                            isReplase = true;
                            isExists = true;
                            break;
                        }
                        else if (existFile.getFilename().equals(file.getFilename()) &&
                                existFile.getAuthor().equals(file.getAuthor()) &&
                                (Long.parseLong(existFile.getDate()) >= Long.parseLong(file.getDate()))) {
                            isExists = true;
                            break;
                        }
                        k++;

                    }

                    if (isReplase) {
                        accessFiles.set(k, file);
                    }

                    if (!isExists)
                        accessFiles.add(file);

                }

                ArrayList<FileJsonOutput> jsonFiles = new ArrayList<>();

                for (File file : accessFiles) {
                    jsonFiles.add(new FileJsonOutput(String.valueOf(file.getId()),file.getFilename(), file.getAuthor(), file.getAuthor(), file.getDate(), file.getTag()));
                }

                String responseString = gson.toJson(jsonFiles);
                return new ResponseEntity<>(responseString, OK);
            }
            else {
                for (File file: files) {
                    Access access = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));
                    if (access != null && (Integer.parseInt(access.getAccess()) >= 1)) {

                        boolean isExists = false;
                        boolean isReplase = false;
                        int k = 0;

                        for (File existFile : accessFiles) {

                            if (existFile.getFilename().equals(file.getFilename()) &&
                                existFile.getAuthor().equals(file.getAuthor()) &&
                                (Long.parseLong(existFile.getDate()) < Long.parseLong(file.getDate()))) {
                                isReplase = true;
                                isExists = true;
                                break;
                            }
                            else if (existFile.getFilename().equals(file.getFilename()) &&
                                     existFile.getAuthor().equals(file.getAuthor()) &&
                                     (Long.parseLong(existFile.getDate()) >= Long.parseLong(file.getDate()))) {
                                isExists = true;
                                break;
                            }
                            k++;

                        }

                        if (isReplase) {
                            accessFiles.set(k, file);
                        }

                        if (!isExists)
                            accessFiles.add(file);
                    }
                }

                if (accessFiles != null && accessFiles.size() > 0) {

                    ArrayList<FileJsonOutput> jsonFiles = new ArrayList<>();

                    for (File file : accessFiles) {
                        jsonFiles.add(new FileJsonOutput(String.valueOf(file.getId()),file.getFilename(), file.getAuthor(), file.getAuthor(), file.getDate(), file.getTag()));
                    }

                    String responseString = gson.toJson(jsonFiles);
                    return new ResponseEntity<>(responseString, OK);

                }
                else {
                    // уточняю
                    return new ResponseEntity<>("", OK);
                }
            }

        }
        else {
            return new ResponseEntity<>("User does not found", NOT_FOUND);
        }

    }

    @GetMapping (value = "/listFile/forUser")
    public ResponseEntity getListFileForAccess (@RequestHeader ("Authorization") String token) {

        Gson gson = new Gson();

        User user = userRepository.findByUsername(jwtToken.getUsernameFromToken(token.substring(7)));

        if (user != null) {

            boolean isAdmin = user.getRoles().contains(ADMIN);

            if (isAdmin) {

                ArrayList<AccessFile> accessFileList = new ArrayList<>();

                for (File file : fileRepository.findAll()) {
                    ArrayList<Option> optionList = new ArrayList<>();
                    optionList.add(new Option("read"));
                    optionList.add(new Option("write"));
                    optionList.add(new Option("delete"));

                    accessFileList.add(new AccessFile(String.valueOf(file.getId()), file.getFilename(), optionList, file.getTag()));

                }

                return new ResponseEntity(gson.toJson(accessFileList), OK);

            }
            else {
                ArrayList<Access> accessList = accessRepository.findByUsername(user.getUsername());

                if (accessList != null) {

                    ArrayList<String> idFileList = new ArrayList<>();
                    for (int i = 0; i < accessList.size(); i++)
                        idFileList.add(accessList.get(i).getIdFile());

                    ArrayList<File> fileList = new ArrayList<>();
                    HashMap<String, String> accessCollection = new HashMap<>();

                    for (String idFile : idFileList) {
                        fileList.add(fileRepository.findById(UUID.fromString(idFile)));
                        accessCollection.put(idFile, accessRepository.findByUsernameAndIdFile(user.getUsername(), idFile).getAccess());
                    }

                    if (fileList.size() > 0) {

                        ArrayList<AccessFile> accessFileList = new ArrayList<>();

                        for (File file : fileList) {

                            ArrayList<Option> optionList = new ArrayList<>();

                            if (Integer.parseInt(accessCollection.get(String.valueOf(file.getId()))) == 1) {
                                optionList.add(new Option("read"));
                            }
                            else if (Integer.parseInt(accessCollection.get(String.valueOf(file.getId()))) == 2) {
                                optionList.add(new Option("read"));
                                optionList.add(new Option("write"));
                            }
                            else {
                                optionList.add(new Option("read"));
                                optionList.add(new Option("write"));
                                optionList.add(new Option("delete"));
                            }

                            accessFileList.add(new AccessFile(String.valueOf(file.getId()), file.getFilename(), optionList, file.getTag()));
                        }

                        return new ResponseEntity(gson.toJson(accessFileList), OK);

                    }

                    return new ResponseEntity("User has not file with access", FORBIDDEN);

                }

                return new ResponseEntity("User has not file with access", FORBIDDEN);

            }





        }

        return new ResponseEntity("User does not found", FORBIDDEN);

    }

    @GetMapping (value = "/file/{id}")
    public ResponseEntity getAccessForFile (@RequestHeader ("Authorization") String token,
                                            @PathVariable ("id") String idFile) {

        Gson gson = new Gson();

        User user = userRepository.findByUsername(jwtToken.getUsernameFromToken(token.substring(7)));

        if (user != null) {

            File file = fileRepository.findById(UUID.fromString(idFile));

            boolean isAdmin = user.getRoles().contains(ADMIN);



            if (isAdmin) {
                ArrayList<Option> optionList = new ArrayList<>();
                optionList.add(new Option("read"));
                optionList.add(new Option("write"));
                optionList.add(new Option("delete"));
                AccessFile accessFile = new AccessFile(String.valueOf(file.getId()), file.getFilename(), optionList, file.getTag());

                return new ResponseEntity(gson.toJson(accessFile), OK);

            }
            else {

                Access access = accessRepository.findByUsernameAndIdFile(user.getUsername(), idFile);
                if (access != null) {
                    ArrayList<Option> optionList = new ArrayList<>();

                    if (Integer.parseInt(access.getAccess()) == 1) {
                        optionList.add(new Option("read"));
                    }
                    else if (Integer.parseInt(access.getAccess()) == 2) {
                        optionList.add(new Option("read"));
                        optionList.add(new Option("write"));
                    }
                    else {
                        optionList.add(new Option("read"));
                        optionList.add(new Option("write"));
                        optionList.add(new Option("delete"));
                    }

                    AccessFile accessFile = new AccessFile(String.valueOf(file.getId()), file.getFilename(), optionList, file.getTag());

                    return new ResponseEntity(gson.toJson(accessFile), OK);
                }

                return new ResponseEntity("Access denied", FORBIDDEN);

            }

        }

        return new ResponseEntity("User does not found", FORBIDDEN);

    }

}