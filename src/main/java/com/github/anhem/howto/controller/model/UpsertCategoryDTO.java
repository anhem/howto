package com.github.anhem.howto.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpsertCategoryDTO {
    @Size(min = 1, max = 100)
    @NotNull
    String name;
    @Size(max = 320)
    String description;
}
