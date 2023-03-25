package com.github.anhem.howto.controller.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AuthenticateDTO {

    @NotEmpty
    String username;
    @NotEmpty
    String password;

}
