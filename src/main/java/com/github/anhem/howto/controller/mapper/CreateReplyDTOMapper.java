package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.CreateReplyDTO;
import com.github.anhem.howto.model.Reply;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.PostId;

import java.time.Instant;

import static com.github.anhem.howto.model.id.ReplyId.NEW_REPLY_ID;

public class CreateReplyDTOMapper {

    public static Reply mapToReply(CreateReplyDTO createReplyDTO, AccountId accountId) {
        Instant now = Instant.now();
        return Reply.builder()
                .replyId(NEW_REPLY_ID)
                .postId(new PostId(createReplyDTO.getPostId()))
                .accountId(accountId)
                .body(createReplyDTO.getBody())
                .created(now)
                .lastUpdated(now)
                .build();
    }
}
