package com.github.anhem.howto.model.id;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class AccountPasswordId implements Id<Integer> {

    public static final AccountPasswordId NEW_ACCOUNT_PASSWORD_ID = AccountPasswordId.builder()
            .value(0)
            .build();

    @NonNull
    Integer value;

}
