package com.github.anhem.howto.model.id;

import lombok.NonNull;

public record CategoryId(@NonNull Integer value) implements Id<Integer> {

    public static final CategoryId NEW_CATEGORY_ID = new CategoryId(0);

}
