package com.example.demo.repository;

import com.example.demo.entity.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface AccessRepository extends JpaRepository<Access, Long> {

    ArrayList<Access> findByUsername(String username);
    ArrayList<Access> findByIdFile(String iDFile);
    Access findByUsernameAndIdFile(String username, String idFile);
    @Query("delete from Access access where access.idFile = :id")
    String deleteAccessByIdFile(@Param("id") String id);

}
