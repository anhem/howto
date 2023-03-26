package com.github.anhem.howto.repository;

import com.github.anhem.howto.exception.NotFoundException;
import com.github.anhem.howto.model.Post;
import com.github.anhem.howto.model.id.CategoryId;
import com.github.anhem.howto.model.id.PostId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static com.github.anhem.howto.repository.mapper.PostMapper.mapToPost;

@Slf4j
@Repository
public class PostRepository extends JdbcRepository {

    private static final String SELECT_POSTS_BY_CATEGORY_ID = "SELECT * FROM post WHERE category_id = :categoryId";
    private static final String SELECT_POST_BY_ID = "SELECT * FROM post WHERE post_id = :postId";
    private static final String DELETE_POST = "DELETE FROM post WHERE post_id = :postId";
    private static final String INSERT_POST = "INSERT INTO post(category_id, account_id, title, body, created, last_updated) VALUES (:categoryId, :accountId, :title, :body, :created, :lastUpdated)";

    protected PostRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public List<Post> getPosts(CategoryId categoryId) {
        MapSqlParameterSource parameters = createParameters("categoryId", categoryId.value());
        return namedParameterJdbcTemplate.query(SELECT_POSTS_BY_CATEGORY_ID, parameters, (rs, i) -> mapToPost(rs));
    }

    public Post getPost(PostId postId) {
        try {
            MapSqlParameterSource parameters = createParameters("postId", postId.value());
            return namedParameterJdbcTemplate.queryForObject(SELECT_POST_BY_ID, parameters, (rs, i) -> mapToPost(rs));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(postId);
        }
    }

    public PostId createPost(Post post) {
        MapSqlParameterSource parameters = createParameters("postId", post.getPostId().value())
                .addValue("categoryId", post.getCategoryId().value())
                .addValue("accountId", post.getAccountId().value())
                .addValue("title", post.getTitle())
                .addValue("body", post.getBody())
                .addValue("created", Timestamp.from(post.getCreated()))
                .addValue("lastUpdated", Timestamp.from(post.getLastUpdated()));
        return insert(INSERT_POST, parameters, PostId.class);
    }

    public void removePost(PostId postId) {
        MapSqlParameterSource parameters = createParameters("postId", postId.value());
        namedParameterJdbcTemplate.update(DELETE_POST, parameters);
        log.info("Post {} removed", postId);
    }

}
