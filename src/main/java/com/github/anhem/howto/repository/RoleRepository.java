package com.github.anhem.howto.repository;

import com.github.anhem.howto.exception.NotFoundException;
import com.github.anhem.howto.model.Role;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.RoleName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.github.anhem.howto.repository.mapper.RoleMapper.mapToRole;

@Slf4j
@Repository
public class RoleRepository extends JdbcRepository {

    private static final String SELECT_AVAILABLE_ROLES = "SELECT r.* FROM role r WHERE r.id NOT IN (SELECT ar.role_id FROM account_role ar WHERE ar.account_id = :accountId)";
    private static final String SELECT_ROLE_BY_NAME = "SELECT * FROM role WHERE role_name = :roleName";

    protected RoleRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public List<Role> getAvailableRoles(AccountId accountId) {
        SqlParameterSource parameters = createParameters("accountId", accountId.value());
        return namedParameterJdbcTemplate.query(SELECT_AVAILABLE_ROLES, parameters, (rs, i) -> mapToRole(rs));
    }

    public Role getRoleByName(RoleName roleName) {
        try {
            MapSqlParameterSource parameters = createParameters("roleName", roleName.value());
            return namedParameterJdbcTemplate.queryForObject(SELECT_ROLE_BY_NAME, parameters, (rs, i) -> mapToRole(rs));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(roleName);
        }
    }

}
