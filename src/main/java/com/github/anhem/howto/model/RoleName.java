package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RoleName implements Id<String> {

    USER(Constants.USER),
    MODERATOR(Constants.MODERATOR),
    ADMINISTRATOR(Constants.ADMINISTRATOR);

    private final String role;

    public static RoleName fromName(String name) {
        return Arrays.stream(RoleName.values())
                .filter(roleName -> roleName.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Invalid roleName %s", name)));
    }

    public static RoleName fromRole(String role) {
        String value = role.replace("ROLE_", "");
        return Arrays.stream(RoleName.values())
                .filter(roleName -> roleName.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Invalid value %s", role)));
    }

    public static class Constants {
        public static final String USER = "ROLE_USER";
        public static final String MODERATOR = "ROLE_MODERATOR";
        public static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    }

    @Override
    public String value() {
        return this.name();
    }
}
