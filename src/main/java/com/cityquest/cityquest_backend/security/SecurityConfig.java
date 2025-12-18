package com.cityquest.cityquest_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("!test")
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Always allow CORS preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Auth routes
                .requestMatchers("/auth/register").permitAll()
                .requestMatchers("/auth/login").authenticated() // Basic Auth for login
                .requestMatchers("/auth/logout").permitAll()

                // Public read endpoints for Places
                .requestMatchers(HttpMethod.GET, "/api/places").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/places/").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/places/*").permitAll() // e.g., /api/places/{id}
                .requestMatchers(HttpMethod.GET, "/api/places/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/places/category/**").permitAll()

                // Public read endpoints for Collections
                .requestMatchers(HttpMethod.GET, "/api/collections").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/collections/").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/collections/*").permitAll() // e.g., /api/collections/{id}
                .requestMatchers(HttpMethod.GET, "/api/collections/search").permitAll()

                // Protected collections endpoints
                .requestMatchers(HttpMethod.GET, "/api/collections/my-collections").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/collections/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/collections/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/collections/**").authenticated()

                // Protected endpoints: own places and write/update/delete
                .requestMatchers(HttpMethod.GET, "/api/places/my-places").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/places/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/places/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/places/**").authenticated()

                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults()) // Enable Basic Auth for /auth/login
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT for other endpoints

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("https://localhost:5173"); // Vite dev 
        config.addAllowedOrigin("https://localhost"); 
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
