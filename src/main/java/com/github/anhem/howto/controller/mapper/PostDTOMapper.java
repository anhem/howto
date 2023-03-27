package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.PostDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.Post;
import com.github.anhem.howto.model.id.Username;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.howto.model.id.Username.UNKNOWN;

public class PostDTOMapper {

    public static List<PostDTO> mapToPostDTOs(List<Post> posts, List<Account> accounts) {
        return posts.stream()
                .map(post -> mapToPostDTO(post, accounts))
                .collect(Collectors.toList());
    }

    public static PostDTO mapToPostDTO(Post post, List<Account> accounts) {
        Username username = accounts.stream()
                .filter(account -> account.getAccountId().equals(post.getAccountId()))
                .findFirst()
                .map(Account::getUsername)
                .orElse(UNKNOWN);

        return PostDTO.builder()
                .postId(post.getPostId().value())
                .categoryId(post.getCategoryId().value())
                .authorUsername(username.value())
                .title(post.getTitle())
                .body(post.getBody())
                .created(post.getCreated())
                .build();
    }
}
