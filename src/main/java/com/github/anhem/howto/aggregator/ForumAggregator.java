package com.github.anhem.howto.aggregator;

import com.github.anhem.howto.controller.mapper.CreatePostDTOMapper;
import com.github.anhem.howto.controller.mapper.CreateReplyDTOMapper;
import com.github.anhem.howto.controller.mapper.UpdatePostDTOMapper;
import com.github.anhem.howto.controller.mapper.UpdateReplyDTOMapper;
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

import static com.github.anhem.howto.controller.mapper.PostDTOMapper.mapToPostDTO;
import static com.github.anhem.howto.controller.mapper.PostDTOMapper.mapToPostDTOs;
import static com.github.anhem.howto.controller.mapper.ReplyDTOMapper.mapToReplyDTO;
import static com.github.anhem.howto.controller.mapper.ReplyDTOMapper.mapToReplyDTOs;
import static com.github.anhem.howto.controller.model.MessageDTO.fromId;
import static com.github.anhem.howto.model.RoleName.ADMINISTRATOR;
import static com.github.anhem.howto.model.RoleName.MODERATOR;

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
        List<Account> accounts = accountService.getAccounts(getAccountIdsFromPosts(posts));

        return mapToPostDTOs(posts, accounts);
    }

    public PostDTO getPost(PostId postId) {
        Post post = forumService.getPost(postId);
        Account account = accountService.getAccount(post.getAccountId());

        return mapToPostDTO(post, List.of(account));
    }

    public MessageDTO createPost(CreatePostDTO createPostDTO) {
        AccountId accountId = authService.getLoggedInAccountId();
        return fromId(forumService.createPost(CreatePostDTOMapper.mapToPost(createPostDTO, accountId)));
    }

    public PostDTO updatePost(PostId postId, UpdatePostDTO updatePostDTO) {
        Post post = forumService.getPost(postId);
        checkAuthorized(post.getAccountId());
        Account account = accountService.getAccount(post.getAccountId());
        return mapToPostDTO(forumService.updatePost(UpdatePostDTOMapper.mapToPost(updatePostDTO, post)), List.of(account));
    }

    public void removePost(PostId postId) {
        Post post = forumService.getPost(postId);
        checkAuthorized(post.getAccountId());
        forumService.removePost(postId);
    }

    public List<ReplyDTO> getReplies(PostId postId) {
        List<Reply> replies = forumService.getReplies(postId);
        List<Account> accounts = accountService.getAccounts(getAccountIdFromReplies(replies));

        return mapToReplyDTOs(replies, accounts);
    }

    public ReplyDTO getReply(ReplyId replyId) {
        Reply reply = forumService.getReply(replyId);
        List<Account> accounts = accountService.getAccounts(Set.of(reply.getAccountId()));
        return mapToReplyDTO(reply, accounts);
    }

    public MessageDTO createReply(CreateReplyDTO createReplyDTO) {
        AccountId accountId = authService.getLoggedInAccountId();
        return fromId(forumService.createReply(CreateReplyDTOMapper.mapToReply(createReplyDTO, accountId)));
    }

    public ReplyDTO updateReply(ReplyId replyId, UpdateReplyDTO updateReplyDTO) {
        Reply reply = forumService.getReply(replyId);
        checkAuthorized(reply.getAccountId());
        Account account = accountService.getAccount(reply.getAccountId());
        return mapToReplyDTO(forumService.updateReply(UpdateReplyDTOMapper.mapToReply(updateReplyDTO, reply)), List.of(account));
    }

    public void removeReply(ReplyId replyId) {
        Reply reply = forumService.getReply(replyId);
        checkAuthorized(reply.getAccountId());
        forumService.removeReply(replyId);
    }

    private void checkAuthorized(AccountId accountId) {
        if (!accountId.equals(authService.getLoggedInAccountId()) && !authService.loggedInAccountHasAnyOf(List.of(MODERATOR, ADMINISTRATOR))) {
            throw new ForbiddenException();
        }
    }

    private Set<AccountId> getAccountIdsFromPosts(List<Post> posts) {
        return posts.stream()
                .map(Post::getAccountId)
                .collect(Collectors.toSet());
    }

    private Set<AccountId> getAccountIdFromReplies(List<Reply> replies) {
        return replies.stream()
                .map(Reply::getAccountId)
                .collect(Collectors.toSet());
    }
}
