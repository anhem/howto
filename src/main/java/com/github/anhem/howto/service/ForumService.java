package com.github.anhem.howto.service;

import com.github.anhem.howto.client.urlhaus.UrlHausClient;
import com.github.anhem.howto.exception.ValidationException;
import com.github.anhem.howto.model.Category;
import com.github.anhem.howto.model.Post;
import com.github.anhem.howto.model.Reply;
import com.github.anhem.howto.model.id.CategoryId;
import com.github.anhem.howto.model.id.PostId;
import com.github.anhem.howto.model.id.ReplyId;
import com.github.anhem.howto.repository.CategoryRepository;
import com.github.anhem.howto.repository.PostRepository;
import com.github.anhem.howto.repository.ReplyRepository;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ForumService {
    public static final String MALICIOUS_URL_DETECTED = "Malicious url detected!";
    private final CategoryRepository categoryRepository;

    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final UrlHausClient urlHausClient;

    public ForumService(CategoryRepository categoryRepository, PostRepository postRepository, ReplyRepository replyRepository, UrlHausClient urlHausClient) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.replyRepository = replyRepository;
        this.urlHausClient = urlHausClient;
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

    @Transactional
    public Category updateCategory(Category category) {
        categoryRepository.updateCategory(category);
        return categoryRepository.getCategory(category.getCategoryId());
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
            throw new ValidationException("Invalid postId");
        }
        checkForMaliciousUrls(post);
        return postRepository.createPost(post);
    }

    @Transactional
    public Post updatePost(Post post) {
        checkForMaliciousUrls(post);
        postRepository.updatePost(post);
        return postRepository.getPost(post.getPostId());
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

    public Reply getReply(ReplyId replyId) {
        return replyRepository.getReply(replyId);
    }

    @Transactional
    public ReplyId createReply(Reply reply) {
        checkForMaliciousUrls(reply);
        return replyRepository.createReply(reply);
    }

    @Transactional
    public Reply updateReply(Reply reply) {
        checkForMaliciousUrls(reply);
        replyRepository.updateReply(reply);
        return replyRepository.getReply(reply.getReplyId());
    }

    @Transactional
    public void removeReply(ReplyId replyId) {
        Reply reply = replyRepository.getReply(replyId);
        replyRepository.removeReply(replyId);
        log.info("{} removed", reply);
    }

    private void checkForMaliciousUrls(Post post) {
        checkForMaliciousUrls(detectUrls(post.getTitle(), post.getBody()));
    }

    private void checkForMaliciousUrls(Reply reply) {
        checkForMaliciousUrls(detectUrls(reply.getBody()));
    }

    private static Set<String> detectUrls(String... string) {
        return Arrays.stream(string)
                .map(s -> new UrlDetector(s, UrlDetectorOptions.Default).detect())
                .flatMap(Collection::stream)
                .map(Url::getFullUrl)
                .collect(Collectors.toSet());
    }

    private void checkForMaliciousUrls(Set<String> urls) {
        if (urlHausClient.checkForMaliciousUrls(urls)) {
            throw new ValidationException(MALICIOUS_URL_DETECTED);
        }
    }
}
