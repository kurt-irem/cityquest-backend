package com.cityquest.cityquest_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "CityQuest backend is running. Visit /h2-console or /auth/login";
    }
}
