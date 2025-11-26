package com.cityquest.cityquest_backend.controller;

import com.cityquest.cityquest_backend.dto.AuthRequest;
import com.cityquest.cityquest_backend.dto.AuthResponse;
import com.cityquest.cityquest_backend.dto.RegisterRequest;
import com.cityquest.cityquest_backend.model.User;
import com.cityquest.cityquest_backend.repository.UserRepository;
import com.cityquest.cityquest_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles("USER")
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization", required = false) String authorization,
                                   @RequestBody(required = false) AuthRequest authRequest) {
        String username = null;
        String password = null;

        // Prefer Basic auth header if present
        if (authorization != null && authorization.startsWith("Basic ")) {
            try {
                String base64 = authorization.substring(6);
                String decoded = new String(java.util.Base64.getDecoder().decode(base64));
                int idx = decoded.indexOf(":");
                if (idx > 0) {
                    username = decoded.substring(0, idx);
                    password = decoded.substring(idx + 1);
                }
            } catch (IllegalArgumentException ignored) { }
        }

        // Fall back to JSON body
        if ((username == null || password == null) && authRequest != null) {
            username = authRequest.getUsername();
            password = authRequest.getPassword();
        }

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Missing credentials");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestParam Optional<String> username) {
        return ResponseEntity.ok("This is a placeholder protected endpoint");
    }
}
