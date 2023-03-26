package com.github.anhem.howto.controller;

import com.github.anhem.howto.aggregator.ForumAggregator;
import com.github.anhem.howto.controller.mapper.CategoryDTOMapper;
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
import static com.github.anhem.howto.controller.mapper.CategoryDTOMapper.mapToCategoryDTOs;
import static com.github.anhem.howto.controller.mapper.CreateCategoryDTOMapper.mapToCategory;
import static com.github.anhem.howto.model.RoleName.Constants.ADMINISTRATOR;

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
        return CategoryDTOMapper.mapToCategoryDTO(forumService.getCategory(new CategoryId(categoryId)));
    }

    @Secured(ADMINISTRATOR)
    @PostMapping(value = "categories")
    public MessageDTO createCategory(@Valid @RequestBody CreateCategoryDTO createCategoryDTO) {
        return MessageDTO.fromId(forumService.createCategory(mapToCategory(createCategoryDTO)));
    }

    @Secured(ADMINISTRATOR)
    @DeleteMapping(value = "categories/{categoryId}")
    public MessageDTO removeCategory(@PathVariable Integer categoryId) {
        forumService.removeCategory(new CategoryId(categoryId));
        return MessageDTO.OK;
    }

    @GetMapping("posts/category/{categoryId}")
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

    @DeleteMapping("posts/{postId}")
    public MessageDTO removePost(@PathVariable Integer postId) {
        forumAggregator.removePost(new PostId(postId));
        return MessageDTO.OK;
    }

    @GetMapping("posts/{postId}/replies")
    public List<ReplyDTO> getReplies(@PathVariable Integer postId) {
        return forumAggregator.getReplies(new PostId(postId));
    }

    @PostMapping("posts/{postId}/replies")
    public MessageDTO createReply(@Valid @RequestBody CreateReplyDTO createReplyDTO) {
        return forumAggregator.createReply(createReplyDTO);
    }

    @GetMapping("/replies/{replyId}")
    public ReplyDTO getReply(@PathVariable Integer replyId) {
        return forumAggregator.getReply(new ReplyId(replyId));
    }

    @DeleteMapping("/replies/{replyId}")
    public MessageDTO removeReply(@PathVariable Integer replyId) {
        forumAggregator.removeReply(new ReplyId(replyId));
        return MessageDTO.OK;
    }
}
