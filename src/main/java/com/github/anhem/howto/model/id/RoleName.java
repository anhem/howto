package com.github.anhem.howto.model.id;

import lombok.NonNull;

public record RoleName(@NonNull String value) implements Id<String> {

    public static final RoleName USER_ROLE_NAME = new RoleName("USER");
    public static final RoleName MODERATOR_ROLE_NAME = new RoleName("MODERATOR");
    public static final RoleName ADMINISTRATOR_ROLE_NAME = new RoleName("ADMINISTRATOR");

    public String getRole() {
        return String.format("ROLE_%s", this.value);
    }

}
