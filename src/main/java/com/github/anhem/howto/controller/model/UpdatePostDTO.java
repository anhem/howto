package com.github.anhem.howto.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class UpdatePostDTO {

    @NotNull
    @Size(min = 1, max = 100)
    String title;
    @NotNull
    @Size(min = 1)
    String body;


}
