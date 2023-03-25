package com.github.anhem.howto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RoleName {

    USER(Constants.USER),
    MODERATOR(Constants.MODERATOR),
    ADMINISTRATOR(Constants.ADMINISTRATOR);

    private final String value;

    public static RoleName fromString(String value) {
        return Arrays.stream(RoleName.values())
                .filter(roleName -> roleName.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Invalid roleName %s", value)));
    }

    public static class Constants {
        public static final String USER = "ROLE_USER";
        public static final String MODERATOR = "ROLE_MODERATOR";
        public static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    }
}
