package com.github.anhem.howto.exception;

import com.github.anhem.howto.model.id.AccountId;

public class NotFoundException extends RuntimeException {
    public NotFoundException(AccountId accountId) {
        super(String.format("Account with %s could not be found", accountId));
    }
}
