package com.github.anhem.howto.controller.api;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AccountDTO {

    int id;
    String username;
    String email;
    String firstName;
    String lastName;

}
