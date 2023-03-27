package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.UpdatePostDTO;
import com.github.anhem.howto.model.Post;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.controller.mapper.UpdatePostDTOMapper.mapToPost;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class UpdatePostDTOMapperTest {

    @Test
    void mappedToModel() {
        UpdatePostDTO updatePostDTO = populate(UpdatePostDTO.class);
        Post post = populate(Post.class);
        Post updatedPost = mapToPost(updatePostDTO, post);

        assertThat(updatedPost).hasNoNullFieldsOrProperties();
        assertThat(updatedPost).isNotEqualTo(post);
    }

}