package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.UpdateReplyDTO;
import com.github.anhem.howto.model.Reply;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class UpdateReplyDTOMapperTest {

    @Test
    void mappedToModel() {
        UpdateReplyDTO updateReplyDTO = populate(UpdateReplyDTO.class);
        Reply reply = populate(Reply.class);

        Reply updatedReply = UpdateReplyDTOMapper.mapToReply(updateReplyDTO, reply);

        assertThat(updatedReply).hasNoNullFieldsOrProperties();
        assertThat(updatedReply).isNotEqualTo(reply);
    }

}