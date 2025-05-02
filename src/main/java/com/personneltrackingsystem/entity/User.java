package com.personneltrackingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(
        name = "user", schema = "dbpersonel",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "username_unique",
                        columnNames = "username"
                ),
                @UniqueConstraint(
                        name = "email_unique",
                        columnNames = "email"
                )

        })
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGen5")
    @SequenceGenerator(name = "mySeqGen5", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;


    @Column(
            name = "username",
            unique = true
    )
    private String username;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @Column(
            name = "email",
            unique = true
    )
    private String email;

    @Column(
            name = "enabled"
    )
    private boolean enabled;

    @Column(name = "locked")
    private boolean locked;

    @Enumerated(EnumType.STRING)
    private Role role;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public User() {
    }

    public User(
            String name,
            String username,
            String password,
            String email,
            boolean enabled,
            boolean locked,
            Role role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.locked = locked;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", locked=" + locked +
                ", role=" + role +
                '}';
    }
}
