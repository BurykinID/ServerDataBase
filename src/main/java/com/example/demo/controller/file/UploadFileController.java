package com.example.demo.controller.file;

import com.example.demo.Validator.Calc;
import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.forJsonObject.file.Update.FileForUpdate;
import com.example.demo.forJsonObject.file.forUpload.FileJson;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

// переработать и добавить версионирование

@RestController
public class UploadFileController {


    private final FileRepository fileRepository;
    private final AccessRepository accessRepository;
    private final JwtToken jwtToken;

    public UploadFileController (FileRepository fileRepository, AccessRepository accessRepository, JwtToken jwtToken) {
        this.fileRepository = fileRepository;
        this.accessRepository = accessRepository;
        this.jwtToken = jwtToken;
    }

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/update")
    public ResponseEntity saveVersionFile(@RequestHeader("Authorization") String token,
                                          @RequestBody FileForUpdate file) {

        File file1 = fileRepository.findById(fileRepository.findByFilenameAndAuthor(file.getFilename(), file.getAuthor(), Sort.by("date").descending()).get(0).getId());

        if (file1 != null) {
            try {
                byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file1.getPath()));
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                if (encodedString != null) {
                    if (!encodedString.equals(file.getContent())) {

                        String username = jwtToken.getUsernameFromToken(token.substring(7));
                        String uploadFileName = file.getFilename();

                        long time = new Date().getTime();
                        String uploadDate = String.valueOf(time);


                        ArrayList<String> tags = new ArrayList<>();

                        tags.addAll(file1.getTag());

                        String uploadFileSize = Calc.getFileSize(file.getContent().length());
                        UUID id = UUID.randomUUID();
                        File newFile = new File(id, uploadFileName, uploadFileSize , uploadDate, file1.getAuthor(), username, uploadPath + id, tags);

                        boolean notNull = false;

                        // нужно автоматически выдать тот же уровень прав всех, у кого он был на предыдущей версии файла.

                        if (newFile != null) {
                            fileRepository.save(newFile);
                            notNull = true;

                            for (Access existAccess :accessRepository.findByIdFile(String.valueOf(file1.getId()))) {

                                Access access = new Access();
                                access.setIdFile(String.valueOf(newFile.getId()));
                                access.setUsername(existAccess.getUsername());
                                access.setAccess(existAccess.getAccess());

                                accessRepository.save(access);

                            }

                        }

                        try{
                            byte[] decodedBytes = Base64.getDecoder().decode(file.getContent());
                            Files.write(Paths.get(uploadPath + id), decodedBytes);
                        }
                        catch (IOException e) {
                            return new ResponseEntity<>("Access denied", FORBIDDEN);
                        }

                        if (notNull) {
                            return new ResponseEntity<>("File was updated", OK);
                        }
                        else {
                            return new ResponseEntity<>("Access denied", FORBIDDEN);
                        }

                    }
                }
            } catch (IOException e) {
                return new ResponseEntity<>("Error Base64 encode", UNPROCESSABLE_ENTITY);
            }
        }

        return new ResponseEntity<>("ok", OK);

    }

    @PostMapping("/addFile")
    public ResponseEntity saveFile(@RequestHeader("Authorization") String token,
                                   @RequestBody FileJson fileJson) {
        File newFile = null;
        boolean fileExistence = false;

        if (fileJson != null) {
            java.io.File uploadDir = new java.io.File(uploadPath);
            String username = jwtToken.getUsernameFromToken(token.substring(7));

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uploadFileName = fileJson.getFilename();

            long time = new Date().getTime();
            String uploadDate = String.valueOf(time);

            ArrayList<String> tags = new ArrayList<>();

            try {
                fileJson.setTag(fileJson.getTag().replaceAll(" ", ""));
                String[] userTags = fileJson.getTag().split(",");
                for (String tagAboutUser : userTags) {
                    tags.add(tagAboutUser);
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                tags.add(fileJson.getTag().trim());
            }

            String uploadFileSize = Calc.getFileSize(fileJson.getContent().length());
            UUID id = UUID.randomUUID();
            newFile = new File(id, uploadFileName, uploadFileSize , uploadDate, username, username, uploadPath + id, tags);

            boolean notNull = false;

            if (newFile != null) {
                fileRepository.save(newFile);
                notNull = true;

                Access access = new Access();
                access.setIdFile(String.valueOf(newFile.getId()));
                access.setUsername(newFile.getAuthor());
                access.setAccess("3");

                accessRepository.save(access);

            }

            try{
                byte[] decodedBytes = Base64.getDecoder().decode(fileJson.getContent());
                Files.write(Paths.get(uploadPath + id), decodedBytes);
            }
            catch (IOException e) {
                return new ResponseEntity<>("Access denied", FORBIDDEN);
            }

            if (notNull) {
                return new ResponseEntity<>("File was saved", OK);
            }
            else {
                return new ResponseEntity<>("Access denied", FORBIDDEN);
            }

        }


        return new ResponseEntity<>("File was not saved", FORBIDDEN);


    }

}

