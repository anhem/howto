package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.ErrorDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import com.github.anhem.howto.testutil.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.github.anhem.howto.controller.HelloWorldController.HELLO_OTHER_WORLD_MESSAGE;
import static com.github.anhem.howto.controller.HelloWorldController.HELLO_WORLD_MESSAGE;
import static com.github.anhem.howto.controller.model.ErrorCode.ACCESS_DENIED;
import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldControllerIT extends TestApplication {

    public static final String GET_HELLO_WORLD_URL = "/api/hello-world";
    public static final String GET_OTHER_HELLO_WORLD_URL = "/api/hello-world/other";

    @Test
    void userCanGetHelloWorld() {
        ResponseEntity<MessageDTO> response = getWithToken(GET_HELLO_WORLD_URL, MessageDTO.class, userJwtToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(HELLO_WORLD_MESSAGE);
    }

    @Test
    void userCannotGetOtherHelloWorld() {
        ResponseEntity<ErrorDTO> response = getWithToken(GET_OTHER_HELLO_WORLD_URL, ErrorDTO.class, userJwtToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ACCESS_DENIED);
    }

    @Test
    void adminCanGetHelloWorld() {
        ResponseEntity<MessageDTO> response = getWithToken(GET_HELLO_WORLD_URL, MessageDTO.class, adminJwtToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(HELLO_WORLD_MESSAGE);
    }

    @Test
    void adminCannotGetOtherHelloWorld() {
        ResponseEntity<MessageDTO> response = getWithToken(GET_OTHER_HELLO_WORLD_URL, MessageDTO.class, adminJwtToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(HELLO_OTHER_WORLD_MESSAGE);
    }

}