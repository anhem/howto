package com.github.anhem.howto.repository;

import com.github.anhem.howto.exception.NotFoundException;
import com.github.anhem.howto.model.AccountPassword;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.AccountPasswordId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

import static com.github.anhem.howto.repository.mapper.AccountPasswordMapper.mapToAccountPassword;

@Slf4j
@Repository
public class AccountPasswordRepository extends JdbcRepository {

    private static final String SELECT_PASSWORD = "SELECT * FROM account_password WHERE account_id = :accountId";
    private static final String INSERT_PASSWORD = "INSERT INTO account_password(account_id, password, created) VALUES(:accountId, :password, :created)";
    private static final String DELETE_PASSWORD = "DELETE FROM account_password where account_id = :accountId";

    protected AccountPasswordRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public AccountPassword getPassword(AccountId accountId) {
        try {
            MapSqlParameterSource parameters = createParameters("accountId", accountId.value());
            return namedParameterJdbcTemplate.queryForObject(SELECT_PASSWORD, parameters, (rs, i) -> mapToAccountPassword(rs));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(accountId);
        }
    }

    public AccountPasswordId createPassword(AccountPassword accountPassword) {
        MapSqlParameterSource parameters = createParameters("accountId", accountPassword.getAccountId().value())
                .addValue("password", accountPassword.getPassword().value())
                .addValue("created", Timestamp.from(accountPassword.getCreated()));
        return insert(INSERT_PASSWORD, parameters, AccountPasswordId.class);
    }

    public void removePassword(AccountId accountId) {
        MapSqlParameterSource parameters = createParameters("accountId", accountId.value());
        namedParameterJdbcTemplate.update(DELETE_PASSWORD, parameters);
    }
}
