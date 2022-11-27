package com.github.anhem.howto.model.id;

import lombok.NonNull;

public record JwtToken(@NonNull String value) implements Id<String> {
}
