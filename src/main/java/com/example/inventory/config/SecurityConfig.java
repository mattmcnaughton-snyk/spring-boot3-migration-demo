package com.example.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Uses the chained (non-lambda) HttpSecurity DSL with {@code and()}. Every method used
 * here is deprecated in Spring Security 6.1+ and removed in Spring Security 7, which is
 * what Spring Boot 4 ships with.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // VULNERABLE: hardcoded credentials.
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Adm1nP@ssw0rd!";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // VULNERABLE: CSRF protection disabled for the whole app.
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/api/public/**", "/h2-console/**", "/actuator/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            // VULNERABLE: allows the app to be framed (clickjacking).
            .headers().frameOptions().disable();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername(ADMIN_USERNAME).password(ADMIN_PASSWORD).roles("ADMIN", "USER").build(),
                User.withUsername("user").password("password").roles("USER").build());
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        // VULNERABLE: passwords stored and compared in plain text.
        return NoOpPasswordEncoder.getInstance();
    }
}
