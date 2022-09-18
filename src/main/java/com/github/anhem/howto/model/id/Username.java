package com.github.anhem.howto.model.id;

import lombok.NonNull;

public record Username(@NonNull String value) implements Id<String> {

}
