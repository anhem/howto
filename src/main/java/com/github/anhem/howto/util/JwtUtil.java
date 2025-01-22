package com.github.anhem.howto.util;

import com.github.anhem.howto.model.id.JwtToken;
import com.github.anhem.howto.model.id.Username;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class JwtUtil {

    private static final int ONE_HOUR_IN_MS = 60 * 60 * 1000;
    private static final String ROLES = "ROLES";

    public static JwtToken generateToken(UserDetails userDetails, String jwtSecret) {
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new JwtToken(
                Jwts.builder()
                        .claims(Map.of(ROLES, roles))
                        .subject(userDetails.getUsername())
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + ONE_HOUR_IN_MS))
                        .signWith(toKey(jwtSecret))
                        .compact()
        );
    }

    public static Boolean validateToken(JwtToken jwtToken, String jwtSecret) {
        try {
            getClaims(jwtToken, jwtSecret);
            return true;
        } catch (Exception e) {
            log.warn("Token invalid", e);
            return false;
        }
    }

    public static Username getUsername(JwtToken jwtToken, String jwtSecret) {
        return new Username(getClaims(jwtToken, jwtSecret).getSubject());
    }

    @SuppressWarnings("unchecked")
    public static List<SimpleGrantedAuthority> getRoles(JwtToken jwtToken, String jwtSecret) {
        return ((List<String>) getClaims(jwtToken, jwtSecret).getOrDefault(ROLES, Collections.emptyList())).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private static Claims getClaims(JwtToken jwtToken, String jwtSecret) {
        return Jwts.parser()
                .verifyWith(toKey(jwtSecret))
                .build()
                .parseSignedClaims(jwtToken.value())
                .getPayload();
    }

    private static SecretKey toKey(String jwtSecret) {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

}
