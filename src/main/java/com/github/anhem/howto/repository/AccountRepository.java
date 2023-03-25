package com.github.anhem.howto.repository;

import com.github.anhem.howto.exception.NotFoundException;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Username;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.anhem.howto.repository.mapper.AccountMapper.mapToAccount;

@Slf4j
@Repository
public class AccountRepository extends JdbcRepository {

    private static final String SELECT_ACCOUNTS = "SELECT * FROM account";
    private static final String SELECT_ACCOUNT_BY_ID = "SELECT * FROM account WHERE account_id = :accountId";
    private static final String SELECT_ACCOUNTS_BY_IDS = "SELECT * FROM account WHERE account_id IN(:accountIds)";
    private static final String SELECT_ACCOUNT_BY_USERNAME = "SELECT * FROM account WHERE username = :username";
    private static final String INSERT_ACCOUNT = "INSERT INTO account(username, email, first_name, last_name, created, last_updated) values(:username, :email, :firstName, :lastName, :created, :lastUpdated)";
    private static final String DELETE_ACCOUNT = "DELETE FROM account WHERE account_id = :accountId";
    private static final String ACCOUNT_EXISTS = "SELECT EXISTS (SELECT 1 FROM account WHERE username = :username OR email =:email LIMIT 1)";

    protected AccountRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public List<Account> getAccounts() {
        return namedParameterJdbcTemplate.query(SELECT_ACCOUNTS, (rs, i) -> mapToAccount(rs));
    }

    public List<Account> getAccounts(Set<AccountId> accountIds) {
        List<Integer> accountsIds = accountIds.stream().map(AccountId::value).collect(Collectors.toList());
        MapSqlParameterSource parameters = createParameters("accountIds", accountsIds);

        return namedParameterJdbcTemplate.query(SELECT_ACCOUNTS_BY_IDS, parameters, (rs, i) -> mapToAccount(rs));
    }

    public Account getAccount(AccountId accountId) {
        try {
            MapSqlParameterSource parameters = createParameters("accountId", accountId.value());
            return namedParameterJdbcTemplate.queryForObject(SELECT_ACCOUNT_BY_ID, parameters, (rs, i) -> mapToAccount(rs));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(accountId);
        }
    }

    public Account getAccount(Username username) {
        try {
            MapSqlParameterSource parameters = createParameters("username", username.value());
            return namedParameterJdbcTemplate.queryForObject(SELECT_ACCOUNT_BY_USERNAME, parameters, (rs, i) -> mapToAccount(rs));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(username);
        }
    }

    public void removeAccount(AccountId accountId) {
        MapSqlParameterSource parameters = createParameters("accountId", accountId.value());
        namedParameterJdbcTemplate.update(DELETE_ACCOUNT, parameters);
        log.info("Removed account {}", accountId);
    }

    public AccountId createAccount(Account account) {
        MapSqlParameterSource parameters = createParameters()
                .addValue("username", account.getUsername().value())
                .addValue("email", account.getEmail())
                .addValue("firstName", account.getFirstName())
                .addValue("lastName", account.getLastName())
                .addValue("created", Timestamp.from(account.getCreated()))
                .addValue("lastUpdated", Timestamp.from(account.getLastUpdated()));
        KeyHolder keyHolder = createKeyHolder();

        namedParameterJdbcTemplate.update(INSERT_ACCOUNT, parameters, keyHolder, new String[]{"account_id"});

        AccountId accountId = new AccountId(extractNumberId(keyHolder));
        log.info("Account {} created", account);
        return accountId;
    }


    public boolean accountExists(Username username, String email) {
        MapSqlParameterSource params = createParameters()
                .addValue("username", username.value())
                .addValue("email", email);
        return namedParameterJdbcTemplate.queryForObject(ACCOUNT_EXISTS, params, Boolean.class);
    }
}
