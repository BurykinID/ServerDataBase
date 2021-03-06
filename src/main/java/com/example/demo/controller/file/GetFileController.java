package com.example.demo.controller.file;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.file.FileJsonOutput;
import com.example.demo.forJsonObject.file.Get.FileForGet;
import com.example.demo.forJsonObject.file.Get.FileForGetVersion;
import com.example.demo.forJsonObject.file.Get.ReturnFile;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

import static com.example.demo.role.Role.ADMIN;
import static org.springframework.http.HttpStatus.*;

@RestController
public class GetFileController {

    private final FileRepository fileRepository;
    private final AccessRepository accessRepository;
    private final UserRepository userRepository;
    private final JwtToken jwtToken;

    public GetFileController (FileRepository fileRepository, AccessRepository accessRepository, UserRepository userRepository, JwtToken jwtToken) {
        this.fileRepository = fileRepository;
        this.accessRepository = accessRepository;
        this.userRepository = userRepository;
        this.jwtToken = jwtToken;
    }

    @PostMapping (value = {"/getfile"})
    public ResponseEntity getFile(@RequestHeader ("Authorization") String token,
                                                    @RequestBody FileForGet fileforGet) {

        ArrayList<File> result = fileRepository.findByFilenameAndAuthor(fileforGet.getFilename(), fileforGet.getAuthor(), Sort.by("date").descending());

        try{
            File file = fileRepository.findById(result.get(0).getId());

            String username = jwtToken.getUsernameFromToken(token.substring(7));

            User user = userRepository.findByUsername(username);
            Gson gson = new Gson();

            if (file != null) {
                if (user != null) {

                    boolean isAdmin = user.getRoles().contains(ADMIN);

                    if (isAdmin) {

                        byte[]fileContent = new byte[0];
                        try {
                            fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                            if (fileContent != null) {
                                ReturnFile returnFile = new ReturnFile();
                                returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                                returnFile.setVersion(String.valueOf(result.size()));

                                String responseString = gson.toJson(returnFile);
                                return new ResponseEntity<>(responseString, HttpStatus.OK);
                            } else {

                                return new ResponseEntity<>("File does not found", NOT_FOUND);
                            }

                        } catch (IOException e) {
                            return new ResponseEntity<>("File does not found", HttpStatus.UNPROCESSABLE_ENTITY);
                        }

                    }
                    else {
                        Access access = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));
                        if (access != null) {
                            if (Integer.parseInt(access.getAccess()) >= 1) {

                                byte[]fileContent = new byte[0];
                                try {
                                    fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                                    if (fileContent != null) {
                                        ReturnFile returnFile = new ReturnFile();
                                        returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                                        returnFile.setVersion(String.valueOf(result.size()));

                                        String responseString = gson.toJson(returnFile);
                                        return new ResponseEntity<>(responseString, HttpStatus.OK);
                                    } else {

                                        return new ResponseEntity<>("File does not found", NOT_FOUND);
                                    }
                                } catch (IOException e) {
                                    return new ResponseEntity<>("File does not found", HttpStatus.UNPROCESSABLE_ENTITY);
                                }
                            }
                            else {
                                return new ResponseEntity<>("Access denied", FORBIDDEN);
                            }
                        }
                        else {
                            return new ResponseEntity<>("Access denied", FORBIDDEN);
                        }
                    }

                }
                else {
                    return new ResponseEntity<>("User does not found", NOT_FOUND);
                }
            }
            else {
                return new ResponseEntity<>("File does not found", NOT_FOUND);
            }
        } catch (IndexOutOfBoundsException e) {
            return new ResponseEntity<>("File does not found", NOT_FOUND);
        }



    }

    @PostMapping (value = {"/getfile/version"})
    public ResponseEntity getFileVersion(@RequestHeader ("Authorization") String token,
                                  @RequestBody FileForGetVersion fileForGetVersion) {

        ArrayList<File> result = fileRepository.findByFilenameAndAuthor(fileForGetVersion.getFilename(), fileForGetVersion.getAuthor(), Sort.by("date").ascending());

        File file = fileRepository.findById(result.get(Integer.parseInt(fileForGetVersion.getVersion())-1).getId());
        String username = jwtToken.getUsernameFromToken(token.substring(7));
        User user = userRepository.findByUsername(username);
        Gson gson = new Gson();

        if (file != null) {
            if (user != null) {

                boolean isAdmin = user.getRoles().contains(ADMIN);

                if (isAdmin) {
                    try {
                        byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                        ReturnFile returnFile = new ReturnFile();
                        returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                        returnFile.setVersion(String.valueOf(result.size()));

                        String responseString = gson.toJson(returnFile);
                        return new ResponseEntity<>(responseString, OK);
                    } catch (IOException e) {
                        return new ResponseEntity<>("File does not found", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }
                else {
                    Access access = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));
                    if (access != null) {
                        if (Integer.parseInt(access.getAccess()) >= 1) {
                            try {
                                byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));

                                ReturnFile returnFile = new ReturnFile();
                                returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                                returnFile.setVersion(String.valueOf(result.size()));

                                String responseString = gson.toJson(returnFile);
                                return new ResponseEntity<>(responseString, OK);
                            } catch (IOException e) {
                                return new ResponseEntity<>("File does not found", HttpStatus.UNPROCESSABLE_ENTITY);
                            }
                        }
                        else {
                            return new ResponseEntity<>("Access denied", FORBIDDEN);
                        }
                    }
                    else {
                        return new ResponseEntity<>("Access denied", FORBIDDEN);
                    }
                }

            }
            else {
                return new ResponseEntity<>("User does not found", NOT_FOUND);
            }
        }
        else {
            return new ResponseEntity<>("File does not found", NOT_FOUND);
        }

    }

    @PostMapping(value = "/file/{id}")
    public ResponseEntity getFileForId(@RequestHeader ("Authorization") String token,
                                       @PathVariable ("id") String idFile) {

        User user = userRepository.findByUsername(jwtToken.getUsernameFromToken(token.substring(7)));
        Gson gson = new Gson();

        if (user != null) {

            File file = fileRepository.findById(UUID.fromString(idFile));

            if (file != null) {

                boolean isAdmin = user.getRoles().contains(ADMIN);

                if (isAdmin) {
                    try {
                        byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                        ReturnFile returnFile = new ReturnFile();
                        returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                        String responseString = gson.toJson(returnFile);
                        return new ResponseEntity<>(responseString, OK);
                    } catch (IOException e) {
                        return new ResponseEntity<>("File does not found", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }
                else {

                    Access access = accessRepository.findByUsernameAndIdFile(user.getUsername(), idFile);
                    if (access != null) {

                        if (Integer.parseInt(access.getAccess()) >= 1) {

                            try {
                                byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
                                ReturnFile returnFile = new ReturnFile();
                                returnFile.setInfo(Base64.getEncoder().encodeToString(fileContent));
                                String responseString = gson.toJson(returnFile);

                                /*Path path = Paths.get("c:\\Users\\admin\\Desktop\\Test\\test.pdf");

                                OutputStream os = Files.newOutputStream(path);
                                os.write(Base64.getDecoder().decode(Base64.getEncoder().encodeToString(fileContent)));
                                os.close();*/

                                return new ResponseEntity<>(responseString, OK);
                            } catch (IOException e) {
                                return new ResponseEntity<>("File does not found", HttpStatus.UNPROCESSABLE_ENTITY);
                            }

                        }

                        return new ResponseEntity("Access denied", FORBIDDEN);

                    }
                    return new ResponseEntity("Access denied", FORBIDDEN);


                }

            }

        }

        return new ResponseEntity("User does not found", NOT_FOUND);
    }

    @GetMapping(value = "/file/getbranch")
    public ResponseEntity getInfoBranchFile(@RequestHeader ("Authorization") String token,
                                            @RequestBody FileForGet fileForGet) {

        ArrayList<File> result = fileRepository.findByFilenameAndAuthor(fileForGet.getFilename(), fileForGet.getAuthor(), Sort.by("date").descending());

        String username = jwtToken.getUsernameFromToken(token.substring(7));
        User user = userRepository.findByUsername(username);

        if (user.getRoles().contains(ADMIN)) {
            ArrayList<FileJsonOutput> responseFiles = new ArrayList<>();

            for (File file : result) {
                responseFiles.add(new FileJsonOutput(String.valueOf(file.getId()), file.getFilename(), file.getAuthor(), file.getEditor(), file.getDate(), file.getTag()));
            }

            Gson gson = new Gson();

            return new ResponseEntity(gson.toJson(responseFiles), OK);

        }
        else {

            ArrayList<FileJsonOutput> responseFiles = new ArrayList<>();

            for (File file : result) {
                Access access = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));
                if (Integer.parseInt(access.getAccess()) >= 1) {
                    responseFiles.add(new FileJsonOutput(String.valueOf(file.getId()), file.getFilename(), file.getAuthor(), file.getEditor(), file.getDate(), file.getTag()));
                }

            }

            Gson gson = new Gson();

            return new ResponseEntity(gson.toJson(responseFiles), OK);

        }

    }

}
