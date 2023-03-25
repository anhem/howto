package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.*;
import com.github.anhem.howto.testutil.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ForumControllerIT extends TestApplication {

    private static final String CATEGORY_URL = "/api/forum/categories";
    private static final String DELETE_CATEGORY_URL = "/api/forum/categories/%d";
    private static final String CREATE_POST_URL = "/api/forum/posts";
    private static final String GET_POSTS_URL = "/api/forum/posts/category/%d";

    @Test
    void categoryCrd() {
        CreateCategoryDTO createCategoryDTO = CreateCategoryDTO.builder()
                .name("name")
                .description("description")
                .build();
        ResponseEntity<MessageDTO> createReponse = postWithToken(CATEGORY_URL, createCategoryDTO, MessageDTO.class, adminJwtToken);

        assertThat(createReponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createReponse.getBody()).isNotNull();
        int categoryId = Integer.parseInt(createReponse.getBody().getMessage());
        assertThat(categoryId).isGreaterThan(0);

        ResponseEntity<CategoryDTO[]> getResponse = getWithToken(CATEGORY_URL, CategoryDTO[].class, userJwtToken);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody()).hasSize(1);
        CategoryDTO categoryDTO = getResponse.getBody()[0];
        assertThat(categoryDTO.getName()).isEqualTo(createCategoryDTO.getName());
        assertThat(categoryDTO.getDescription()).isEqualTo(createCategoryDTO.getDescription());

        ResponseEntity<MessageDTO> deleteResponse = deleteWithToken(String.format(DELETE_CATEGORY_URL, categoryDTO.getCategoryId()), MessageDTO.class, adminJwtToken);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<CategoryDTO[]> getAfterDeleteResponse = getWithToken(CATEGORY_URL, CategoryDTO[].class, userJwtToken);
        assertThat(getAfterDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAfterDeleteResponse.getBody()).isNotNull();
        assertThat(Arrays.stream(getAfterDeleteResponse.getBody()).filter(c -> c.getName().equals(createCategoryDTO.getName()))).hasSize(0);
    }

    @Test
    void postCrd() {
        CreateCategoryDTO createCategoryDTO = CreateCategoryDTO.builder()
                .name("postCategory")
                .description("description")
                .build();
        ResponseEntity<MessageDTO> categoryResponse = postWithToken(CATEGORY_URL, createCategoryDTO, MessageDTO.class, adminJwtToken);
        assertThat(categoryResponse.getBody()).isNotNull();
        int categoryId = Integer.parseInt(categoryResponse.getBody().getMessage());

        CreatePostDTO createPostDTO = CreatePostDTO.builder()
                .title("title")
                .body("body")
                .categoryId(categoryId)
                .build();

        ResponseEntity<MessageDTO> createPostResponse = postWithToken(CREATE_POST_URL, createPostDTO, MessageDTO.class, userJwtToken);

        assertThat(createPostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createPostResponse.getBody()).isNotNull();
        int postId = Integer.parseInt(createPostResponse.getBody().getMessage());

        ResponseEntity<PostDTO[]> withToken = getWithToken(String.format(GET_POSTS_URL, categoryId), PostDTO[].class, userJwtToken);

        assertThat(withToken.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(withToken.getBody()).isNotNull();
        assertThat(withToken.getBody()).hasSize(1);
        PostDTO postDTO = withToken.getBody()[0];
        assertThat(postDTO.getTitle()).isEqualTo(createPostDTO.getTitle());
        assertThat(postDTO.getBody()).isEqualTo(createPostDTO.getBody());

    }

}