package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.*;
import com.github.anhem.howto.testutil.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class ForumControllerIT extends TestApplication {

    private static final String GET_CATEGORIES_URL = "/api/forum/categories";
    private static final String CATEGORY_URL = "/api/forum/categories/%d";
    private static final String CREATE_POST_URL = "/api/forum/posts";
    private static final String GET_POSTS_URL = "/api/forum/posts/category/%d";
    private static final String POST_URL = "/api/forum/posts/%d";
    private static final String CREATE_REPLY_URL = "/api/forum/posts/%d/replies";
    private static final String GET_REPLIES_URL = "/api/forum/posts/%d/replies";
    private static final String REPLY_URL = "/api/forum/replies/%d";

    @Test
    void categoryCrd() {
        int categoryId = createCategory();

        getCategory(categoryId, true);
        List<CategoryDTO> categoriesAfterCreate = getCategories();
        assertThat(findCategoryDTO(categoryId, categoriesAfterCreate)).isPresent();

        deleteCategory(categoryId);

        getCategory(categoryId, false);
        List<CategoryDTO> categoriesAfterDelete = getCategories();
        assertThat(findCategoryDTO(categoryId, categoriesAfterDelete)).isEmpty();
    }

    @Test
    void postCrd() {
        int categoryId = createCategory();
        int postId = createPost(categoryId);

        getPost(postId, true);
        List<PostDTO> postsAfterCreate = getPosts(categoryId);
        assertThat(findPostDTO(postId, postsAfterCreate)).isPresent();

        deletePost(postId);

        getPost(postId, false);
        List<PostDTO> postsAfterDelete = getPosts(categoryId);
        assertThat(findPostDTO(postId, postsAfterDelete)).isEmpty();
    }

    @Test
    void replyCrd() {
        int postId = createPost(createCategory());
        int replyId = createReply(postId);

        getReply(replyId, true);
        List<ReplyDTO> repliesAfterCreate = getReplies(postId);
        assertThat(findReplyDTO(replyId, repliesAfterCreate)).isPresent();

        deleteReply(replyId);

        getReply(replyId, false);
        List<ReplyDTO> repliesAfterDelete = getReplies(postId);
        assertThat(findReplyDTO(replyId, repliesAfterDelete)).isEmpty();
    }

    private List<CategoryDTO> getCategories() {
        ResponseEntity<CategoryDTO[]> response = getWithToken(GET_CATEGORIES_URL, CategoryDTO[].class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private Optional<CategoryDTO> getCategory(int categoryId, boolean shouldExist) {
        if (shouldExist) {
            ResponseEntity<CategoryDTO> response = getWithToken(String.format(CATEGORY_URL, categoryId), CategoryDTO.class, userJwtToken);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            return Optional.of(response.getBody());
        } else {
            ResponseEntity<ErrorDTO> response = getWithToken(String.format(CATEGORY_URL, categoryId), ErrorDTO.class, userJwtToken);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
            return Optional.empty();
        }
    }

    private int createCategory() {
        ResponseEntity<MessageDTO> response = postWithToken(GET_CATEGORIES_URL, populate(CreateCategoryDTO.class), MessageDTO.class, adminJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        int categoryId = Integer.parseInt(response.getBody().getMessage());
        assertThat(categoryId).isGreaterThan(0);
        return categoryId;
    }

    private void deleteCategory(int categoryId) {
        ResponseEntity<MessageDTO> deleteResponse = deleteWithToken(String.format(CATEGORY_URL, categoryId), MessageDTO.class, adminJwtToken);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private static Optional<CategoryDTO> findCategoryDTO(int categoryId, List<CategoryDTO> categoryDTOs) {
        return categoryDTOs.stream()
                .filter(categoryDTO -> categoryDTO.getCategoryId() == categoryId)
                .findFirst();
    }

    private int createPost(int categoryId) {
        CreatePostDTO createPostDTO = populate(CreatePostDTO.class)
                .toBuilder()
                .categoryId(categoryId)
                .build();
        ResponseEntity<MessageDTO> response = postWithToken(CREATE_POST_URL, createPostDTO, MessageDTO.class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        int postId = Integer.parseInt(response.getBody().getMessage());
        assertThat(postId).isGreaterThan(0);

        return postId;
    }

    private List<PostDTO> getPosts(int categoryId) {
        ResponseEntity<PostDTO[]> response = getWithToken(String.format(GET_POSTS_URL, categoryId), PostDTO[].class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private Optional<PostDTO> getPost(int postId, boolean shouldExist) {
        if (shouldExist) {
            ResponseEntity<PostDTO> response = getWithToken(String.format(POST_URL, postId), PostDTO.class, userJwtToken);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            return Optional.of(response.getBody());
        } else {
            ResponseEntity<ErrorDTO> response = getWithToken(String.format(POST_URL, postId), ErrorDTO.class, userJwtToken);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
            return Optional.empty();
        }
    }

    private static Optional<PostDTO> findPostDTO(int postId, List<PostDTO> postDTOs) {
        return postDTOs.stream().filter(postDTO -> postDTO.getPostId() == postId)
                .findFirst();
    }

    private void deletePost(int postId) {
        ResponseEntity<MessageDTO> deleteResponse = deleteWithToken(String.format(POST_URL, postId), MessageDTO.class, userJwtToken);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private int createReply(int postId) {
        CreateReplyDTO createReplyDTO = populate(CreateReplyDTO.class)
                .toBuilder()
                .postId(postId)
                .build();
        ResponseEntity<MessageDTO> response = postWithToken(String.format(CREATE_REPLY_URL, postId), createReplyDTO, MessageDTO.class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        int replyId = Integer.parseInt(response.getBody().getMessage());
        assertThat(replyId).isGreaterThan(0);
        return replyId;
    }

    private List<ReplyDTO> getReplies(int postId) {
        ResponseEntity<ReplyDTO[]> response = getWithToken(String.format(GET_REPLIES_URL, postId), ReplyDTO[].class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private Optional<ReplyDTO> getReply(int replyId, boolean shouldExist) {
        if (shouldExist) {
            ResponseEntity<ReplyDTO> response = getWithToken(String.format(REPLY_URL, replyId), ReplyDTO.class, userJwtToken);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            return Optional.of(response.getBody());
        } else {
            ResponseEntity<ErrorDTO> response = getWithToken(String.format(REPLY_URL, replyId), ErrorDTO.class, userJwtToken);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
            return Optional.empty();
        }
    }

    private static Optional<ReplyDTO> findReplyDTO(int replyId, List<ReplyDTO> replyDTOs) {
        return replyDTOs.stream().filter(replyDTO -> replyDTO.getReplyId() == replyId)
                .findFirst();
    }

    private void deleteReply(int replyId) {
        ResponseEntity<MessageDTO> deleteResponse = deleteWithToken(String.format(REPLY_URL, replyId), MessageDTO.class, userJwtToken);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}