package com.example.demo.controller.file;

import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.file.forUpload.Content;
import com.example.demo.forJsonObject.file.forUpload.FileJson;
import com.example.demo.forJsonObject.file.forUpload.Filename;
import com.example.demo.forJsonObject.file.forUpload.Tag;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.form.FileForm;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

//@RestController
@Controller
public class UploadFileController {


    private final FileRepository fileRepository;
    private final AccessRepository accessRepository;

    String upl = null;

    public UploadFileController (FileRepository fileRepository, AccessRepository accessRepository) {
        this.fileRepository = fileRepository;
        this.accessRepository = accessRepository;
    }

    @Value("${upload.path}")
    private String uploadPath;

    /*@GetMapping("/addFile")
    public String showAddPersonPage(Model model) {

        FileForm fileForm = new FileForm();
        model.addAttribute("fileForm", fileForm);

        return "files/addFile";
    }*/

    @PostMapping("/addFile")
    @ResponseBody
    public String saveFile(@RequestBody FileJson fileJson) {
        File newFile = null;
        boolean fileExistence = false;

        if (fileJson != null) {
            java.io.File uploadDir = new java.io.File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            StringBuilder uploadFileNameSb = new StringBuilder();

            String uploadFileName = fileJson.getFilename();//file.getOriginalFilename();

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

            //String uploadSize = Calc.getFileSize(fileJson.getContents());//file.getSize());
            String uploadSize = "555КБ";

            StringBuilder uploadPathFileSb = new StringBuilder();
            uploadPathFileSb.append(uploadPath);
            uploadPathFileSb.append(uploadFileName);

            String uploadPathFile = uploadPathFileSb.toString();

            long time = new Date().getTime();
            String uploadDate = String.valueOf(time);

            ArrayList<String> accessList = new ArrayList<>();
            accessList.add(fileJson.getUsername());//user.getUsername());

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

            //tags.add("#тест");

            //<-- на релизе надо выпилить
            //byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File());
            /*String encodedString = Base64.getEncoder().encodeToString(file.getBytes());
            System.out.println(encodedString);
            if (upl != null){
                if (upl.equals(encodedString))
                    System.out.println(true);
                else
                    System.out.println(false);
            }
            upl = encodedString;*/
            // на релизе надо выпилить -->
            //newFile = new File(uploadFileName,  uploadSize, uploadDate, user.getUsername(), user.getUsername(), uploadPathFile, tags, accessList);
            newFile = new File(uploadFileName,  uploadSize, uploadDate, fileJson.getUsername(), fileJson.getUsername(), uploadPathFile, tags);
            //<-- на релизе выпилить
            //file.transferTo(new java.io.File(uploadPathFile));
            // -->
            try{
                //byte[] decodedBytes = Base64.getDecoder().decode(upl);
                byte[] decodedBytes = Base64.getDecoder().decode(fileJson.getContent());//fileJson.getContent());//);.getContent());
                Files.write(Paths.get(uploadPath + uploadFileName), decodedBytes);
            }
            catch (IOException e) {
                System.out.println("error");
            }

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

        Gson gson = new Gson();
        Response response = new Response();
        if (notNull) {
            response.setStatus("ok");
            //response.setDescription("File " + file.getOriginalFilename() + " had uploaded");
            response.setDescription("File " + fileJson.getFilename() + " had uploaded");
        }
        else {
            response.setStatus("error");
            response.setDescription("File hadn't selected");
        }

        String responseString = gson.toJson(response);

        return responseString;
    }

    /*@PostMapping(value = {"/addFile"})
    @ResponseBody
    public String saveFile( @RequestBody Username username,
                            //@AuthenticationPrincipal User user,
                            //@RequestBody FileJson fileJson,
                            @RequestBody Filename filename,
                            @RequestBody Content content,
                            //@RequestParam("filename") MultipartFile file,
                            //@RequestParam String tag
                            @RequestBody Tag tag) throws IOException {

        File newFile = null;
        boolean fileExistence = false;

        if (filename != null && content != null){//fileJson != null) {
            java.io.File uploadDir = new java.io.File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            StringBuilder uploadFileNameSb = new StringBuilder();

            String uploadFileName = filename.getFilename();//fileJson.getFilename();//.getFilename(); //file.getOriginalFilename();

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

            //String uploadSize = Calc.getFileSize(fileJson.getContents());//file.getSize());
            String uploadSize = "555КБ";

            StringBuilder uploadPathFileSb = new StringBuilder();
            uploadPathFileSb.append(uploadPath);
            uploadPathFileSb.append(uploadFileName);

            String uploadPathFile = uploadPathFileSb.toString();

            long time = new Date().getTime();
            String uploadDate = String.valueOf(time);

            ArrayList<String> accessList = new ArrayList<>();
            accessList.add(username.getUsername());//user.getUsername());

            ArrayList<String> tags = new ArrayList<>();

            try {
                tag.setTag(tag.getTag().replaceAll(" ", ""));
                String[] userTags = tag.getTag().split(",");
                for (String tagAboutUser : userTags) {
                    tags.add(tagAboutUser);
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                tags.add(tag.getTag().trim());
            }

            //tags.add("#тест");

            //<-- на релизе надо выпилить
            //byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File());
            *//*String encodedString = Base64.getEncoder().encodeToString(file.getBytes());
            System.out.println(encodedString);
            if (upl != null){
                if (upl.equals(encodedString))
                    System.out.println(true);
                else
                    System.out.println(false);
            }
            upl = encodedString;*//*
            // на релизе надо выпилить -->
            //newFile = new File(uploadFileName,  uploadSize, uploadDate, user.getUsername(), user.getUsername(), uploadPathFile, tags, accessList);
            newFile = new File(uploadFileName,  uploadSize, uploadDate, username.getUsername(), username.getUsername(), uploadPathFile, tags, accessList);
            //<-- на релизе выпилить
            //file.transferTo(new java.io.File(uploadPathFile));
            // -->
            try{
                //byte[] decodedBytes = Base64.getDecoder().decode(upl);
                byte[] decodedBytes = Base64.getDecoder().decode(content.getContent());//fileJson.getContent());//);.getContent());
                Files.write(Paths.get(uploadPath + uploadFileName), decodedBytes);
            }
            catch (IOException e) {
                System.out.println("error");
            }

        }

        boolean notNull = false;

        if (newFile != null) {
            fileRepository.save(newFile);
            notNull = true;
        }

        Gson gson = new Gson();
        Response response = new Response();
        if (notNull) {
            response.setStatus("ok");
            //response.setDescription("File " + file.getOriginalFilename() + " had uploaded");
            response.setDescription("File " + filename.getFilename()*//*fileJson.getFilename()*//* + " had uploaded");
        }
        else {
            response.setStatus("error");
            response.setDescription("File hadn't selected");
        }

        String responseString = gson.toJson(response);

        return responseString;
    }*/





}

