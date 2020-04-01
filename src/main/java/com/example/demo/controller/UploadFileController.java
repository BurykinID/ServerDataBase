package com.example.demo.controller;

import com.example.demo.Validator.Calc;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.form.FileForm;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String saveFile( @AuthenticationPrincipal User user,
                            @RequestParam("filename") MultipartFile file,
                            @RequestParam String type,
                            @RequestParam String parent) throws IOException {

        File newFile = null;
        boolean fileExistence = false;

        if (file != null) {
            java.io.File uploadDir = new java.io.File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

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
                            String filenameForEquals = filename.substring(0, begin) + "." + typeFile;
                            if (filenameForEquals.equals(uploadFileName)) {
                                short nowCountFiles = Short.parseShort(filename.substring(begin+1, end));
                                if (nowCountFiles > countFiles)
                                countFiles = nowCountFiles;
                            }
                        }
                    }


                    /*if (index != -1) {
                        Pattern pattern = Pattern.compile(fileNameForBruteForce.substring(index) + "\\(([0-9]{1,3})\\).*");
                        Matcher matcher = pattern.matcher(fileNameForBruteForce);
                        if (matcher.find()) {
                            countFiles = Short.parseShort(matcher.group(1));
                        }
                    }*/
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

                uploadFileName = filenameWithoutType + "(" + countFiles + ")." + typeFile;

            }

            String uploadSize = Calc.getFileSize(file.getSize());
            String uploadPathFile = uploadPath + uploadFileName;
            long time = new Date().getTime();
            String uploadDate = String.valueOf(time);

            newFile = new File(uploadFileName, type, uploadSize, uploadDate, parent, user.getUsername(), user.getUsername(), uploadPathFile);
            file.transferTo(new java.io.File(uploadPathFile));

        }

        if (newFile != null)
            fileRepository.save(newFile);

        return "redirect:/listFile";
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

