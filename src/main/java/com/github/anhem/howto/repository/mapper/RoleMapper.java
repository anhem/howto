package com.github.anhem.howto.repository.mapper;

import com.github.anhem.howto.model.Role;
import com.github.anhem.howto.model.id.RoleId;
import com.github.anhem.howto.model.id.RoleName;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleMapper {

    public static Role mapToRole(ResultSet rs) throws SQLException {
        return Role.builder()
                .roleId(new RoleId(rs.getInt("role_id")))
                .roleName(new RoleName(rs.getString("role_name")))
                .description(rs.getString("description"))
                .created(rs.getTimestamp("created").toInstant())
                .lastUpdated(rs.getTimestamp("last_updated").toInstant())
                .build();
    }
}
