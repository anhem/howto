package com.github.anhem.howto.util;

import com.github.anhem.howto.model.id.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static com.github.anhem.howto.util.JwtUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    public static final String JWT_SECRET = "mySecret";
    public static final User USER = new User("username", "password", List.of(new SimpleGrantedAuthority(RoleName.USER_ROLE_NAME.getRole())));

    @Test
    void canGenerateAndValidateJwtToken() {
        String jwtToken = generateToken(USER, JWT_SECRET);

        assertThat(validateToken(jwtToken, JWT_SECRET)).isTrue();
    }

    @Test
    void canGetUsernameFromJwtToken() {
        String jwtToken = generateToken(USER, JWT_SECRET);

        assertThat(getUsername(jwtToken, JWT_SECRET).value()).isEqualTo(USER.getUsername());
    }

    @Test
    void canGetRolesFromJwtToken() {
        String jwtToken = generateToken(USER, JWT_SECRET);

        assertThat(getRoles(jwtToken, JWT_SECRET).toArray()).containsAll(USER.getAuthorities());
    }

}