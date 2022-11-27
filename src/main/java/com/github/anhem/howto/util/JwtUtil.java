package com.github.anhem.howto.util;

import com.github.anhem.howto.model.id.Username;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class JwtUtil {

    private static final int ONE_HOUR_IN_MS = 60 * 60 * 1000;
    private static final String ROLES = "ROLES";

    public static String generateToken(UserDetails userDetails, String jwtSecret) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLES, roles);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ONE_HOUR_IN_MS))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public static Boolean validateToken(String jwtToken, UserDetails userDetails, String jwtSecret) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwtToken)
                .getBody();

        return claims.getSubject().equals(userDetails.getUsername()) && claims.getExpiration().after(new Date());
    }

    public static Username getUsername(String jwtToken, String jwtSecret) {
        return new Username(getClaims(jwtToken, jwtSecret).getSubject());
    }

    public static List<SimpleGrantedAuthority> getRoles(String jwtToken, String jwtSecret) {
        return ((List<String>) getClaims(jwtToken, jwtSecret).getOrDefault(ROLES, Collections.emptyList())).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private static Claims getClaims(String jwtToken, String jwtSecret) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwtToken)
                .getBody();
    }

}
