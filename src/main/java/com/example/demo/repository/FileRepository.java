package com.example.demo.repository;

import com.example.demo.entity.File;
import net.bytebuddy.TypeCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, Long> {

        ArrayList<File> findByFilename (String filename);
        ArrayList<File> findAll();
        File findById(UUID id);
        ArrayList<File> findByFilenameAndAuthor(@Param("filename") String filename, @Param("author") String author, Sort sort);
        ArrayList<File> findAll(Sort sort);

}
