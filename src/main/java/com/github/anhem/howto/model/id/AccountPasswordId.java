package com.github.anhem.howto.model.id;

import lombok.NonNull;

public record AccountPasswordId(@NonNull Integer value) implements Id<Integer> {

    public static final AccountPasswordId NEW_ACCOUNT_PASSWORD_ID = new AccountPasswordId(0);
}
