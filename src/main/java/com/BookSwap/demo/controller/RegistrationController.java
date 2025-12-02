package com.BookSwap.demo.controller;

import com.BookSwap.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/setup")
    public String showSetupPage(Model model){
        if(!userService.isFirstUser()){
            return "redirect:/login";
        }
        model.addAttribute("isSetup", true);
        return "register";
    }

    @PostMapping("/setup")
    public String setupFirstAdmin(@RequestParam String username,
                                     @RequestParam String password,
                                        @RequestParam String email,
                                            Model model){
        try {
            userService.registerFirstAdmin(username, password, email);
            return "redirect:/login?setupSuccess";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isSetup", true);
            return "register";
        }
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        if(userService.isFirstUser()){
            return "redirect:/setup";
        }
        model.addAttribute("isSetup", false);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                                 @RequestParam String email,
                                    Model model){
        try {
            userService.registerUser(username, password, email,false);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isSetup", false);
            return "register";
        }
    }
    
}
