package com.github.anhem.howto.repository.mapper;

import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Username;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.anhem.howto.util.SqlMapperUtil.getNullableInstant;

public class AccountMapper {

    public static Account mapToAccount(ResultSet rs) throws SQLException {
        return Account.builder()
                .accountId(AccountId.of(rs.getInt("account_id")))
                .username(Username.of(rs.getString("username")))
                .email(rs.getString("email"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .created(rs.getTimestamp("created").toInstant())
                .lastUpdated(rs.getTimestamp("last_updated").toInstant())
                .lastLogin(getNullableInstant(rs, "last_login"))
                .build();
    }
}
