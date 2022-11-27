package com.github.anhem.howto.configuration;

import com.github.anhem.howto.model.id.JwtToken;
import com.github.anhem.howto.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final String BEARER = "Bearer ";

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
                logger.error("Could not validate token", e);
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
