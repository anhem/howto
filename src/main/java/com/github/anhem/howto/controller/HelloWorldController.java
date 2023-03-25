package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.MessageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.anhem.howto.configuration.JwtTokenFilter.BEARER_AUTHENTICATION;
import static com.github.anhem.howto.model.id.RoleName.Constants.*;

@RestController
@RequestMapping(value = "api/hello-world")
@SecurityRequirement(name = BEARER_AUTHENTICATION)
public class HelloWorldController {

    public static final String HELLO_WORLD_MESSAGE = "Hello world";
    public static final String HELLO_OTHER_WORLD_MESSAGE = "Hello other world";

    @Secured({USER, MODERATOR, ADMINISTRATOR})
    @GetMapping
    public MessageDTO helloWorld() {
        return MessageDTO.builder()
                .message(HELLO_WORLD_MESSAGE)
                .build();
    }

    @Secured(ADMINISTRATOR)
    @GetMapping(value = "other")
    public MessageDTO otherHelloWorld() {
        return MessageDTO.builder()
                .message(HELLO_OTHER_WORLD_MESSAGE)
                .build();
    }
}
