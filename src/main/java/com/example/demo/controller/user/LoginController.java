package com.example.demo.controller.user;

import com.example.demo.config.component.JwtRequest;
import com.example.demo.config.component.JwtResponse;
import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.Response;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtToken jwtToken;

    @Autowired
    private UserService jwtUserDetailsService;

    private final UserRepository userRepository;

    public LoginController (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        User user = userRepository.findByUsername(authenticationRequest.getUsername());
        if (user != null) {
            if (user.getActivationCode() == null) {
                authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
                final UserDetails userDetails = jwtUserDetailsService
                        .loadUserByUsername(authenticationRequest.getUsername());
                final String token = jwtToken.generateToken(userDetails);
                return ResponseEntity.ok(new JwtResponse(token));
            }
            else {
                return new ResponseEntity<>("User is not active", HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>("User does not found", HttpStatus.FORBIDDEN);
        }

    }

    private void authenticate(String username, String password) throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

    }

}