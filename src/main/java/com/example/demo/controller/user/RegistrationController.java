package com.example.demo.controller.user;

import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.form.UserForm;
import com.example.demo.service.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;


    @GetMapping(value = "/registration")
    public String registration(Model model) {

        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        return "users/registration";
    }

    //success только чекнуть что с постом
    @PostMapping(value = "/registration")
    public @ResponseBody String addUser(User user) {

        String response;

        Gson gson = new Gson();

        Response answer = new Response();

        if (!userService.addUser(user)) {
            //model.addAttribute("message", "User already exists!");
            //System.out.println("user already exists!");
            answer.setDescription("User already exists!");
            answer.setStatus("error");
            response = gson.toJson(answer);
            return response;

        }

        answer.setStatus("ok");
        answer.setDescription("User create");

        response = gson.toJson(answer);

        return response;
    }


    //success
    @GetMapping("/activate/{code}")
    public @ResponseBody String activate(/*Model model,*/@PathVariable(name = "code") String code) {

        boolean isActivated = userService.activateUser(code);

        Gson gson = new Gson();

        String response;

        Response answer = new Response();

        if(isActivated) {
            //model.addAttribute("message", "User activate");
            //System.out.println("User activate");
            answer.setStatus("ok");
            answer.setDescription("User has activate");
            response = gson.toJson(answer);
        }

        else {
            answer.setStatus("error");
            answer.setDescription("Activate code doesn't found.");
            response = gson.toJson(answer);

            //System.out.println("Activation code isn't found");
            //model.addAttribute("message", "Activation code isn't found");
        }



        return response;

    }

}
