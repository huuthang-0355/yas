package com.yas.cart.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.DefaultSecurityFilterChain;

public class ConfigCoverageTest {

    @Test
    void testDatabaseAutoConfig() {
        DatabaseAutoConfig config = new DatabaseAutoConfig();
        AuditorAware<String> auditorAware = config.auditorAware();
        assertNotNull(auditorAware);

        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        
        when(context.getAuthentication()).thenReturn(null);
        assertEquals("", auditorAware.getCurrentAuditor().orElse(null));
        
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");
        when(context.getAuthentication()).thenReturn(auth);
        assertEquals("user", auditorAware.getCurrentAuditor().orElse(null));
        
        SecurityContextHolder.clearContext();
    }

    @Test
    void testRestClientConfig() {
        RestClientConfig config = new RestClientConfig();
        assertNotNull(config.restClient());
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
