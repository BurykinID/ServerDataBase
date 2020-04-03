package com.example.demo.form;

import com.example.demo.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserForm {

    private UUID id;
    private String username;
    private String password;
    private Set<Role> roles;
    private String email;

}
