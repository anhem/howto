package com.github.anhem.howto.repository;

import com.github.anhem.howto.model.id.Id;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Optional;

@Slf4j
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

    protected <T extends Id<Integer>> T insert(String sql, MapSqlParameterSource parameters, Class<T> returnClass) {
        try {
            KeyHolder keyHolder = createKeyHolder();
            String primaryKeyName = getPrimaryKeyName(returnClass);
            namedParameterJdbcTemplate.update(sql, parameters, keyHolder, new String[]{primaryKeyName});
            T t = returnClass.getDeclaredConstructor(Integer.class).newInstance(extractNumberId(keyHolder));
            log.info("{} created", t);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    <T extends Id<Integer>> String getPrimaryKeyName(Class<T> returnClass) {
        return returnClass.getSimpleName()
                .replaceAll("(?!^)([A-Z])", "_$1")
                .toLowerCase();
    }
}
