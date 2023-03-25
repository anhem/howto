package com.github.anhem.howto.repository.mapper;

import com.github.anhem.howto.model.Post;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.CategoryId;
import com.github.anhem.howto.model.id.PostId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper {

    public static Post mapToPost(ResultSet rs) throws SQLException {
        return Post.builder()
                .postId(new PostId(rs.getInt("post_id")))
                .categoryId(new CategoryId(rs.getInt("category_id")))
                .accountId(new AccountId(rs.getInt("account_id")))
                .title(rs.getString("name"))
                .body(rs.getString("description"))
                .created(rs.getTimestamp("created").toInstant())
                .lastUpdated(rs.getTimestamp("last_updated").toInstant())
                .build();
    }
}
