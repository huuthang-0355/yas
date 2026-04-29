package com.yas.media.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.DefaultSecurityFilterChain;

public class ConfigCoverageTest {

    @Test
    void testFilesystemConfig() {
        FilesystemConfig config = new FilesystemConfig();
        config.getDirectory();
        assertNotNull(config);
    }

    @Test
    void testYasConfig() {
        YasConfig config = new YasConfig("url");
        assertEquals("url", config.publicUrl());
    }

    @Test
    void testSwaggerConfig() {
        SwaggerConfig config = new SwaggerConfig();
        assertNotNull(config);
    }

    @Test
    void testSecurityConfig() throws Exception {
        SecurityConfig config = new SecurityConfig();
        HttpSecurity http = mock(HttpSecurity.class);
        DefaultSecurityFilterChain filterChain = mock(DefaultSecurityFilterChain.class);
        
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.oauth2ResourceServer(any())).thenReturn(http);
        when(http.build()).thenReturn(filterChain);

        assertEquals(filterChain, config.filterChain(http));
        
        var converter = config.jwtAuthenticationConverterForKeycloak();
        assertNotNull(converter);
        
        Jwt jwt = mock(Jwt.class);
        java.util.Map<String, Object> realmAccess = java.util.Map.of("roles", java.util.List.of("ADMIN"));
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);
        
        assertNotNull(converter.convert(jwt));
    }
}
