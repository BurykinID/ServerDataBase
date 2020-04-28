package com.example.demo.controller.user;

import com.example.demo.entity.User;
import com.example.demo.forJsonObject.user.UserLogin;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/login")
    public String getLoginForm() {
        return "users/login";
    }

    /*@PostMapping ("/login")
    public String postLoginForm( @RequestParam String username,
                                @RequestParam String password) {

        User user = userRepository.findByUsername(username);

        if ((user.getActivationCode() == null) && (user.getPassword().equals(password))) {
            return "redirect:/listFile";
        }

        return "redirect:/login";
    }*/

    @PostMapping(value = "/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestBody UserLogin userLogin,
                            Model model) {
        String errorMessge = null;

        User user = userRepository.findByUsername(userLogin.getUsername());


        if (user.getActivationCode() != null) {
            errorMessge = "User is inactive";
        }

        if(error != null) {
            errorMessge = "Username or Password is incorrect !!";
        }
        if(logout != null) {
            errorMessge = "You have been successfully logged out !!";
        }
        model.addAttribute("errorMessge", errorMessge);
        return "users/login";
    }


    /*@PostMapping(value = {"/login"})
    public String postLogin(@RequestBody UserLogin userLogin) {

        User user = userRepository.findByUsername(userLogin.getUsername());

        Response response = new Response();
        Gson gson = new Gson();

        if (user != null) {

            StringBuilder builder = new StringBuilder();

            if (user.getActivationCode() == null && user.getPassword().equals(userLogin.getPassword())) {
                response.setStatus("ok");
                response.setDescription("");
                builder.append(gson.toJson(response         ));
            }
            else if (user.getActivationCode() != null && user.getPassword().equals(userLogin.getPassword())) {
                response.setStatus("error");
                response.setDescription("user isn't active");
                builder.append(gson.toJson(response));
            }
            else if (user.getActivationCode() == null && !user.getPassword().equals(userLogin.getPassword())){
                response.setStatus("error");
                response.setDescription("incorrect password");
                builder.append(gson.toJson(response));
            }
            else {
                response.setStatus("error");
                response.setDescription("user isn't active and incorrect password");
                builder.append(gson.toJson(response));
            }

            String responseString = builder.toString();
            return responseString;

        }

        response.setStatus("error");
        response.setDescription("User doesn't exists");
        String responseString = gson.toJson(response);

        return responseString;

    }*/

}
