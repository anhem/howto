package com.github.anhem.howto.repository;

import com.github.anhem.howto.exception.NotFoundException;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Id;
import com.github.anhem.howto.model.id.Username;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static com.github.anhem.howto.repository.mapper.AccountMapper.mapToAccount;

@Slf4j
@Repository
public class AccountRepository extends JdbcRepository {

    private static final String SELECT_ACCOUNTS = "SELECT * FROM account";
    private static final String SELECT_ACCOUNT = "SELECT * FROM account WHERE account_id = :accountId";
    private static final String INSERT_ACCOUNT = "INSERT INTO account(username, email, first_name, last_name, created, last_updated) values(:username, :email, :firstName, :lastName, :created, :lastUpdated)";
    private static final String DELETE_ACCOUNT = "DELETE FROM account WHERE account_id = :accountId";
    private static final String ACCOUNT_EXISTS = "SELECT EXISTS (SELECT 1 FROM account WHERE username = :username OR email =:email LIMIT 1)";

    protected AccountRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public List<Account> getUsers() {
        return namedParameterJdbcTemplate.query(SELECT_ACCOUNTS, (rs, i) -> mapToAccount(rs));
    }

    public Account getAccount(AccountId accountId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("accountId", accountId.getValue());
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_ACCOUNT, parameters, (rs, i) -> mapToAccount(rs));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(accountId);
        }
    }

    public void removeAccount(AccountId accountId) {
        MapSqlParameterSource parameters = createParameters()
                .addValue("accountId", accountId.getValue());

        namedParameterJdbcTemplate.update(DELETE_ACCOUNT, parameters);
        log.info("Removed user {}", accountId);
    }

    public AccountId createAccount(Account account) {
        MapSqlParameterSource parameters = createParameters()
                .addValue("username", account.getUsername().getValue())
                .addValue("email", account.getEmail())
                .addValue("firstName", account.getFirstName())
                .addValue("lastName", account.getLastName())
                .addValue("created", Timestamp.from(account.getCreated()))
                .addValue("lastUpdated", Timestamp.from(account.getLastUpdated()));
        KeyHolder keyHolder = createKeyHolder();

        namedParameterJdbcTemplate.update(INSERT_ACCOUNT, parameters, keyHolder, new String[]{"account_id"});

        return Id.of(AccountId.class, extractNumberId(keyHolder));
    }


    public boolean accountExists(Username username, String email) {
        MapSqlParameterSource params = createParameters()
                .addValue("username", username.getValue())
                .addValue("email", email);
        return namedParameterJdbcTemplate.queryForObject(ACCOUNT_EXISTS, params, Boolean.class);
    }


}
