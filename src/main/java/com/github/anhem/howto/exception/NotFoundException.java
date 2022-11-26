package com.github.anhem.howto.exception;

import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.RoleName;

public class NotFoundException extends RuntimeException {
    public NotFoundException(AccountId accountId) {
        super(String.format("Account with %s could not be found", accountId));
    }

    public NotFoundException(RoleName roleName) {
        super(String.format("Role with %s could not be found", roleName));
    }
}
