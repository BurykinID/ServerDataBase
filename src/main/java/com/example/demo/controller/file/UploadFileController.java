package com.example.demo.controller.file;

import com.example.demo.Validator.Calc;
import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.forJsonObject.file.forUpload.FileJson;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

// переработать и добавить версионирование

@RestController
public class UploadFileController {


    private final FileRepository fileRepository;
    private final AccessRepository accessRepository;
    private final JwtToken jwtToken;

    String upl = null;

    public UploadFileController (FileRepository fileRepository, AccessRepository accessRepository, JwtToken jwtToken) {
        this.fileRepository = fileRepository;
        this.accessRepository = accessRepository;
        this.jwtToken = jwtToken;
    }

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/addFile")
    public ResponseEntity saveFile(@RequestHeader("Authorization") String token,
                                   @RequestBody FileJson fileJson) {
        File newFile = null;
        boolean fileExistence = false;

        if (fileJson != null) {
            java.io.File uploadDir = new java.io.File(uploadPath);
            String tokenRes = token.substring(7);
            String username = jwtToken.getUsernameFromToken(tokenRes);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            StringBuilder uploadFileNameSb = new StringBuilder();

            String uploadFileName = fileJson.getFilename();

            java.io.File[] files = uploadDir.listFiles();
            ArrayList<String> listFileName = new ArrayList<>();
            for (java.io.File fileForBruteForce : files ) {
                listFileName.add(fileForBruteForce.getName());
            }

            short countFiles = 0;

            for (String fileNameForBruteForce : listFileName) {
                if (fileNameForBruteForce.equals(uploadFileName)) {
                    fileExistence = true;
                }
                else {
                    String[] fileInfo = fileNameForBruteForce.split("\\.");

                    if (fileInfo.length > 0) {
                        String filenameForStr = fileInfo[0];
                        String typeFile = fileInfo[fileInfo.length -1];
                        int begin = filenameForStr.indexOf("(");
                        if (begin != -1) {
                            int end = filenameForStr.indexOf(")");
                            StringBuilder filenameForEquals = new StringBuilder();
                            filenameForEquals.append(filenameForStr.substring(0, begin));
                            filenameForEquals.append(".");
                            filenameForEquals.append(typeFile);
                            if (filenameForEquals.toString().equals(uploadFileName)) {
                                short nowCountFiles = Short.parseShort(filenameForStr.substring(begin+1, end));
                                if (nowCountFiles > countFiles)
                                    countFiles = nowCountFiles;
                            }
                        }
                    }

                }

            }

            if (fileExistence) {

                countFiles += 1;

                String[] lotOfString = uploadFileName.split("\\.");
                String filenameWithoutType = null;
                String typeFile = null;

                if (lotOfString.length > 0) {
                    int lengthLotOfString = lotOfString.length;
                    filenameWithoutType = lotOfString[0];
                    typeFile = lotOfString[lengthLotOfString-1];
                }

                uploadFileNameSb.append(filenameWithoutType);
                uploadFileNameSb.append("(");
                uploadFileNameSb.append(countFiles);
                uploadFileNameSb.append(").");
                uploadFileNameSb.append(typeFile);

                uploadFileName = uploadFileNameSb.toString();

            }

            StringBuilder uploadPathFileSb = new StringBuilder();
            uploadPathFileSb.append(uploadPath);
            uploadPathFileSb.append(uploadFileName);

            String uploadPathFile = uploadPathFileSb.toString();

            long time = new Date().getTime();
            String uploadDate = String.valueOf(time);

            ArrayList<String> accessList = new ArrayList<>();
            accessList.add(username);

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

            java.io.File fileWithSize = null;

            try{
                byte[] decodedBytes = Base64.getDecoder().decode(fileJson.getContent());
                Path path = Files.write(Paths.get(uploadPath + uploadFileName), decodedBytes);
                fileWithSize = new java.io.File(String.valueOf(path));
            }
            catch (IOException e) {
                return new ResponseEntity<>("Access denied", FORBIDDEN);
            }

            String uploadFileSize = Calc.getFileSize(fileWithSize.length());

            newFile = new File(uploadFileName, uploadFileSize , uploadDate, username, username, uploadPathFile, tags);

        }

        boolean notNull = false;

        if (newFile != null) {
            fileRepository.save(newFile);
            notNull = true;

            Access access = new Access();
            access.setFilename(newFile.getFilename());
            access.setUsername(newFile.getAuthor());
            access.setAccess("3");

            accessRepository.save(access);

        }

        if (notNull) {
            return new ResponseEntity<>("File was saved", OK);
        }
        else {
            return new ResponseEntity<>("Access denied", FORBIDDEN);
        }

    }

}

