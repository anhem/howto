package com.github.anhem.howto.controller.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;

@Value
@Builder
@Jacksonized
public class AuthenticateDTO {

    @NotEmpty
    String username;
    @NotEmpty
    String password;

}
