package com.github.anhem.howto.controller.model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class AccountsDTO {

    Integer accountCount;
    @Singular
    List<AccountDTO> accounts;
}
