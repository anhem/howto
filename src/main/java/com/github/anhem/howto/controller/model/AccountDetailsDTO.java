package com.github.anhem.howto.controller.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@Jacksonized
public class AccountDetailsDTO {

    AccountDTO account;
    Instant created;
    Instant updated;
    List<String> roles;
}
