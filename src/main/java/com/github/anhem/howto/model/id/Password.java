package com.github.anhem.howto.model.id;

public record Password(String value) implements Id<String> {

    static final String BCRYPT = "{bcrypt}";

    public Password {
        if (value == null) {
            throw new NullPointerException("value is marked non-null but is null");
        }
        if (!value.startsWith(BCRYPT)) {
            throw new RuntimeException("Password not encoded!");
        }
    }
}
