package com.example.demo.repository;

import com.example.demo.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

        Optional<File> findById(Long id);

}
