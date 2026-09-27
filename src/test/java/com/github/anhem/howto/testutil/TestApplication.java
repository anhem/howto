package com.github.anhem.howto.testutil;

import com.github.anhem.howto.controller.model.AuthenticateDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import com.github.anhem.howto.model.id.JwtToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static com.github.anhem.howto.configuration.JwtTokenFilter.BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.utility.MountableFile.forClasspathResource;

@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public abstract class TestApplication {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "superSecret1!";
    private static final String MODERATOR_USERNAME = "moderator";
    private static final String MODERATOR_PASSWORD = "superSecret2!";
    private static final String USER_USERNAME = "user";
    private static final String USER_PASSWORD = "superSecret3!";

    protected JwtToken adminJwtToken;
    protected JwtToken moderatorJwtToken;
    protected JwtToken userJwtToken;

    private static final String AUTHENTICATE_URL = "/api/auth/authenticate";

    static final PostgreSQLContainer SQL_CONTAINER = new PostgreSQLContainer("postgres:14.5")
            .withDatabaseName("howto-db-it")
            .withUsername("howto")
            .withCopyFileToContainer(forClasspathResource("/db/baseline/howto_db_baseline.sql"), "/docker-entrypoint-initdb.d/howto_db_baseline.sql");

    @DynamicPropertySource
    static void registerSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.hikari.username", SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.hikari.password", SQL_CONTAINER::getPassword);
    }

    @BeforeAll
    public static void beforeAll() {
        SQL_CONTAINER.start();
    }

    @BeforeEach
    void setUp() {
        if (adminJwtToken == null) {
            adminJwtToken = authenticate(ADMIN_USERNAME, ADMIN_PASSWORD);
        }
        if (moderatorJwtToken == null) {
            moderatorJwtToken = authenticate(MODERATOR_USERNAME, MODERATOR_PASSWORD);
        }
        if (userJwtToken == null) {
            userJwtToken = authenticate(USER_USERNAME, USER_PASSWORD);
        }
    }

    @Autowired
    protected RestTestClient restTestClient;

    protected <T> ResponseEntity<T> getWithToken(String url, Class<T> responseType, JwtToken jwtToken) {
        var result = restTestClient.get()
                .uri(url)
                .headers(headers -> headers.addAll(withJwtToken(jwtToken)))
                .exchange()
                .expectBody(responseType)
                .returnResult();
        return new ResponseEntity<>(result.getResponseBody(), result.getResponseHeaders(), result.getStatus());
    }

    protected <T, B> ResponseEntity<T> postWithToken(String url, B body, Class<T> responseType, JwtToken jwtToken) {
        var result = restTestClient.post()
                .uri(url)
                .headers(headers -> headers.addAll(withJwtToken(jwtToken)))
                .body(body)
                .exchange()
                .expectBody(responseType)
                .returnResult();
        return new ResponseEntity<>(result.getResponseBody(), result.getResponseHeaders(), result.getStatus());
    }

    protected <T, B> ResponseEntity<T> putWithToken(String url, B body, Class<T> responseType, JwtToken jwtToken) {
        var result = restTestClient.put()
                .uri(url)
                .headers(headers -> headers.addAll(withJwtToken(jwtToken)))
                .body(body)
                .exchange()
                .expectBody(responseType)
                .returnResult();
        return new ResponseEntity<>(result.getResponseBody(), result.getResponseHeaders(), result.getStatus());
    }

    protected <T> ResponseEntity<T> deleteWithToken(String url, Class<T> responseType, JwtToken jwtToken) {
        var result = restTestClient.delete()
                .uri(url)
                .headers(headers -> headers.addAll(withJwtToken(jwtToken)))
                .exchange()
                .expectBody(responseType)
                .returnResult();
        return new ResponseEntity<>(result.getResponseBody(), result.getResponseHeaders(), result.getStatus());
    }

    private static HttpHeaders withJwtToken(JwtToken jwtToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, String.format("%s%s", BEARER, jwtToken.value()));
        return httpHeaders;
    }

    private JwtToken authenticate(String username, String password) {
        AuthenticateDTO authenticateDTO = AuthenticateDTO.builder()
                .username(username)
                .password(password)
                .build();
        var result = restTestClient.post()
                .uri(AUTHENTICATE_URL)
                .body(authenticateDTO)
                .exchange()
                .expectBody(MessageDTO.class)
                .returnResult();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getResponseBody()).isNotNull();
        return new JwtToken(result.getResponseBody().getMessage());
    }

}
