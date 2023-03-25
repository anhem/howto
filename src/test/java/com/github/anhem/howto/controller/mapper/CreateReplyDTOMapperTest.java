package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.CreateReplyDTO;
import com.github.anhem.howto.model.Reply;
import com.github.anhem.howto.model.id.AccountId;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class CreateReplyDTOMapperTest {

    @Test
    void mappedToModel() {
        CreateReplyDTO createReplyDTO = populate(CreateReplyDTO.class);
        AccountId accountId = populate(AccountId.class);

        Reply reply = CreateReplyDTOMapper.mapToReply(createReplyDTO, accountId);

        assertThat(reply).hasNoNullFieldsOrProperties();
    }

}