package com.github.anhem.howto.testutil;

import com.github.anhem.howto.controller.api.MessageDTO;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class TestApplication {

    @Container
    static final PostgreSQLContainer<?> SQL_CONTAINER = new PostgreSQLContainer<>("postgres:14.5")
            .withDatabaseName("howto-db-it");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.hikari.username", SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.hikari.password", SQL_CONTAINER::getPassword);
    }

    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected ResponseEntity<MessageDTO> deleteForEntity(String url) {
        return testRestTemplate.exchange(url, HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {
        });
    }

}
