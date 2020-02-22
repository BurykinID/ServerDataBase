package com.example.demo.controller;

import com.example.demo.entity.File;
import com.example.demo.form.FileForm;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MainController {

    @Autowired
    private FileRepository fileRepository;

    //private static List<File> files = new ArrayList<File>();

    // ​​​​​​​
    // Вводится (inject) из application.properties.

    @Value("${error.message}")
    private String errorMessage;

    @GetMapping(value = { "/", "/index" })
    public String index(Model model) {
        return "index";
    }

    @GetMapping(value = "/listFile")
    public String personList(Map<String, Object> model) {

        Iterable<File> files = fileRepository.findAll();

        model.put("files", files);

        return "listFile";
    }

    @GetMapping (value = "/addFile")
    public String showAddPersonPage(Model model) {

        FileForm fileForm = new FileForm();
        model.addAttribute("fileForm", fileForm);

        return "addFile";
    }

    @PostMapping (value = { "/addFile" })
    public String saveFile(@RequestParam String filename,
                           @RequestParam String type,
                           @RequestParam String size,
                           @RequestParam String date,
                           @RequestParam String parent,
                           @RequestParam String author,
                           @RequestParam String editor) {

        File newFile = new File(filename, type, size, date, parent, author, editor);
        fileRepository.save(newFile);

        return "redirect:/listFile";
    }

}
