package com.github.anhem.howto.controller.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CategoryDTO {

    Integer categoryId;
    String name;
    String description;
}
