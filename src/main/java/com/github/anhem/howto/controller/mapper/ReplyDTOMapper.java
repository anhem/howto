package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.ReplyDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.Reply;
import com.github.anhem.howto.model.id.AccountId;

import java.util.List;
import java.util.stream.Collectors;

public class ReplyDTOMapper {

    public static List<ReplyDTO> mapToReplyDTOs(List<Reply> replies, List<Account> accounts) {
        return replies.stream()
                .map(reply -> mapToReplyDTO(reply, accounts))
                .collect(Collectors.toList());
    }

    public static ReplyDTO mapToReplyDTO(Reply reply, List<Account> accounts) {
        AccountId accountId = accounts.stream()
                .filter(account -> account.getAccountId().equals(reply.getAccountId()))
                .findFirst()
                .map(Account::getAccountId)
                .orElse(AccountId.UNKNOWN_ACCOUNT_ID);

        return ReplyDTO.builder()
                .replyId(reply.getReplyId().value())
                .accountId(accountId.value())
                .body(reply.getBody())
                .created(reply.getCreated())
                .build();
    }
}
