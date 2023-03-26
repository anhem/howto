package com.github.anhem.howto.service;

import com.github.anhem.howto.model.Category;
import com.github.anhem.howto.model.Post;
import com.github.anhem.howto.model.Reply;
import com.github.anhem.howto.model.id.CategoryId;
import com.github.anhem.howto.model.id.PostId;
import com.github.anhem.howto.model.id.ReplyId;
import com.github.anhem.howto.repository.CategoryRepository;
import com.github.anhem.howto.repository.PostRepository;
import com.github.anhem.howto.repository.ReplyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ForumService {
    private final CategoryRepository categoryRepository;

    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    public ForumService(CategoryRepository categoryRepository, PostRepository postRepository, ReplyRepository replyRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.replyRepository = replyRepository;
    }

    public List<Category> getCategories() {
        return categoryRepository.getCategories();
    }

    public Category getCategory(CategoryId categoryId) {
        return categoryRepository.getCategory(categoryId);
    }

    @Transactional
    public void removeCategory(CategoryId categoryId) {
        Category category = categoryRepository.getCategory(categoryId);
        categoryRepository.removeCategory(categoryId);
        log.info("{} removed", category);
    }

    @Transactional
    public CategoryId createCategory(Category category) {
        return categoryRepository.createCategory(category);
    }

    public Post getPost(PostId postId) {
        return postRepository.getPost(postId);
    }

    public List<Post> getPosts(CategoryId categoryId) {
        return postRepository.getPosts(categoryId);
    }

    @Transactional
    public PostId createPost(Post post) {
        if (!post.getPostId().isNew()) {
            throw new IllegalArgumentException("Invalid postId");
        }
        return postRepository.createPost(post);
    }

    @Transactional
    public void removePost(PostId postId) {
        Post post = postRepository.getPost(postId);
        replyRepository.removeReplies(postId);
        postRepository.removePost(postId);
        log.info("{} and replies removed", post);
    }

    public List<Reply> getReplies(PostId postId) {
        return replyRepository.getReplies(postId);
    }

    @Transactional
    public ReplyId createReply(Reply reply) {
        return replyRepository.createReply(reply);
    }

    public Reply getReply(ReplyId replyId) {
        return replyRepository.getReply(replyId);
    }

    @Transactional
    public void removeReply(ReplyId replyId) {
        Reply reply = replyRepository.getReply(replyId);
        replyRepository.removeReply(replyId);
        log.info("{} removed", reply);
    }
}
