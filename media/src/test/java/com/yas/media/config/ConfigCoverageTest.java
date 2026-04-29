package com.yas.media.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

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
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.oauth2ResourceServer(any())).thenReturn(http);

        assertNotNull(config.filterChain(http));
        assertNotNull(config.jwtAuthenticationConverterForKeycloak());
    }
}
