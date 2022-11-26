package com.github.anhem.howto.repository;

import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.RoleId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Repository
public class AccountRoleRepository extends JdbcRepository {

    private static final String INSERT_ACCOUNT_ROLE = "INSERT INTO account_role(account_id, role_id, created) VALUES (:accountId, :roleId, :created)";
    private static final String DELETE_ACCOUNT_ROLE = "DELETE FROM account_role WHERE account_id = :accountId AND role_id = :roleId";
    private static final String DELETE_ACCOUNT_ROLES = "DELETE FROM account_role WHERE account_id = :accountId";

    protected AccountRoleRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public boolean addRoleToAccount(AccountId accountId, RoleId roleId, Instant created) {
        MapSqlParameterSource parameters = createParameters()
                .addValue("accountId", accountId.value())
                .addValue("roleId", roleId.value())
                .addValue("created", Timestamp.from(created));
        namedParameterJdbcTemplate.update(INSERT_ACCOUNT_ROLE, parameters);
        log.info("{} added to {}", roleId, accountId);
        return true;
    }

    public boolean removeRoleFromAccount(AccountId accountId, RoleId roleId) {
        MapSqlParameterSource parameters = createParameters()
                .addValue("accountId", accountId.value())
                .addValue("roleId", roleId.value());
        namedParameterJdbcTemplate.update(DELETE_ACCOUNT_ROLE, parameters);
        log.info("{} removed from {}", roleId, accountId);
        return true;
    }

    public boolean removeRolesFromAccount(AccountId accountId) {
        MapSqlParameterSource parameters = createParameters("accountId", accountId.value());
        namedParameterJdbcTemplate.update(DELETE_ACCOUNT_ROLES, parameters);
        log.info("All roles removed from {}", accountId);
        return true;
    }
}
