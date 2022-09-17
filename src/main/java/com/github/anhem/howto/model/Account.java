package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Username;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class Account {

    @NonNull
    AccountId accountId;
    @NonNull
    Username username;
    @NonNull
    String email;
    String firstName;
    String lastName;
    @NonNull
    Instant created;
    @NonNull
    Instant lastUpdated;
    Instant lastLogin;

}
