package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.UpdateReplyDTO;
import com.github.anhem.howto.model.Reply;

import java.time.Instant;

public class UpdateReplyDTOMapper {

    public static Reply mapToReply(UpdateReplyDTO updateReplyDTO, Reply reply) {
        return reply.toBuilder()
                .body(updateReplyDTO.getBody())
                .lastUpdated(Instant.now())
                .build();
    }
}
