package com.example.demo.controller;

import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.ElObj.Rules;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class MainController {

    @Autowired
    private FileRepository fileRepository;

    @GetMapping(value = { "/"})
    public String index(Model model) {
        return "index";
    }




}
