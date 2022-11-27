package com.github.anhem.howto.repository.mapper;

import com.github.anhem.howto.model.AccountPassword;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.AccountPasswordId;
import com.github.anhem.howto.model.id.Password;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountPasswordMapper {

    public static AccountPassword mapToAccountPassword(ResultSet rs) throws SQLException {
        return AccountPassword.builder()
                .accountPasswordId(new AccountPasswordId(rs.getInt("account_password_id")))
                .accountId(new AccountId(rs.getInt("account_id")))
                .password(new Password(rs.getString("password")))
                .created(rs.getTimestamp("created").toInstant())
                .build();
    }
}
