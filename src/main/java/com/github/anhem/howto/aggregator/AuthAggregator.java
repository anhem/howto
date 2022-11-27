package com.github.anhem.howto.aggregator;

import com.github.anhem.howto.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthAggregator {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    public AuthAggregator(AuthenticationManager authenticationManager, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    public String authenticateAndGetJwtToken(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        return authService.generateToken((UserDetails) authentication.getPrincipal());
    }

}
