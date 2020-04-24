package com.example.demo.controller.user;

import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.UserJSON;
import com.example.demo.form.UserForm;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.example.demo.service.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/registration")
    public String registration(Model model) {

        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        return "users/registration";
    }

    //success только чекнуть что с постом
    /*@PostMapping(value = "/registration"
     *//*headers = {"Content-type=application/json"}*//*)
    public @ResponseBody String addUser(@RequestBody UserJSON user) {

        String response;

        Gson gson = new Gson();

        Response answer = new Response();

        User user1 = new User();
        user1.setUsername(user.getUsername());
        user1.setPassword(user.getPassword());
        user1.setEmail(user.getEmail());
        //gson.fromJson(, UserJSON.class);



        if (!userService.addUser(user1)) {
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
    }*/


    @PostMapping(value = "/registration")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          Map<String, Object> model) {


        User user1 = new User();
        user1.setUsername(username);
        user1.setPassword(password);
        user1.setEmail(email);

        if (!userService.addUser(user1)) {
            //model.addAttribute("message", "User already exists!");
            System.out.println("user already exists!");
            return "redirect:/registration";

        }


        return "redirect:/login";
    }

    //success
    @GetMapping("/activate/{code}")
    public @ResponseBody String activate(/*Model model,*/@PathVariable(name = "code") String code) {

        boolean isActivated = userService.activateUser(code);

        Gson gson = new Gson();

        String response;

        Response answer = new Response();

        if(isActivated) {
            answer.setStatus("ok");
            answer.setDescription("User has activate");
            response = gson.toJson(answer);
        }

        else {
            answer.setStatus("error");
            answer.setDescription("Activate code doesn't found.");
            response = gson.toJson(answer);
        }



        return response;

    }

}
