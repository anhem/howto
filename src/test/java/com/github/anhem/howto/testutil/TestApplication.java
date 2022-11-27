package com.github.anhem.howto.testutil;

import com.github.anhem.howto.controller.model.AuthenticateDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.anhem.howto.configuration.JwtTokenFilter.BEARER;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("integration-test")
@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class TestApplication {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "superSecret1!";
    private static final String USER_USERNAME = "user";
    private static final String USER_PASSWORD = "superSecret2!";

    protected String adminJwtToken;
    protected String userJwtToken;

    private static final String AUTHENTICATE_URL = "/api/auth/authenticate";

    @Container
    static final PostgreSQLContainer<?> SQL_CONTAINER = new PostgreSQLContainer<>("postgres:14.5")
            .withDatabaseName("howto-db-it");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.hikari.username", SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.hikari.password", SQL_CONTAINER::getPassword);
    }

    @BeforeEach
    void setUp() {
        if (adminJwtToken == null) {
            adminJwtToken = authenticate(ADMIN_USERNAME, ADMIN_PASSWORD);
        }
        if (userJwtToken == null) {
            userJwtToken = authenticate(USER_USERNAME, USER_PASSWORD);
        }
    }

    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected <T> ResponseEntity<T> getWithToken(String url, Class<T> responseType, String jwtToken) {
        return testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(withJwtToken(jwtToken)), responseType);
    }

    protected <T, B> ResponseEntity<T> postWithToken(String url, B body, Class<T> responseType, String jwtToken) {
        return testRestTemplate.exchange(url, HttpMethod.POST, withJwtToken(body, jwtToken), responseType);
    }

    protected <T> ResponseEntity<T> deleteWithToken(String url, Class<T> responseType, String jwtToken) {
        return testRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(withJwtToken(jwtToken)), responseType);
    }

    private <T> HttpEntity<T> withJwtToken(T body, String jwtToken) {
        HttpHeaders httpHeaders = withJwtToken(jwtToken);
        return new HttpEntity<>(body, httpHeaders);
    }

    private static HttpHeaders withJwtToken(String jwtToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, String.format("%s%s", BEARER, jwtToken));
        return httpHeaders;
    }

    private String authenticate(String username, String password) {
        AuthenticateDTO authenticateDTO = AuthenticateDTO.builder()
                .username(username)
                .password(password)
                .build();
        ResponseEntity<MessageDTO> response = testRestTemplate.postForEntity(AUTHENTICATE_URL, authenticateDTO, MessageDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().getMessage();
    }

}
