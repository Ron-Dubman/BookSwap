package com.BookSwap.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // This looks for "home.html" in src/main/resources/templates/
        return "home";
    }
    
    @GetMapping("/guide")
    public String showGuidePage() {
        return "guide";
    }
}