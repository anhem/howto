package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.ReplyDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.Reply;
import com.github.anhem.howto.model.id.Username;

import java.util.List;
import java.util.stream.Collectors;

public class ReplyDTOMapper {

    public static List<ReplyDTO> mapToReplyDTOs(List<Reply> replies, List<Account> accounts) {
        return replies.stream()
                .map(reply -> mapToReplyDTO(reply, accounts))
                .collect(Collectors.toList());
    }

    public static ReplyDTO mapToReplyDTO(Reply reply, List<Account> accounts) {
        Username username = accounts.stream()
                .filter(account -> account.getAccountId().equals(reply.getAccountId()))
                .findFirst()
                .map(Account::getUsername)
                .orElse(Username.UNKNOWN);

        return ReplyDTO.builder()
                .replyId(reply.getReplyId().value())
                .postId(reply.getPostId().value())
                .username(username.value())
                .body(reply.getBody())
                .created(reply.getCreated())
                .build();
    }
}
