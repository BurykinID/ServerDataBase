package com.example.demo.controller.file;

import com.example.demo.Validator.Calc;
import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam("author") String author) {

        File file1 = fileRepository.findById(fileRepository
                .findByFilenameAndAuthor(file.getOriginalFilename(), author, Sort.by("date").descending())
                .get(0)
                .getId());

        String usr = jwtToken.getUsernameFromToken(token.substring(7));

        if (file1 != null) {
            try {

                if (!file.isEmpty()) {

                    ArrayList<Access> accessesList = accessRepository.findByIdFile(String.valueOf(file1.getId()));

                    boolean isEnabled = false;

                    for (Access access : accessesList) {
                        if (access.getUsername().equals(usr)) {
                            isEnabled = true;
                            break;
                        }
                    }

                    if (isEnabled) {

                        BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1.getPath()), "UTF-8"));

                        Path filepath = Paths.get(uploadPath, file.getOriginalFilename());

                        OutputStream os = Files.newOutputStream(filepath);
                        os.write(file.getBytes());

                        BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(uploadPath + file.getOriginalFilename()), "UTF-8"));
                        boolean isNotEquals = false;

                        String s;
                        while (br1.read() != - 1) {
                            s = br1.readLine();
                            if (! s.equals(br2.readLine())) {
                                br2.lines();
                                isNotEquals = true;
                                br1.close();
                                br2.close();
                                break;
                            }
                        }

                        java.io.File fileF = new java.io.File(uploadPath + file.getOriginalFilename());
                        fileF.delete();

                        if (isNotEquals) {


                            String username = jwtToken.getUsernameFromToken(token.substring(7));
                            String uploadFileName = file.getOriginalFilename();

                            long time = new Date().getTime();
                            String uploadDate = String.valueOf(time);

                            ArrayList<String> tags = new ArrayList<>();

                            tags.addAll(file1.getTag());

                            String uploadFileSize = Calc.getFileSize(file.getSize());
                            UUID id = UUID.randomUUID();
                            File newFile = new File(id, uploadFileName, uploadFileSize, uploadDate, file1.getAuthor(), username, uploadPath + id, tags);
                            // нужно автоматически выдать тот же уровень прав всех, у кого он был на предыдущей версии файла.

                            fileRepository.save(newFile);

                            for (Access existAccess : accessRepository.findByIdFile(String.valueOf(file1.getId()))) {

                                Access access = new Access();
                                access.setIdFile(String.valueOf(newFile.getId()));
                                access.setUsername(existAccess.getUsername());
                                access.setAccess(existAccess.getAccess());

                                accessRepository.save(access);

                            }

                            try {
                                filepath = Paths.get(uploadPath, String.valueOf(id));
                                os = Files.newOutputStream(filepath);
                                os.write(file.getBytes());
                                os.close();
                            } catch (IOException e) {
                                return new ResponseEntity<>("Access denied", FORBIDDEN);
                            }

                            return new ResponseEntity<>("File was updated", OK);

                        }


                    } else {
                        return new ResponseEntity("Access denied", FORBIDDEN);
                    }
                }
                else {
                    return new ResponseEntity("File is empty", UNPROCESSABLE_ENTITY);
                }

            } catch (IOException e) {
                return new ResponseEntity<>("Error Base64 encode", UNPROCESSABLE_ENTITY);
            }
        }

        return new ResponseEntity<>("Bad request", BAD_REQUEST);

    }

    @PostMapping("/addFile")
    public ResponseEntity saveFile(@RequestHeader("Authorization") String token,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam ("tag") String tag) {
        File newFile = null;

        if (file != null) {

            if (!file.isEmpty()) {
                java.io.File uploadDir = new java.io.File(uploadPath);
                String username = jwtToken.getUsernameFromToken(token.substring(7));

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String uploadFileName = file.getOriginalFilename();

                long time = new Date().getTime();
                String uploadDate = String.valueOf(time);

                ArrayList<String> tags = new ArrayList<>();

                try {
                    tag = tag.replaceAll(" ", "");
                    String[] userTags = tag.split(",");
                    tags.addAll(Arrays.asList(userTags));
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    tags.add(tag.trim());
                }

                String uploadFileSize = Calc.getFileSize(file.getSize());
                UUID id = UUID.randomUUID();
                newFile = new File(id, uploadFileName, uploadFileSize , uploadDate, username, username, uploadPath + id, tags);

                fileRepository.save(newFile);

                Access access = new Access();
                access.setIdFile(String.valueOf(newFile.getId()));
                access.setUsername(newFile.getAuthor());
                access.setAccess("3");

                accessRepository.save(access);

                try{

                    Path filepath = Paths.get(uploadPath, String.valueOf(id));

                    OutputStream os = Files.newOutputStream(filepath);
                    os.write(file.getBytes());
                    os.close();

                }
                catch (IOException e) {
                    return new ResponseEntity<>("Access denied", FORBIDDEN);
                }

                return new ResponseEntity<>("File was saved", OK);

            }
            else {
                return new ResponseEntity<>("File is empty", UNPROCESSABLE_ENTITY);
            }

        }

        return new ResponseEntity<>("File was not saved", FORBIDDEN);

    }

}