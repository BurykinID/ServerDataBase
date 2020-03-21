package com.example.demo.controller;

import com.example.demo.Validator.Calc;
import com.example.demo.entity.File;
import com.example.demo.form.FileForm;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@RestController
public class UploadFileController {

    @Autowired
    private FileRepository fileRepository;


    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping(value = "/addFile")
    public String showAddPersonPage(Model model) {

        FileForm fileForm = new FileForm();
        model.addAttribute("fileForm", fileForm);

        return "addFile";
    }

    @PostMapping(value = {"/addFile"})
    public String saveFile(@RequestParam("filename") MultipartFile file,
                           @RequestParam String type,
                           @RequestParam String parent,
                           @RequestParam String author,
                           @RequestParam String editor) throws IOException {

        File newFile = null;

        if (file != null) {
            java.io.File uploadDir = new java.io.File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uploadFileName = file.getOriginalFilename();
            String uploadSize = Calc.getFileSize(file.getSize());
            String uploadPathFile = uploadPath + uploadFileName;
            long time = new Date().getTime();
            String uploadDate = String.valueOf(time);

            newFile = new File(uploadFileName, type, uploadSize, uploadDate, parent, author, editor, uploadPathFile);
            file.transferTo(new java.io.File(uploadPathFile));

        }

        if (newFile != null)
            fileRepository.save(newFile);

        return "redirect:/listFile";
    }
}

