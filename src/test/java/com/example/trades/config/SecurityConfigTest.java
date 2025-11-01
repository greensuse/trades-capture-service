package com.example.trades.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityConfigTest {
    @Test
    void securityConfig_exposesSecurityBeans() {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SecurityConfig.class)) {
            // PasswordEncoder bean is commonly provided by security configuration
            PasswordEncoder encoder = ctx.getBean(PasswordEncoder.class);
            assertNotNull(encoder, "PasswordEncoder bean should not be null");

            // SecurityFilterChain may be declared in modern Spring Security configurations
            String[] filterChainBeans = ctx.getBeanNamesForType(SecurityFilterChain.class);
            assertTrue(filterChainBeans.length > 0, "Expected at least one SecurityFilterChain bean");

            // AuthenticationManager is commonly exposed as a bean (if configured)
            String[] authManagerBeans = ctx.getBeanNamesForType(AuthenticationManager.class);
            assertTrue(authManagerBeans.length > 0, "Expected at least one AuthenticationManager bean");
        }
    }
}
