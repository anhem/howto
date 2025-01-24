package com.github.anhem.howto.repository;

import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Username;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
    private static final String SELECT_ACCOUNT_BY_ID = "SELECT * FROM account WHERE account_id = :id";
    private static final String SELECT_ACCOUNTS_BY_IDS = "SELECT * FROM account WHERE account_id IN(:accountIds)";
    private static final String SELECT_ACCOUNT_BY_USERNAME = "SELECT * FROM account WHERE username = :id";
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
        List<Integer> accountsIds = getAccountsIds(accountIds);
        MapSqlParameterSource parameters = createParameters("accountIds", accountsIds);

        return namedParameterJdbcTemplate.query(SELECT_ACCOUNTS_BY_IDS, parameters, (rs, i) -> mapToAccount(rs));
    }

    public Account getAccount(AccountId accountId) {
        return findById(accountId, SELECT_ACCOUNT_BY_ID, (rs, i) -> mapToAccount(rs));
    }

    public Account getAccount(Username username) {
        return findById(username, SELECT_ACCOUNT_BY_USERNAME, (rs, i) -> mapToAccount(rs));
    }

    public void removeAccount(AccountId accountId) {
        MapSqlParameterSource parameters = createParameters("accountId", accountId.value());
        namedParameterJdbcTemplate.update(DELETE_ACCOUNT, parameters);
        log.info("Removed account {}", accountId);
    }

    public AccountId createAccount(Account account) {
        MapSqlParameterSource parameters = createParameters("username", account.getUsername().value())
                .addValue("email", account.getEmail())
                .addValue("firstName", account.getFirstName())
                .addValue("lastName", account.getLastName())
                .addValue("created", Timestamp.from(account.getCreated()))
                .addValue("lastUpdated", Timestamp.from(account.getLastUpdated()));
        return insert(INSERT_ACCOUNT, parameters, AccountId.class);
    }


    public boolean accountExists(Username username, String email) {
        MapSqlParameterSource params = createParameters("username", username.value())
                .addValue("email", email);
        return namedParameterJdbcTemplate.queryForObject(ACCOUNT_EXISTS, params, Boolean.class);
    }

    private static List<Integer> getAccountsIds(Set<AccountId> accountIds) {
        return accountIds.stream()
                .map(AccountId::value)
                .collect(Collectors.toList());
    }
}
