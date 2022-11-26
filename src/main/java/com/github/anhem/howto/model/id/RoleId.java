package com.github.anhem.howto.model.id;

import lombok.NonNull;

public record RoleId(@NonNull Integer value) implements Id<Integer> {

}
