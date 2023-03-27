package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.PostDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.Post;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.howto.controller.mapper.PostDTOMapper.mapToPostDTOs;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class PostDTOMapperTest {

    @Test
    void mappedToDTO() {
        Account account = populate(Account.class);
        Post post = populate(Post.class).toBuilder()
                .accountId(account.getAccountId())
                .build();

        List<PostDTO> postDTOs = mapToPostDTOs(List.of(post), List.of(account));

        assertThat(postDTOs).hasSize(1);
        assertThat(postDTOs.get(0)).hasNoNullFieldsOrProperties();
    }

}