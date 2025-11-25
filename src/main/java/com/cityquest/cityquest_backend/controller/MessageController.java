package main.java.com.cityquest.cityquest_backend.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @GetMapping("/api/message")
    public Map<String, String> message() {
        return Map.of("message", "Hello from Spring Boot backend", "timestamp", Long.toString(System.currentTimeMillis()));
    }
}
