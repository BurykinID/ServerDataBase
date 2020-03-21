package com.example.demo.controller;

import com.example.demo.entity.File;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController {

    @Autowired
    private FileRepository fileRepository;

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

}
