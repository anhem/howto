package com.github.anhem.howto.aggregator;

import com.github.anhem.howto.controller.model.*;
import com.github.anhem.howto.exception.ForbiddenException;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.Post;
import com.github.anhem.howto.model.Reply;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.CategoryId;
import com.github.anhem.howto.model.id.PostId;
import com.github.anhem.howto.model.id.ReplyId;
import com.github.anhem.howto.service.AccountService;
import com.github.anhem.howto.service.AuthService;
import com.github.anhem.howto.service.ForumService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.anhem.howto.controller.mapper.CreatePostDTOMapper.mapToPost;
import static com.github.anhem.howto.controller.mapper.CreateReplyDTOMapper.mapToReply;
import static com.github.anhem.howto.controller.mapper.PostDTOMapper.mapToPostDTO;
import static com.github.anhem.howto.controller.mapper.PostDTOMapper.mapToPostDTOs;
import static com.github.anhem.howto.controller.mapper.ReplyDTOMapper.mapToReplyDTOs;

@Component
public class ForumAggregator {

    private final ForumService forumService;
    private final AccountService accountService;
    private final AuthService authService;

    public ForumAggregator(ForumService forumService, AccountService accountService, AuthService authService) {
        this.forumService = forumService;
        this.accountService = accountService;
        this.authService = authService;
    }

    public List<PostDTO> getPosts(CategoryId categoryId) {
        List<Post> posts = forumService.getPosts(categoryId);
        List<Account> accounts = accountService.getAccounts(getAccountIds(posts));

        return mapToPostDTOs(posts, accounts);
    }

    public PostDTO getPost(PostId postId) {
        Post post = forumService.getPost(postId);
        Account account = accountService.getAccount(post.getAccountId());

        return mapToPostDTO(post, List.of(account));
    }

    public MessageDTO createPost(CreatePostDTO createPostDTO) {
        AccountId accountId = authService.getAccountId();
        return MessageDTO.fromId(forumService.createPost(mapToPost(createPostDTO, accountId)));
    }

    public void removePost(PostId postId) {
        AccountId accountId = authService.getAccountId();
        Post post = forumService.getPost(postId);
        if (post.getAccountId().equals(accountId)) {
            forumService.removePost(postId);
        } else {
            throw new ForbiddenException();
        }
    }

    public List<ReplyDTO> getReplies(PostId postId) {
        List<Reply> replies = forumService.getReplies(postId);
        List<Account> accounts = accountService.getAccounts(getAccountIdSet(replies));

        return mapToReplyDTOs(replies, accounts);
    }

    public MessageDTO createReply(CreateReplyDTO createReplyDTO) {
        AccountId accountId = authService.getAccountId();
        return MessageDTO.fromId(forumService.createReply(mapToReply(createReplyDTO, accountId)));
    }

    public void removeReply(PostId postId, ReplyId replyId) {
        AccountId accountId = authService.getAccountId();
        Reply reply = forumService.getReply(replyId);
        if (reply.getAccountId().equals(accountId)) {
            forumService.removeReply(postId, replyId);
        } else {
            throw new ForbiddenException();
        }
    }

    private static Set<AccountId> getAccountIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getAccountId)
                .collect(Collectors.toSet());
    }

    private static Set<AccountId> getAccountIdSet(List<Reply> replies) {
        return replies.stream()
                .map(Reply::getAccountId)
                .collect(Collectors.toSet());
    }
}
