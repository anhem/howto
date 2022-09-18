package com.github.anhem.howto.model.id;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
public class Password implements Id<String> {

    static final String BCRYPT = "{bcrypt}";

    @NonNull
    String value;

    @Builder
    public Password(String value) {
        if (!value.startsWith(BCRYPT)) {
            throw new RuntimeException("Password not encoded!");
        }
        this.value = value;
    }
}
