package com.example.demo.controller.user;

import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.forJsonObject.user.UserJSON;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistrationController {


    private final UserService userService;
    private final UserRepository userRepository;

    public RegistrationController (UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }
    /*
    @GetMapping(value = "/registration")
    public String registration(Model model) {

        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        return "users/registration";
    }*/
    //success
    @PostMapping(value = "/registration")
    public String addUser(@RequestBody UserJSON user) {

        String response;
        Gson gson = new Gson();
        Response answer = new Response();

        User user1 = new User();
        user1.setUsername(user.getUsername());
        user1.setPassword(user.getPassword());
        user1.setEmail(user.getEmail());

        if (!userService.addUser(user1)) {
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
    /*@PostMapping(value = "/registration")
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
    }*/
    //success
    @GetMapping(value = "/activate/{code}")
    public String activate(@PathVariable(name = "code") String code) {

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
