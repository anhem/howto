package com.github.anhem.howto.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public class SqlMapperUtil {

    public static Instant getNullableInstant(ResultSet rs, String columnLabel) throws SQLException {
        return Optional.ofNullable(rs.getTimestamp(columnLabel))
                .map(Timestamp::toInstant)
                .orElse(null);
    }

}
