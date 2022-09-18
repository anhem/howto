package com.github.anhem.howto.model.id;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class AccountId implements Id<Integer> {

    public static final AccountId NEW_ACCOUNT_ID = AccountId.builder()
            .value(0)
            .build();

    @NonNull
    Integer value;

}
