package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.entity.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/register")
public interface AuthenticationController {

    @GetMapping
    String register(Model model);


    @PostMapping
    String register(@ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes);


    @GetMapping("/confirmToken")
    String confirmToken(@RequestParam("token") String token, Model model);
}
