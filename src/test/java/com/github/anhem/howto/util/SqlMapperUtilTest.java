package com.github.anhem.howto.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static com.github.anhem.howto.util.SqlMapperUtil.getNullableInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqlMapperUtilTest {

    private static final String COLUMN_LABEL = "my_instant";
    @Mock
    private ResultSet resultSet;

    @Test
    void getNullableInstantReturnsNull() throws SQLException {
        assertThat(getNullableInstant(resultSet, COLUMN_LABEL)).isNull();
    }

    @Test
    void getNullableInstantReturnsInstant() throws SQLException {
        when(resultSet.getTimestamp(COLUMN_LABEL)).thenReturn(Timestamp.from(Instant.now()));

        assertThat(getNullableInstant(resultSet, COLUMN_LABEL)).isNotNull();

    }

}