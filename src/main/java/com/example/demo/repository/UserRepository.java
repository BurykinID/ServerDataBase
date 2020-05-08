package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername (String username);
    User findByActivationCode (String code);
    User findByEmail (String email);
    User findById(UUID uuid);
    ArrayList<User> findAll();
    @Query ("select usr.username from User usr where usr.username like CONCAT(:partName, '%')")
    ArrayList<String> findByPartName(@Param("partName") String partName);

    ArrayList<User> findByRoles (Role admin);
}
