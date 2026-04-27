package com.yas.search.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

class SecurityConfigFilterChainTest {

    @Test
    @SuppressWarnings("unchecked")
    void testFilterChain_buildsSecurityChain() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);
        DefaultSecurityFilterChain expectedChain = mock(DefaultSecurityFilterChain.class);

        when(http.authorizeHttpRequests(any(Customizer.class))).thenReturn(http);
        when(http.oauth2ResourceServer(any(Customizer.class))).thenReturn(http);
        when(http.build()).thenReturn(expectedChain);

        SecurityFilterChain actualChain = new SecurityConfig().filterChain(http);

        assertThat(actualChain).isSameAs(expectedChain);
    }
}
