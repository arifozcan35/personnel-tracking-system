package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.entity.Token;
import com.personneltrackingsystem.entity.User;
import com.personneltrackingsystem.repository.UserRepository;
import com.personneltrackingsystem.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService, UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenServiceImpl tokenService;

    private final EmailServiceImpl emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }


    @Override
    public User registerUser(User user) {
        userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail())
                .ifPresent(existingUser -> {
                    throw new IllegalStateException("User already exists");
                });

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        Token confirmationToken = new Token(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        tokenService.save(confirmationToken);
        emailService.sendSimpleMail(user.getEmail(), token);
        System.out.println(token);

        return user;
    }

    @Override
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void confirmToken(String token) {
        Token confirmationToken = tokenService.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if(confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("User already confirmed");
        }

        LocalDateTime expiresAt = confirmationToken.getExpiresAt();

        if(expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        tokenService.save(confirmationToken);

        enableUser(confirmationToken.getUser());
    }
}
