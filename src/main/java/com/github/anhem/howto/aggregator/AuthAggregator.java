package com.github.anhem.howto.aggregator;

import com.github.anhem.howto.controller.mapper.CreateAccountDTOMapper;
import com.github.anhem.howto.controller.model.CreateAccountDTO;
import com.github.anhem.howto.model.id.JwtToken;
import com.github.anhem.howto.model.id.Password;
import com.github.anhem.howto.service.AccountService;
import com.github.anhem.howto.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthAggregator {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public AuthAggregator(AuthenticationManager authenticationManager,
                          AuthService authService,
                          AccountService accountService,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    public JwtToken authenticateAndGetJwtToken(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        return authService.generateToken((UserDetails) authentication.getPrincipal());
    }

    public JwtToken registerAndGetJwtToken(CreateAccountDTO createAccountDTO) {
        accountService.createUserAccount(
                CreateAccountDTOMapper.mapToAccount(createAccountDTO),
                new Password(passwordEncoder.encode(createAccountDTO.getPassword()))
        );
        return authenticateAndGetJwtToken(
                new UsernamePasswordAuthenticationToken(createAccountDTO.getUsername(), createAccountDTO.getPassword())
        );
    }
}
