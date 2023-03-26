package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.CreatePostDTO;
import com.github.anhem.howto.model.Post;
import com.github.anhem.howto.model.id.AccountId;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.controller.mapper.CreatePostDTOMapper.mapToPost;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class CreatePostDTOMapperTest {

    @Test
    void mappedToModel() {
        CreatePostDTO createPostDTO = populate(CreatePostDTO.class);
        AccountId accountId = populate(AccountId.class);

        Post post = mapToPost(createPostDTO, accountId);

        assertThat(post).hasNoNullFieldsOrProperties();
    }

}