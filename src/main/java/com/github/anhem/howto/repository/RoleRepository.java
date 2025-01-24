package com.github.anhem.howto.repository;

import com.github.anhem.howto.model.Role;
import com.github.anhem.howto.model.RoleName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static com.github.anhem.howto.repository.mapper.RoleMapper.mapToRole;

@Slf4j
@Repository
public class RoleRepository extends JdbcRepository {

    private static final String SELECT_ROLE_BY_NAME = "SELECT * FROM role WHERE role_name = :id";

    protected RoleRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public Role getRoleByName(RoleName roleName) {
        return findById(roleName, SELECT_ROLE_BY_NAME, (rs, i) -> mapToRole(rs));
    }

}
