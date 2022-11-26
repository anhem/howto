package com.github.anhem.howto.controller.model;

import com.github.anhem.howto.model.id.Id;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MessageDTO {
    public static final MessageDTO OK = MessageDTO.builder()
            .message("OK")
            .build();

    String message;

    public static MessageDTO fromId(Id<?> id) {
        return MessageDTO.builder()
                .message(String.valueOf(id.value()))
                .build();
    }
}
