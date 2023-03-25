package com.github.anhem.howto.controller.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

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
