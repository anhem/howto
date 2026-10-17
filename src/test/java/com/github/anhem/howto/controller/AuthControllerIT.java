package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.AuthenticateDTO;
import com.github.anhem.howto.controller.model.CategoryDTO;
import com.github.anhem.howto.controller.model.CreateAccountDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import com.github.anhem.howto.model.id.JwtToken;
import com.github.anhem.howto.testutil.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerIT extends TestApplication {

    private static final String AUTHENTICATE_URL = "/api/auth/authenticate";
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String CATEGORIES_URL = "/api/forum/categories";

    @Test
    void userCanAuthenticate() {
        AuthenticateDTO authenticateDTO = AuthenticateDTO.builder()
                .username("admin")
                .password("superSecret1!")
                .build();

        var result = restTestClient.post()
                .uri(AUTHENTICATE_URL)
                .body(authenticateDTO)
                .exchange()
                .expectBody(MessageDTO.class)
                .returnResult();

        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getResponseBody()).isNotNull();
        assertThat(result.getResponseBody().getMessage()).isNotBlank();
    }

    @Test
    void userCanRegisterAndImmediatelyAccessProtectedEndpoints() {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis()).substring(7);
        CreateAccountDTO createAccountDTO = populate(CreateAccountDTO.class, "email", String.class, () -> "test" + uniqueSuffix + "@example.com")
                .toBuilder()
                .username("newuser" + uniqueSuffix)
                .password("superSecretPassword123!")
                .build();

        var registerResult = restTestClient.post()
                .uri(REGISTER_URL)
                .body(createAccountDTO)
                .exchange()
                .expectBody(MessageDTO.class)
                .returnResult();

        assertThat(registerResult.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(registerResult.getResponseBody()).isNotNull();
        String tokenValue = registerResult.getResponseBody().getMessage();
        assertThat(tokenValue).isNotBlank();

        ResponseEntity<CategoryDTO[]> categoriesResponse = getWithToken(
                CATEGORIES_URL,
                CategoryDTO[].class,
                new JwtToken(tokenValue)
        );
        assertThat(categoriesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
