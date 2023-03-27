package com.github.anhem.howto.repository;

import com.github.anhem.howto.exception.NotFoundException;
import com.github.anhem.howto.model.Reply;
import com.github.anhem.howto.model.id.PostId;
import com.github.anhem.howto.model.id.ReplyId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static com.github.anhem.howto.repository.mapper.ReplyMapper.mapToReply;

@Slf4j
@Repository
public class ReplyRepository extends JdbcRepository {

    private static final String SELECT_REPLIES_BY_POST_ID = "SELECT * FROM reply where post_id = :postId";
    private static final String SELECT_POST_BY_ID = "SELECT * FROM reply WHERE reply_id = :replyId";
    private static final String DELETE_REPLY = "DELETE FROM reply WHERE reply_id = :replyId";
    private static final String DELETE_REPLIES = "DELETE FROM reply WHERE post_id = :postId";
    private static final String INSERT_REPLY = "INSERT INTO reply(post_id, account_id, body, created, last_updated) VALUES (:postId, :accountId, :body, :created, :lastUpdated)";
    private static final String UPDATE_REPLY = "UPDATE REPLY SET body = :body, last_updated = :lastUpdated WHERE reply_id = :replyId";

    protected ReplyRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public List<Reply> getReplies(PostId postId) {
        MapSqlParameterSource parameters = createParameters("postId", postId.value());
        return namedParameterJdbcTemplate.query(SELECT_REPLIES_BY_POST_ID, parameters, (rs, i) -> mapToReply(rs));
    }

    public Reply getReply(ReplyId replyId) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_POST_BY_ID, createParameters("replyId", replyId.value()), (rs, i) -> mapToReply(rs));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(replyId);
        }
    }

    public ReplyId createReply(Reply reply) {
        MapSqlParameterSource parameters = createParameters()
                .addValue("postId", reply.getPostId().value())
                .addValue("accountId", reply.getAccountId().value())
                .addValue("body", reply.getBody())
                .addValue("created", Timestamp.from(reply.getCreated()))
                .addValue("lastUpdated", Timestamp.from(reply.getLastUpdated()));
        return insert(INSERT_REPLY, parameters, ReplyId.class);
    }

    public void updateReply(Reply reply) {
        MapSqlParameterSource parameters = createParameters("replyId", reply.getReplyId().value())
                .addValue("body", reply.getBody())
                .addValue("lastUpdated", Timestamp.from(reply.getLastUpdated()));
        namedParameterJdbcTemplate.update(UPDATE_REPLY, parameters);
    }

    public void removeReply(ReplyId ReplyId) {
        MapSqlParameterSource parameters = createParameters("replyId", ReplyId.value());
        namedParameterJdbcTemplate.update(DELETE_REPLY, parameters);
        log.info("Reply {} removed", ReplyId);
    }

    public void removeReplies(PostId postId) {
        MapSqlParameterSource parameters = createParameters("postId", postId.value());
        namedParameterJdbcTemplate.update(DELETE_REPLIES, parameters);
        log.info("Replies for {} removed", postId);
    }
}
