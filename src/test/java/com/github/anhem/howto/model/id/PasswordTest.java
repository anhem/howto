package com.github.anhem.howto.model.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordTest {

    public static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    public static final String RAW_PASSWORD = "abc123";

    @Test
    void nullPointerExceptionWhenValueIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> Password.builder().build());
    }

    @Test
    void runtimeExceptionWhenValueIsEmpty() {
        Assertions.assertThrows(RuntimeException.class, () -> Password.builder()
                .value("")
                .build());
    }

    @Test
    void runtimeExceptionWhenValueNotEncoded() {
        Assertions.assertThrows(RuntimeException.class, () -> Password.builder()
                .value(RAW_PASSWORD)
                .build());
    }

    @Test
    void passwordCreatedWhenValueIsEncoded() {
        String encodePassword = PASSWORD_ENCODER.encode(RAW_PASSWORD);

        Password password = Password.builder()
                .value(encodePassword)
                .build();

        Password passwordOf = Id.of(Password.class, encodePassword);

        assertThat(password).isEqualTo(passwordOf);
    }
}