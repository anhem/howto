package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.ReplyDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.Reply;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.howto.controller.mapper.ReplyDTOMapper.mapToReplyDTOs;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ReplyDTOMapperTest {

    @Test
    void mappedToDTO() {
        Account account = populate(Account.class);
        Reply reply = populate(Reply.class).toBuilder()
                .accountId(account.getAccountId())
                .build();

        List<ReplyDTO> replyDTOs = mapToReplyDTOs(List.of(reply), List.of(account));

        assertThat(replyDTOs).hasSize(1);
        AssertionsForClassTypes.assertThat(replyDTOs.get(0)).hasNoNullFieldsOrProperties();
    }

}