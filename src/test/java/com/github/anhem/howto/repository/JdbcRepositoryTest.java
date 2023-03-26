package com.github.anhem.howto.repository;

import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.AccountPasswordId;
import com.github.anhem.howto.model.id.PostId;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcRepositoryTest {

    private final ArbitraryRepository arbitraryRepository = new ArbitraryRepository(null);

    @Test
    public void getPrimaryKeyNameReturnsClassNameWithUnderscore() {
        assertThat(arbitraryRepository.getPrimaryKeyName(PostId.class)).isEqualTo("post_id");
        assertThat(arbitraryRepository.getPrimaryKeyName(AccountId.class)).isEqualTo("account_id");
        assertThat(arbitraryRepository.getPrimaryKeyName(AccountPasswordId.class)).isEqualTo("account_password_id");
    }

    private static class ArbitraryRepository extends JdbcRepository {
        protected ArbitraryRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
            super(namedParameterJdbcTemplate);
        }
    }

}