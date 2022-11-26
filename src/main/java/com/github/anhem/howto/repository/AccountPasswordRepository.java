package com.github.anhem.howto.repository;

import com.github.anhem.howto.model.AccountPassword;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.AccountPasswordId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Slf4j
@Repository
public class AccountPasswordRepository extends JdbcRepository {

    private static final String INSERT_PASSWORD = "INSERT INTO account_password(account_id, password, created) VALUES(:accountId, :password, :created)";
    private static final String DELETE_PASSWORD = "DELETE FROM account_password where account_id = :accountId";

    protected AccountPasswordRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public AccountPasswordId createPassword(AccountPassword accountPassword) {
        MapSqlParameterSource parameters = createParameters()
                .addValue("accountId", accountPassword.getAccountId().value())
                .addValue("password", accountPassword.getPassword().value())
                .addValue("created", Timestamp.from(accountPassword.getCreated()));
        KeyHolder keyHolder = createKeyHolder();

        namedParameterJdbcTemplate.update(INSERT_PASSWORD, parameters, keyHolder, new String[]{"account_password_id"});

        return new AccountPasswordId(extractNumberId(keyHolder));
    }

    public void removePassword(AccountId accountId) {
        MapSqlParameterSource parameters = createParameters("accountId", accountId.value());
        namedParameterJdbcTemplate.update(DELETE_PASSWORD, parameters);
    }
}
