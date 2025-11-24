package com.cityquest.cityquest_backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/hello")
    public String hello(@AuthenticationPrincipal UserDetails user) {
        String name = (user != null) ? user.getUsername() : "anonymous";
        return "Hello, " + name;
    }
}
