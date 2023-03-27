package com.github.anhem.howto.controller.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@Builder
@Jacksonized
public class ReplyDTO {

    Integer replyId;
    Integer postId;
    String username;
    String body;
    Instant created;
    Instant updated;
}
