package com.github.anhem.howto.controller.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@Builder
@Jacksonized
public class PostDTO {

    Integer postId;
    Integer categoryId;
    String username;
    String title;
    String body;
    Instant created;
    Instant updated;
}
