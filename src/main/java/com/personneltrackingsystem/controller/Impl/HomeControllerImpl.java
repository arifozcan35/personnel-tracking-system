package com.personneltrackingsystem.controller.impl;

import com.personneltrackingsystem.controller.HomeController;
import org.springframework.stereotype.Controller;

@Controller
public class HomeControllerImpl implements HomeController {
    @Override
    public String home() {
        return "home";
    }
}
