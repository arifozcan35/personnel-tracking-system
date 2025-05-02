
package com.personneltrackingsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;

public interface HomeController {

    @GetMapping("/")
    String home();
}
