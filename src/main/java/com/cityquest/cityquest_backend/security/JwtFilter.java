package com.cityquest.cityquest_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.cookie-name:jwt-token}")
    private String cookieName;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Skip JWT filter for auth endpoints (login/register/logout)
        String path = request.getRequestURI();
        System.out.println("JWT Filter: Processing request to: " + path + " Method: " + request.getMethod());
        
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register") || path.startsWith("/auth/logout")) {
            System.out.println("JWT Filter: Skipping auth endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        String username = null;
        String token = null;

        // Try to extract JWT from cookie first
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Fallback: check Authorization header for Bearer token (for API clients)
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token != null) {
            try {
                username = jwtUtil.extractUsername(token);
                System.out.println("JWT Filter: Found token for user: " + username);
            } catch (Exception e) {
                System.err.println("JWT Filter: Failed to extract username: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, user)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("JWT Filter: Successfully authenticated user: " + username);
            } else {
                System.err.println("JWT Filter: Token validation failed for user: " + username);
            }
        }

        filterChain.doFilter(request, response);
    }
}
