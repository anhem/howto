package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.CreatePostDTO;
import com.github.anhem.howto.model.Post;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.CategoryId;

import java.time.Instant;

import static com.github.anhem.howto.model.id.PostId.NEW_POST_ID;

public class CreatePostDTOMapper {

    public static Post mapToPost(CreatePostDTO createPostDTO, AccountId accountId) {
        Instant now = Instant.now();
        return Post.builder()
                .postId(NEW_POST_ID)
                .categoryId(new CategoryId(createPostDTO.getCategoryId()))
                .accountId(accountId)
                .title(createPostDTO.getTitle())
                .body(createPostDTO.getBody())
                .created(now)
                .lastUpdated(now)
                .build();
    }
}
