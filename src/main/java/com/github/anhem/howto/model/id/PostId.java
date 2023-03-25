package com.github.anhem.howto.model.id;

import lombok.NonNull;

public record PostId(@NonNull Integer value) implements Id<Integer> {

    public static final PostId NEW_POST_ID = new PostId(0);
}
