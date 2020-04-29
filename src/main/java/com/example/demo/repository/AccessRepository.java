package com.example.demo.repository;

import com.example.demo.entity.Access;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface AccessRepository extends JpaRepository<Access, Long> {

    ArrayList<Access> findByUsername(String username);
    ArrayList<Access> findByFilename(String filename);
    Access findByUsernameAndFilename(String username, String filename);

}
