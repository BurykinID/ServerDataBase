package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private MainSender mailSender;

    private final UserRepository userRepository;

    public UserService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public boolean addUser(User user) {

        User userFromDb =  userRepository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return false;
        }

        userFromDb = userRepository.findByEmail(user.getEmail());

        if (userFromDb != null) {
            return false;
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        //roles.add(Role.ADMIN);
        user.setRoles(roles);
        user.setActivationCode(UUID.randomUUID().toString());

        userRepository.save(user);

        if (!StringUtils.isEmpty(user.getEmail())) {

            String message = "Hello, " + user.getUsername() + "!\n" +
                    "Welcome to Service. Please, visit to next link for activate your account: http://localhost:8080/activate/" + user.getActivationCode();

            mailSender.send(user.getEmail(), "Activation code", message);
        }

        return true;

    }

    public boolean addUser(User user, Role admin) {
        User userFromDb =  userRepository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return false;
        }

        userFromDb = userRepository.findByEmail(user.getEmail());

        if (userFromDb != null) {
            return false;
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
        user.setRoles(roles);
        user.setActivationCode(UUID.randomUUID().toString());

        userRepository.save(user);

        if (!StringUtils.isEmpty(user.getEmail())) {

            String message = "Hello, " + user.getUsername() + "!\n" +
                    "Welcome to Service. Please, visit to next link for activate your account: http://localhost:8080/activate/" + user.getActivationCode();

            mailSender.send(user.getEmail(), "Activation code", message);
        }

        return true;
    }

    public boolean activateUser (String code) {

        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActivationCode(null);

        userRepository.save(user);

        return true;
    }

}
