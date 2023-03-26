package com.github.anhem.howto.repository.mapper;

import com.github.anhem.howto.model.Category;
import com.github.anhem.howto.model.id.CategoryId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryMapper {

    public static Category mapToCategory(ResultSet rs) throws SQLException {
        return Category.builder()
                .categoryId(new CategoryId(rs.getInt("category_id")))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .created(rs.getTimestamp("created").toInstant())
                .lastUpdated(rs.getTimestamp("last_updated").toInstant())
                .build();
    }
}
