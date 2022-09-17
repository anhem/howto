package com.github.anhem.howto.service;

import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.AccountPassword;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Password;
import com.github.anhem.howto.repository.AccountPasswordRepository;
import com.github.anhem.howto.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.github.anhem.howto.model.id.AccountPasswordId.NEW_ACCOUNT_PASSWORD_ID;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountPasswordRepository accountPasswordRepository;

    public AccountService(AccountRepository accountRepository, AccountPasswordRepository accountPasswordRepository) {
        this.accountRepository = accountRepository;
        this.accountPasswordRepository = accountPasswordRepository;
    }

    public List<Account> getUsers() {
        return accountRepository.getUsers();
    }

    public Account getAccount(AccountId accountId) {
        if (accountId.isNew()) {
            throw new IllegalArgumentException("invalid accountId");
        }
        return accountRepository.getAccount(accountId);
    }

    @Transactional
    public void removeAccount(AccountId accountId) {
        if (accountId.isNew()) {
            throw new IllegalArgumentException("invalid accountId");
        }
        accountRepository.getAccount(accountId);
        accountPasswordRepository.removePassword(accountId);
        accountRepository.removeAccount(accountId);
        log.info("Account {} removed", accountId);
    }

    @Transactional
    public AccountId createAccount(Account account, Password password) {
        if (!account.getAccountId().isNew()) {
            throw new IllegalArgumentException("invalid accountId");
        }
        if (accountRepository.accountExists(account.getUsername(), account.getEmail())) {
            throw new IllegalArgumentException("username or email already exists");
        }

        AccountId accountId = accountRepository.createAccount(account);

        AccountPassword accountPassword = AccountPassword.builder()
                .accountPasswordId(NEW_ACCOUNT_PASSWORD_ID)
                .accountId(accountId)
                .password(password)
                .created(Instant.now())
                .build();

        accountPasswordRepository.createPassword(accountPassword);

        log.info("Account {} {} created", accountId, account.getUsername());
        return accountId;
    }
}
