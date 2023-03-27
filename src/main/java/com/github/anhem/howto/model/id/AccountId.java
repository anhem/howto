package com.github.anhem.howto.model.id;

import lombok.NonNull;

public record AccountId(@NonNull Integer value) implements Id<Integer> {

    public static final AccountId NEW_ACCOUNT_ID = new AccountId(0);
}
