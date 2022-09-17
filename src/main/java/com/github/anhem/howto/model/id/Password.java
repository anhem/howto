package com.github.anhem.howto.model.id;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Password implements Id<String> {

    @NonNull
    String value;

    public static Password of(String username) {
        return Password.builder()
                .value(username)
                .build();
    }
}
