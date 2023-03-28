package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.MessageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.anhem.howto.configuration.JwtTokenFilter.BEARER_AUTHENTICATION;
import static com.github.anhem.howto.model.RoleName.Constants.*;

@RestController
@RequestMapping(value = "api/hello-world")
@SecurityRequirement(name = BEARER_AUTHENTICATION)
public class HelloWorldController {

    public static final String HELLO_WORLD = "Hello world";
    public static final String HELLO_AUTHENTICATED_USER = "Hello authenticated user";
    public static final String HELLO_ADMINISTRATOR = "Hello administrator";

    @GetMapping()
    public MessageDTO hello() {
        return MessageDTO.builder()
                .message(HELLO_WORLD)
                .build();
    }

    @Secured({USER, MODERATOR, ADMINISTRATOR})
    @GetMapping("authenticated")
    public MessageDTO helloAuthenticated() {
        return MessageDTO.builder()
                .message(HELLO_AUTHENTICATED_USER)
                .build();
    }

    @Secured(ADMINISTRATOR)
    @GetMapping(value = "authenticated/administrator")
    public MessageDTO helloAdministrator() {
        return MessageDTO.builder()
                .message(HELLO_ADMINISTRATOR)
                .build();
    }
}
