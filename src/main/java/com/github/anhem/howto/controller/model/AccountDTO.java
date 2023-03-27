package com.github.anhem.howto.controller.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AccountDTO {

    Integer id;
    String username;
    String email;
    String firstName;
    String lastName;

}
