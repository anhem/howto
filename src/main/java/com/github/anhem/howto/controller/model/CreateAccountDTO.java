package com.github.anhem.howto.controller.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class CreateAccountDTO {

    @Size(min = 6, max = 30)
    @NotNull
    String username;
    @Email
    @NotNull
    String email;
    @Size(min = 1, max = 100)
    String firstName;
    @Size(min = 1, max = 100)
    String lastName;
    @NotNull
    @Size(min = 1, max = 100)
    String password;

}
