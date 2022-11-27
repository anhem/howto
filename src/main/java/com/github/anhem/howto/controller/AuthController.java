package com.github.anhem.howto.controller;

import com.github.anhem.howto.aggregator.AuthAggregator;
import com.github.anhem.howto.controller.model.AuthenticateDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/auth")
public class AuthController {

    private final AuthAggregator authAggregator;

    public AuthController(AuthAggregator authAggregator) {
        this.authAggregator = authAggregator;
    }

    @PostMapping(value = "authenticate")
    public MessageDTO authenticate(@RequestBody AuthenticateDTO authenticateDTO) {
        return MessageDTO.builder()
                .message(authAggregator.authenticateAndGetJwtToken(new UsernamePasswordAuthenticationToken(authenticateDTO.getUsername(), authenticateDTO.getPassword())))
                .build();
    }
}
