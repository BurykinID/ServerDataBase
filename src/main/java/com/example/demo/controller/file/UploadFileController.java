package com.example.demo.controller.file;

import com.example.demo.Validator.Calc;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.ElObj.Rules;
import com.example.demo.forJsonObject.Response;
import com.example.demo.form.FileForm;
import com.example.demo.repository.FileRepository;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.dom4j.rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Controller
public class UploadFileController {

    @Autowired
    private FileRepository fileRepository;


    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping(value = "/addFile")
    public String showAddPersonPage(Model model) {

        FileForm fileForm = new FileForm();
        model.addAttribute("fileForm", fileForm);

        return "files/addFile";
    }

    @PostMapping(value = {"/addFile"})
    @ResponseBody
    public String saveFile( @AuthenticationPrincipal User user,
                            @RequestParam("filename") MultipartFile file,
                            @RequestParam String type,
                            @RequestParam String parent,
                            @RequestParam String tag) throws IOException {

        File newFile = null;
        boolean fileExistence = false;

        if (file != null) {
            java.io.File uploadDir = new java.io.File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            StringBuilder uploadFileNameSb = new StringBuilder();

            String uploadFileName = file.getOriginalFilename();

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
                        String filename = fileInfo[0];
                        String typeFile = fileInfo[fileInfo.length -1];
                        int begin = filename.indexOf("(");
                        if (begin != -1) {
                            int end = filename.indexOf(")");
                            StringBuilder filenameForEquals = new StringBuilder();
                            filenameForEquals.append(filename.substring(0, begin));
                            filenameForEquals.append(".");
                            filenameForEquals.append(typeFile);
                            if (filenameForEquals.toString().equals(uploadFileName)) {
                                short nowCountFiles = Short.parseShort(filename.substring(begin+1, end));
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

            String uploadSize = Calc.getFileSize(file.getSize());

            StringBuilder uploadPathFileSb = new StringBuilder();
            uploadPathFileSb.append(uploadPath);
            uploadPathFileSb.append(uploadFileName);

            String uploadPathFile = uploadPathFileSb.toString();

            long time = new Date().getTime();
            String uploadDate = String.valueOf(time);

            ArrayList<String> accessList = new ArrayList<>();
            accessList.add(user.getUsername());

            ArrayList<String> tags = new ArrayList<>();
            tags.add("#тест");
            

            newFile = new File(uploadFileName, type, uploadSize, uploadDate, parent, user.getUsername(), user.getUsername(), uploadPathFile, tags, accessList);
            file.transferTo(new java.io.File(uploadPathFile.toString()));


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
            response.setDescription("File " + file.getOriginalFilename() + " had uploaded");
        }
        else {
            response.setStatus("error");
            response.setDescription("File hadn't selected");
        }

        String responseString = gson.toJson(response);

        return responseString;
    }


    @GetMapping(value = {"/getfile/{filename}"})
    @ResponseBody
    public String getFile(@PathVariable (name = "filename") String filename) {

        File file = fileRepository.findByFilename(filename);

        try {
            byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            return encodedString;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "error";

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

