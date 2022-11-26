package com.github.anhem.howto.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Optional;

public abstract class JdbcRepository {

    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    protected JdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    protected static KeyHolder createKeyHolder() {
        return new GeneratedKeyHolder();
    }

    protected MapSqlParameterSource createParameters() {
        return new MapSqlParameterSource();
    }

    protected MapSqlParameterSource createParameters(String name, Object value) {
        return createParameters().addValue(name, value);
    }

    protected Integer extractNumberId(KeyHolder keyHolder) {
        return Optional.ofNullable(keyHolder.getKey())
                .map(Number::intValue)
                .orElseThrow(() -> new RuntimeException("Failed to extract id"));
    }
}
