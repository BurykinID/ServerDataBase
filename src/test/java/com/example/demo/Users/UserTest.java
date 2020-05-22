package com.example.demo.Users;

import com.example.demo.ClientApplication;
import com.example.demo.controller.user.RegistrationController;
import com.example.demo.entity.User;

import com.example.demo.forJsonObject.user.UserJSON;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.example.demo.role.Role.*;
import static com.example.demo.role.Role.USER;

@RunWith(SpringRunner.class)
@WebMvcTest (RegistrationController.class)
public class UserTest {

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

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
    public void testRegistrationUser() throws IOException {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(ADMIN);
        roleSet.add(USER);
        User user = User.createUser("u", "1", "burykin.ivan2016@gmail.com", roleSet);

        userRepository.save(user);
        ArrayList<User> findUser =  userRepository.findAll();
        Assert.assertEquals(1, findUser.size());
        Assert.assertEquals("u", user.getUsername());
        Assert.assertEquals("1", user.getPassword());
        Assert.assertEquals("burykin.ivan2016@gmail.com", user.getEmail());
        Assert.assertEquals(roleSet, user.getRoles());
    }

}
