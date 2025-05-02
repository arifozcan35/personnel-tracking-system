package com.personneltrackingsystem.service;

import com.personneltrackingsystem.entity.User;

public interface CustomUserDetailsService {

    User registerUser(User user);

    void enableUser(User user);

    void confirmToken(String token);
}
