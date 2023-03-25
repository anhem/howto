package com.github.anhem.howto.model.id;

import lombok.NonNull;

public record ReplyId(@NonNull Integer value) implements Id<Integer> {

    public static final ReplyId NEW_REPLY_ID = new ReplyId(0);
}
