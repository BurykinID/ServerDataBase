package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "Access")
@NoArgsConstructor
@Data
public class Access {

    @Id
    @GeneratedValue (generator = "UUID")
    @GenericGenerator (
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private String idFile;
    private String username;
    private String access;

    public Access(String idFile, String username, String access) {
        this.idFile = idFile;
        this.username = username;
        this.access = access;
    }

}
