package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.HomeController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeControllerImpl implements HomeController {
    @Override
    public String home() {
        return "home";
    }
}
