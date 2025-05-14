package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.UserController;
import com.personneltrackingsystem.dto.RegisterRequest;
import com.personneltrackingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public List<RegisterRequest> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    public RegisterRequest getOneUser(Long userId) {
        return userService.getOneUser(userId);
    }

    @Override
    public RegisterRequest createUser(RegisterRequest newUser) {
        return userService.createUser(newUser);
    }

    @Override
    public RegisterRequest updateUser(Long userId, RegisterRequest newUser) {
        return userService.updateUser(userId, newUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }
} 