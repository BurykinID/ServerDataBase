package com.example.demo.repository;

import com.example.demo.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

        Optional<File> findById (Long id);
        Optional<File> findByAuthor (String username);
        File findByFilename (String filename);
        Optional<File> findByAccessList (String username);
        ArrayList<File> findAll();

}
