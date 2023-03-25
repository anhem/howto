package com.github.anhem.howto.controller;

import com.github.anhem.howto.aggregator.AuthAggregator;
import com.github.anhem.howto.controller.model.AuthenticateDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import com.github.anhem.howto.model.id.JwtToken;
import jakarta.validation.Valid;
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
    public MessageDTO authenticate(@Valid @RequestBody AuthenticateDTO authenticateDTO) {
        JwtToken jwtToken = authAggregator.authenticateAndGetJwtToken(new UsernamePasswordAuthenticationToken(authenticateDTO.getUsername(), authenticateDTO.getPassword()));
        return MessageDTO.builder()
                .message(jwtToken.value())
                .build();
    }
}
