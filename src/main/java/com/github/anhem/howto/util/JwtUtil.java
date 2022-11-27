package com.github.anhem.howto.util;

import com.github.anhem.howto.model.id.JwtToken;
import com.github.anhem.howto.model.id.Username;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class JwtUtil {

    private static final int ONE_HOUR_IN_MS = 60 * 60 * 1000;
    private static final String ROLES = "ROLES";

    public static JwtToken generateToken(UserDetails userDetails, String jwtSecret) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLES, roles);
        return new JwtToken(
                Jwts.builder()
                        .setClaims(claims)
                        .setSubject(userDetails.getUsername())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + ONE_HOUR_IN_MS))
                        .signWith(SignatureAlgorithm.HS512, jwtSecret)
                        .compact()
        );
    }

    public static Boolean validateToken(JwtToken jwtToken, String jwtSecret) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken.value());
            return true;
        } catch (Exception e) {
            log.warn("Token invalid", e);
            return false;
        }
    }

    public static Username getUsername(JwtToken jwtToken, String jwtSecret) {
        return new Username(getClaims(jwtToken, jwtSecret).getSubject());
    }

    public static List<SimpleGrantedAuthority> getRoles(JwtToken jwtToken, String jwtSecret) {
        return ((List<String>) getClaims(jwtToken, jwtSecret).getOrDefault(ROLES, Collections.emptyList())).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private static Claims getClaims(JwtToken jwtToken, String jwtSecret) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwtToken.value())
                .getBody();
    }

}
