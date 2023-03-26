package com.github.anhem.howto.repository.mapper;

import com.github.anhem.howto.model.Reply;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.PostId;
import com.github.anhem.howto.model.id.ReplyId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReplyMapper {

    public static Reply mapToReply(ResultSet rs) throws SQLException {
        return Reply.builder()
                .replyId(new ReplyId(rs.getInt("reply_id")))
                .postId(new PostId(rs.getInt("post_id")))
                .accountId(new AccountId(rs.getInt("account_id")))
                .body(rs.getString("body"))
                .created(rs.getTimestamp("created").toInstant())
                .lastUpdated(rs.getTimestamp("last_updated").toInstant())
                .build();
    }
}
