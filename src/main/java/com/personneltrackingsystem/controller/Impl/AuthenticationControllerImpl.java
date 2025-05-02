package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.AuthenticationController;
import com.personneltrackingsystem.entity.User;
import com.personneltrackingsystem.service.Impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final CustomUserDetailsService userDetailsService;


    @Override
    public String register(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @Override
    public String register(User user, Model model, RedirectAttributes redirectAttributes) {
        userDetailsService.registerUser(user);
        redirectAttributes.addFlashAttribute("success", "Please confirm your email address");
        return "redirect:/register";
    }

    @Override
    public String confirmToken(String token, Model model) {
        userDetailsService.confirmToken(token);
        return "confirmToken";
    }

}
