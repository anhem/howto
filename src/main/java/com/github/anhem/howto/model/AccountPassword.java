package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.AccountPasswordId;
import com.github.anhem.howto.model.id.Password;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class AccountPassword {

    @NonNull
    AccountPasswordId accountPasswordId;
    @NonNull
    AccountId accountId;
    @NonNull
    Password password;
    @NonNull
    Instant created;

}
