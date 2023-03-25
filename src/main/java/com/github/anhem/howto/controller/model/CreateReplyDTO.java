package com.github.anhem.howto.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateReplyDTO {

    @NotNull
    Integer postId;
    @NotNull
    @Size(min = 1)
    String body;
}
