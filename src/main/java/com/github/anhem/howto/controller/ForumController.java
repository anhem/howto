package com.github.anhem.howto.controller;

import com.github.anhem.howto.aggregator.ForumAggregator;
import com.github.anhem.howto.controller.model.*;
import com.github.anhem.howto.model.id.CategoryId;
import com.github.anhem.howto.model.id.PostId;
import com.github.anhem.howto.model.id.ReplyId;
import com.github.anhem.howto.service.ForumService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.github.anhem.howto.configuration.JwtTokenFilter.BEARER_AUTHENTICATION;
import static com.github.anhem.howto.controller.mapper.CategoryDTOMapper.mapToCategoryDTO;
import static com.github.anhem.howto.controller.mapper.CategoryDTOMapper.mapToCategoryDTOs;
import static com.github.anhem.howto.controller.mapper.UpsertCategoryDTOMapper.mapToCategory;
import static com.github.anhem.howto.controller.model.MessageDTO.fromId;
import static com.github.anhem.howto.model.RoleName.Constants.ADMINISTRATOR;
import static com.github.anhem.howto.model.RoleName.Constants.MODERATOR;

@RestController
@RequestMapping(value = "api/forum")
@SecurityRequirement(name = BEARER_AUTHENTICATION)
public class ForumController {

    private final ForumAggregator forumAggregator;
    private final ForumService forumService;

    public ForumController(ForumAggregator forumAggregator, ForumService forumService) {
        this.forumAggregator = forumAggregator;
        this.forumService = forumService;
    }

    @GetMapping("categories")
    public List<CategoryDTO> getCategories() {
        return mapToCategoryDTOs(forumService.getCategories());
    }

    @GetMapping("categories/{categoryId}")
    public CategoryDTO getCategory(@PathVariable Integer categoryId) {
        return mapToCategoryDTO(forumService.getCategory(new CategoryId(categoryId)));
    }

    @Secured({MODERATOR, ADMINISTRATOR})
    @PostMapping(value = "categories")
    public MessageDTO createCategory(@Valid @RequestBody UpsertCategoryDTO upsertCategoryDTO) {
        return fromId(forumService.createCategory(mapToCategory(upsertCategoryDTO)));
    }

    @Secured({MODERATOR, ADMINISTRATOR})
    @PutMapping("categories/{categoryId}")
    public CategoryDTO updateCategory(@PathVariable Integer categoryId, @Valid @RequestBody UpsertCategoryDTO upsertCategoryDTO) {
        return forumAggregator.updateCategory(new CategoryId(categoryId), upsertCategoryDTO);
    }

    @Secured(ADMINISTRATOR)
    @DeleteMapping(value = "categories/{categoryId}")
    public MessageDTO removeCategory(@PathVariable Integer categoryId) {
        forumService.removeCategory(new CategoryId(categoryId));
        return MessageDTO.OK;
    }

    @GetMapping("categories/{categoryId}/posts")
    public List<PostDTO> getPosts(@PathVariable Integer categoryId) {
        return forumAggregator.getPosts(new CategoryId(categoryId));
    }

    @GetMapping("posts/{postId}")
    public PostDTO getPost(@PathVariable Integer postId) {
        return forumAggregator.getPost(new PostId(postId));
    }

    @PostMapping("posts")
    public MessageDTO createPost(@Valid @RequestBody CreatePostDTO createPostDTO) {
        return forumAggregator.createPost(createPostDTO);
    }

    @PutMapping("posts/{postId}")
    public PostDTO updatePost(@PathVariable Integer postId, @Valid @RequestBody UpdatePostDTO updatePostDTO) {
        return forumAggregator.updatePost(new PostId(postId), updatePostDTO);
    }

    @DeleteMapping("posts/{postId}")
    public MessageDTO removePost(@PathVariable Integer postId) {
        forumAggregator.removePost(new PostId(postId));
        return MessageDTO.OK;
    }

    @GetMapping("posts/{postId}/replies")
    public List<ReplyDTO> getReplies(@PathVariable Integer postId) {
        return forumAggregator.getReplies(new PostId(postId));
    }

    @GetMapping("replies/{replyId}")
    public ReplyDTO getReply(@PathVariable Integer replyId) {
        return forumAggregator.getReply(new ReplyId(replyId));
    }

    @PostMapping("replies")
    public MessageDTO createReply(@Valid @RequestBody CreateReplyDTO createReplyDTO) {
        return forumAggregator.createReply(createReplyDTO);
    }

    @PutMapping("replies/{replyId}")
    public ReplyDTO updateReply(@PathVariable Integer replyId, @Valid @RequestBody UpdateReplyDTO updateReplyDTO) {
        return forumAggregator.updateReply(new ReplyId(replyId), updateReplyDTO);
    }

    @DeleteMapping("replies/{replyId}")
    public MessageDTO removeReply(@PathVariable Integer replyId) {
        forumAggregator.removeReply(new ReplyId(replyId));
        return MessageDTO.OK;
    }
}
