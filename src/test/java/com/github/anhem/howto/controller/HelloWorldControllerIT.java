package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.ErrorDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import com.github.anhem.howto.testutil.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.github.anhem.howto.controller.HelloWorldController.*;
import static com.github.anhem.howto.controller.model.ErrorCode.ACCESS_DENIED;
import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldControllerIT extends TestApplication {

    public static final String GET_URL = "/api/hello-world";
    public static final String GET_AUTHENTICATED_URL = "/api/hello-world/authenticated";
    public static final String GET_ADMINISTRATOR_URL = "/api/hello-world/authenticated/administrator";

    @Test
    public void unauthenticatedCanGetHelloWorld() {
        ResponseEntity<MessageDTO> response = testRestTemplate.getForEntity(GET_URL, MessageDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(HELLO_WORLD);
    }

    @Test
    void userCanGetHelloAuthenticated() {
        ResponseEntity<MessageDTO> response = getWithToken(GET_AUTHENTICATED_URL, MessageDTO.class, userJwtToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(HELLO_AUTHENTICATED_USER);
    }

    @Test
    void adminCanGetHelloAuthenticated() {
        ResponseEntity<MessageDTO> response = getWithToken(GET_AUTHENTICATED_URL, MessageDTO.class, adminJwtToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(HELLO_AUTHENTICATED_USER);
    }

    @Test
    void adminCanGetHelloAdministrator() {
        ResponseEntity<MessageDTO> response = getWithToken(GET_ADMINISTRATOR_URL, MessageDTO.class, adminJwtToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(HELLO_ADMINISTRATOR);
    }

    @Test
    void userCannotGetHelloAdministrator() {
        ResponseEntity<ErrorDTO> response = getWithToken(GET_ADMINISTRATOR_URL, ErrorDTO.class, userJwtToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ACCESS_DENIED);
    }

}