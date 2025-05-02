package com.personneltrackingsystem.config;

import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.entity.User;
import com.personneltrackingsystem.repository.UserRepository;
import com.personneltrackingsystem.service.Impl.CustomUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/register","/register/**", "/public/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                // .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .userDetailsService(userDetailsService);

        return http.build();
    }


    @Bean
    public CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String defaultName = "Arif Ozcan";
            String defaultUsername = "arifozcan";
            String defaultPassword = "gib6";
            String defaultEmail = "zcanarif@gmail.com";
            boolean defaultEnabled = true;
            boolean defaultLocked = false;

            if (userRepository.findByUsername(defaultUsername).isEmpty()) {
                User admin = new User();
                admin.setName(defaultName);
                admin.setUsername(defaultUsername);
                admin.setPassword(passwordEncoder.encode(defaultPassword));
                admin.setEmail(defaultEmail);
                admin.setEnabled(defaultEnabled);
                admin.setLocked(defaultLocked);
                admin.setRole(Role.ROLE_ADMIN);

                userRepository.save(admin);
                System.out.println(">> Default admin user created: username=admin, password=admin123");
            } else {
                System.out.println(">> Default admin user already exists.");
            }
        };
    }


}