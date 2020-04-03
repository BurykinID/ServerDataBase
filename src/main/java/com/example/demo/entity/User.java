package com.example.demo.entity;


import com.example.demo.role.Role;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usr")
@Data
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String email;

    // вообще надо бы lazy, но это на потестить потом, как долго будут грузиться данные
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }


    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public Set<Role> getRoles () {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities () {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired () {
        return true;
    }

    @Override
    public boolean isAccountNonLocked () {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired () {
        return true;
    }

    @Override
    public boolean isEnabled () {
        return true;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public void setRoles (Set<Role> roles) {
        this.roles = roles;
    }


}
