package com.yas.search;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class ElasticsearchApplicationTest {

    @Test
    void testMain_startsSpringApplication() {
        String[] args = new String[]{"--spring.main.web-application-type=none"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ElasticsearchApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(ElasticsearchApplication.class, args));
        }
    }
}
