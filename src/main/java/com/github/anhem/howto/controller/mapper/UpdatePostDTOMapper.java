package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.UpdatePostDTO;
import com.github.anhem.howto.model.Post;

import java.time.Instant;

public class UpdatePostDTOMapper {

    public static Post mapToPost(UpdatePostDTO updatePostDTO, Post post) {
        return post.toBuilder()
                .title(updatePostDTO.getTitle())
                .body(updatePostDTO.getBody())
                .lastUpdated(Instant.now())
                .build();
    }
}
