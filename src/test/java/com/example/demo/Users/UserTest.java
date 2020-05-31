/*
package com.example.demo.Users;

import com.example.demo.config.component.JwtRequest;
import com.example.demo.config.component.JwtResponse;
import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.example.demo.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.example.demo.role.Role.ADMIN;
import static com.example.demo.role.Role.USER;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtToken jwtToken;

    @Test
    public void testUserCreate() {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(ADMIN);
        roleSet.add(USER);
        User user = User.createUser("u", "1", "burykin.ivan2016@gmail.com", roleSet);
        Assert.assertEquals("u", user.getUsername());
        Assert.assertEquals("1", user.getPassword());
        Assert.assertEquals("burykin.ivan2016@gmail.com", user.getEmail());
        Assert.assertEquals(roleSet, user.getRoles());
    }

    @Test
    public void testRegistrationUser() {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(ADMIN);
        roleSet.add(USER);
        User user = User.createUser("u", "1", "burykin.ivan2016@gmail.com", roleSet);

        if (!userService.addUser(user, ADMIN)) {

            Assert.assertEquals("User already exists", "User already exists");
        }

        userRepository.save(user);
        ArrayList<User> findUser =  userRepository.findAll();
        Assert.assertEquals(1, findUser.size());
        Assert.assertEquals("u", user.getUsername());
        Assert.assertEquals("1", user.getPassword());
        Assert.assertEquals("burykin.ivan2016@gmail.com", user.getEmail());
        Assert.assertEquals(roleSet, user.getRoles());
    }

    @Test
    public void activateUser() throws IOException {

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(ADMIN);
        roleSet.add(USER);
        User user = User.createUser("u", "1", "burykin.ivan2016@gmail.com", roleSet);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
        user.setRoles(roles);
        user.setActivationCode(UUID.randomUUID().toString());

        userRepository.save(user);

        Assert.assertNotNull(userRepository.findAll().get(0).getActivationCode());

        user = userRepository.findAll().get(0);
        user.setActivationCode(null);
        userRepository.save(user);

        Assert.assertNull(userRepository.findAll().get(0).getActivationCode());

    }

    @Test
    public void getUserList() {

        for (int i = 0; i < 100; i++) {

            Set<Role> roleSet = new HashSet<>();
            roleSet.add(USER);
            User user = User.createUser("u" + i, "1", String.valueOf(i), roleSet);
            user.setActivationCode(UUID.randomUUID().toString());

            userRepository.save(user);

        }

        Assert.assertNotNull(userRepository.findByPartName("u"));

    }

    @Test
    public void auth() {
        JwtRequest authenticationRequest = new JwtRequest();
        authenticationRequest.setUsername("u");
        authenticationRequest.setPassword("1");

        User user = userRepository.findByUsername(authenticationRequest.getUsername());
        if (user != null) {
            if (user.getActivationCode() == null) {

                try {
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("u", "1"));
                } catch (DisabledException e) {
                    try {
                        throw new Exception("USER_DISABLED", e);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } catch (BadCredentialsException e) {
                    try {
                        throw new Exception("INVALID_CREDENTIALS", e);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                final UserDetails userDetails = userService
                        .loadUserByUsername(authenticationRequest.getUsername());
                final String token = jwtToken.generateToken(userDetails);
                Assert.assertNotNull(new JwtResponse(token));
            }
            else {
                Assert.assertNotNull(new ResponseEntity<>("User is not active", HttpStatus.FORBIDDEN));
            }
        }
        else {
            Assert.assertNotNull(new ResponseEntity<>("User does not found", HttpStatus.FORBIDDEN));
        }
    }

    @After
    public void clearTable() {
        userRepository.deleteAll();
    }

}
*/
