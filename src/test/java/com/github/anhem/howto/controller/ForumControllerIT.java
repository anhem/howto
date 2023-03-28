package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.*;
import com.github.anhem.howto.model.id.JwtToken;
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
    private static final String GET_POSTS_URL = "/api/forum/categories/%d/posts";
    private static final String POST_URL = "/api/forum/posts/%d";
    private static final String CREATE_REPLY_URL = "/api/forum/replies";
    private static final String GET_REPLIES_URL = "/api/forum/posts/%d/replies";
    private static final String REPLY_URL = "/api/forum/replies/%d";

    @Test
    void categoryCrud() {
        int categoryId = createCategory();

        CategoryDTO categoryDTO = assertAndGetCategory(categoryId, true).get();
        assertThat(findCategoryDTO(categoryId, getCategories())).isPresent();

        CategoryDTO updatedCategoryDTO = updateCategory(categoryId);
        assertThat(updatedCategoryDTO.getCategoryId()).isEqualTo(categoryDTO.getCategoryId());
        assertThat(updatedCategoryDTO).isNotEqualTo(categoryDTO);
        assertThat(updatedCategoryDTO).isEqualTo(assertAndGetCategory(categoryId, true).get());

        deleteCategory(categoryId);

        assertAndGetCategory(categoryId, false);
        assertThat(findCategoryDTO(categoryId, getCategories())).isEmpty();
    }

    @Test
    void postCrud() {
        int categoryId = createCategory();
        int postId = createPost(categoryId);

        PostDTO postDTO = assertAndGetPost(postId, true).get();
        assertThat(findPostDTO(postId, getPosts(categoryId))).isPresent();

        PostDTO updatedPostDTO = updatePost(postId);
        assertThat(updatedPostDTO.getCategoryId()).isEqualTo(postDTO.getCategoryId());
        assertThat(updatedPostDTO).isNotEqualTo(postDTO);
        assertThat(updatedPostDTO).isEqualTo(assertAndGetPost(postId, true).get());

        deletePost(postId);

        assertAndGetPost(postId, false);
        assertThat(findPostDTO(postId, getPosts(categoryId))).isEmpty();
    }

    @Test
    void replyCrud() {
        int postId = createPost(createCategory());
        int replyId = createReply(postId);

        ReplyDTO replyDTO = assertAndGetReply(replyId, true).get();
        assertThat(findReplyDTO(replyId, getReplies(postId))).isPresent();

        ReplyDTO updatedReplyDTO = updateReply(replyId);
        assertThat(updatedReplyDTO.getReplyId()).isEqualTo(replyDTO.getReplyId());
        assertThat(updatedReplyDTO).isNotEqualTo(replyDTO);
        assertThat(updatedReplyDTO).isEqualTo(assertAndGetReply(replyId, true).get());

        deleteReply(replyId);

        assertAndGetReply(replyId, false);
        assertThat(findReplyDTO(replyId, getReplies(postId))).isEmpty();
    }

    @Test
    public void moderatorCanCreateButNotDeleteCategory() {
        int categoryId = createCategory(moderatorJwtToken);
        assertAndGetCategory(categoryId, true);

        ResponseEntity<ErrorDTO> response = deleteWithToken(String.format(CATEGORY_URL, categoryId), ErrorDTO.class, moderatorJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
    }

    @Test
    public void userCannotCreateCategory() {
        ResponseEntity<ErrorDTO> response = postWithToken(GET_CATEGORIES_URL, populate(UpsertCategoryDTO.class), ErrorDTO.class, userJwtToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
    }

    @Test
    public void moderatorCanDeletePostMadeByUser() {
        int categoryId = createCategory();
        int postId = createPost(categoryId);

        deletePost(postId, moderatorJwtToken);

        assertAndGetPost(postId, false);
    }

    @Test
    public void moderatorCanDeleteReplyMadeByUser() {
        int replyId = createReply(createPost(createCategory()));

        deleteReply(replyId, moderatorJwtToken);

        assertAndGetReply(replyId, false);
    }

    @Test
    public void administratorCanDeletePostMadeByUser() {
        int categoryId = createCategory();
        int postId = createPost(categoryId);

        deletePost(postId, adminJwtToken);

        assertAndGetPost(postId, false);
    }

    @Test
    public void administratorCanDeleteReplyMadeByUser() {
        int replyId = createReply(createPost(createCategory()));

        deleteReply(replyId, adminJwtToken);

        assertAndGetReply(replyId, false);
    }

    private List<CategoryDTO> getCategories() {
        ResponseEntity<CategoryDTO[]> response = getWithToken(GET_CATEGORIES_URL, CategoryDTO[].class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private Optional<CategoryDTO> assertAndGetCategory(int categoryId, boolean shouldExist) {
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
        return createCategory(adminJwtToken);
    }

    private CategoryDTO updateCategory(int categoryId) {
        UpsertCategoryDTO upsertCategoryDTO = populate(UpsertCategoryDTO.class);
        ResponseEntity<CategoryDTO> response = putWithToken(String.format(CATEGORY_URL, categoryId), upsertCategoryDTO, CategoryDTO.class, adminJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private int createCategory(JwtToken jwtToken) {
        ResponseEntity<MessageDTO> response = postWithToken(GET_CATEGORIES_URL, populate(UpsertCategoryDTO.class), MessageDTO.class, jwtToken);
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

    private PostDTO updatePost(int postId) {
        UpdatePostDTO updatePostDTO = populate(UpdatePostDTO.class);
        ResponseEntity<PostDTO> response = putWithToken(String.format(POST_URL, postId), updatePostDTO, PostDTO.class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUpdated()).isNotNull();
        return response.getBody();
    }

    private List<PostDTO> getPosts(int categoryId) {
        ResponseEntity<PostDTO[]> response = getWithToken(String.format(GET_POSTS_URL, categoryId), PostDTO[].class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private Optional<PostDTO> assertAndGetPost(int postId, boolean shouldExist) {
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
        deletePost(postId, userJwtToken);
    }

    private void deletePost(int postId, JwtToken jwtToken) {
        ResponseEntity<MessageDTO> deleteResponse = deleteWithToken(String.format(POST_URL, postId), MessageDTO.class, jwtToken);
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

    private ReplyDTO updateReply(int replyId) {
        UpdateReplyDTO updateReplyDTO = populate(UpdateReplyDTO.class);
        ResponseEntity<ReplyDTO> response = putWithToken(String.format(REPLY_URL, replyId), updateReplyDTO, ReplyDTO.class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUpdated()).isNotNull();
        return response.getBody();
    }

    private List<ReplyDTO> getReplies(int postId) {
        ResponseEntity<ReplyDTO[]> response = getWithToken(String.format(GET_REPLIES_URL, postId), ReplyDTO[].class, userJwtToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private Optional<ReplyDTO> assertAndGetReply(int replyId, boolean shouldExist) {
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
        deleteReply(replyId, userJwtToken);
    }

    private void deleteReply(int replyId, JwtToken jwtToken) {
        ResponseEntity<MessageDTO> deleteResponse = deleteWithToken(String.format(REPLY_URL, replyId), MessageDTO.class, jwtToken);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}