package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.MessageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/hello-world")
@SecurityRequirement(name = "Bearer Authentication")
public class HelloWorldController {

    @GetMapping
    public MessageDTO helloWorld() {
        return MessageDTO.builder()
                .message("Hello world")
                .build();
    }

    @GetMapping(value = "other-hello")
    public MessageDTO helloWorld2() {
        return MessageDTO.builder()
                .message("Hello other world")
                .build();
    }
}
