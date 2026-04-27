package com.yas.search.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.search.kafka.config.consumer.AppKafkaListenerConfigurer;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestClient;

class SearchInfrastructureUnitTest {

    @Test
    void testServiceUrlConfig_accessorReturnsValue() {
        ServiceUrlConfig config = new ServiceUrlConfig("http://api.yas.local/product");

        assertThat(config.product()).isEqualTo("http://api.yas.local/product");
    }

    @Test
    void testElasticsearchDataConfig_gettersAndSettersWork() {
        ElasticsearchDataConfig config = new ElasticsearchDataConfig();

        config.setUrl("localhost:9200");
        config.setUsername("elastic");
        config.setPassword("secret");

        assertThat(config.getUrl()).isEqualTo("localhost:9200");
        assertThat(config.getUsername()).isEqualTo("elastic");
        assertThat(config.getPassword()).isEqualTo("secret");
    }

    @Test
    void testImperativeClientConfig_clientConfigurationBuilds() {
        ElasticsearchDataConfig dataConfig = new ElasticsearchDataConfig();
        dataConfig.setUrl("localhost:9200");
        dataConfig.setUsername("elastic");
        dataConfig.setPassword("secret");

        ImperativeClientConfig config = new ImperativeClientConfig(dataConfig);

        assertThat(config.clientConfiguration()).isNotNull();
    }

    @Test
    void testAppKafkaListenerConfigurer_setsValidatorOnRegistrar() {
        LocalValidatorFactoryBean validator = mock(LocalValidatorFactoryBean.class);
        KafkaListenerEndpointRegistrar registrar = mock(KafkaListenerEndpointRegistrar.class);

        AppKafkaListenerConfigurer configurer = new AppKafkaListenerConfigurer(validator);

        configurer.configureKafkaListeners(registrar);

        verify(registrar).setValidator(validator);
    }

    @Test
    void testRestClientConfig_setsDefaultJsonHeader() {
        RestClient.Builder builder = mock(RestClient.Builder.class);
        RestClient client = mock(RestClient.class);
        when(builder.defaultHeader("Content-Type", "application/json")).thenReturn(builder);
        when(builder.build()).thenReturn(client);

        RestClient result = new RestClientConfig().getRestClient(builder);

        assertThat(result).isSameAs(client);
        verify(builder).defaultHeader("Content-Type", "application/json");
        verify(builder).build();
    }

    @Test
    void testSecurityConfig_jwtConverterMapsRolesToAuthorities() {
        SecurityConfig securityConfig = new SecurityConfig();
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("realm_access", Map.of("roles", List.of("ADMIN", "MANAGER")))
            .build();

        var authentication = converter.convert(jwt);

        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
            .extracting(authority -> authority.getAuthority())
            .contains("ROLE_ADMIN", "ROLE_MANAGER");
    }
}
