package com.sleepystack.bankingapp.resource.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/auth/login")
    public String loginPage() {
        return "login";
    }
}
