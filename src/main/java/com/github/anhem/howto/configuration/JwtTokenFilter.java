package com.github.anhem.howto.configuration;

import com.github.anhem.howto.model.id.JwtToken;
import com.github.anhem.howto.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final String BEARER = "Bearer ";
    public static final String BEARER_AUTHENTICATION = BEARER + "Authentication";

    private final AuthService authService;

    public JwtTokenFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<JwtToken> jwtToken = getJwtToken(request);
        jwtToken.ifPresent(token -> {
            try {
                if (authService.validateToken(token)) {
                    authService.setSecurityContext(new WebAuthenticationDetailsSource().buildDetails(request), token);
                }
            } catch (Exception e) {
                log.error("Could not validate token", e);
            }
        });
        filterChain.doFilter(request, response);
    }

    private static Optional<JwtToken> getJwtToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(BEARER)) {
            return Optional.of(new JwtToken(bearerToken.substring(BEARER.length())));
        }
        return Optional.empty();
    }
}
