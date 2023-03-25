package com.github.anhem.howto.service;

import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.AccountPassword;
import com.github.anhem.howto.model.Role;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Password;
import com.github.anhem.howto.model.id.Username;
import com.github.anhem.howto.repository.AccountPasswordRepository;
import com.github.anhem.howto.repository.AccountRepository;
import com.github.anhem.howto.repository.AccountRoleRepository;
import com.github.anhem.howto.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.github.anhem.howto.model.RoleName.ADMINISTRATOR;
import static com.github.anhem.howto.model.RoleName.USER;
import static com.github.anhem.howto.model.id.AccountPasswordId.NEW_ACCOUNT_PASSWORD_ID;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountPasswordRepository accountPasswordRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;

    public AccountService(AccountRepository accountRepository, AccountPasswordRepository accountPasswordRepository, AccountRoleRepository accountRoleRepository, RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.accountPasswordRepository = accountPasswordRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.roleRepository = roleRepository;
    }

    public List<Account> getUsers() {
        return accountRepository.getAccounts();
    }

    public Account getAccount(AccountId accountId) {
        if (accountId.isNew()) {
            throw new IllegalArgumentException("invalid accountId");
        }
        return accountRepository.getAccount(accountId);
    }

    public List<Account> getAccounts(Set<AccountId> accountIds) {
        return accountRepository.getAccounts(accountIds);
    }

    @Transactional
    public AccountId createUserAccount(Account account, Password password) {
        Role userRole = roleRepository.getRoleByName(USER);
        return createAccount(account, password, userRole);
    }

    @Transactional
    public AccountId createAdministratorAccount(Account account, Password password) {
        Role administratorRole = roleRepository.getRoleByName(ADMINISTRATOR);
        return createAccount(account, password, administratorRole);
    }

    @Transactional
    public void removeAccount(AccountId accountId) {
        if (accountId.isNew()) {
            throw new IllegalArgumentException("invalid accountId");
        }
        Account account = accountRepository.getAccount(accountId);
        accountRoleRepository.removeRolesFromAccount(accountId);
        accountPasswordRepository.removePassword(accountId);
        accountRepository.removeAccount(accountId);
        log.info("Account {} removed", account);
    }

    private AccountId createAccount(Account account, Password password, Role userRole) {
        if (!account.getAccountId().isNew()) {
            throw new IllegalArgumentException("invalid accountId");
        }
        if (account.getUsername().equals(Username.UNKNOWN)) {
            throw new IllegalArgumentException("reserved username");
        }
        if (accountRepository.accountExists(account.getUsername(), account.getEmail())) {
            throw new IllegalArgumentException("username or email already exists");
        }
        AccountId accountId = accountRepository.createAccount(account);
        accountRoleRepository.addRoleToAccount(accountId, userRole.getRoleId(), Instant.now());
        AccountPassword accountPassword = AccountPassword.builder()
                .accountPasswordId(NEW_ACCOUNT_PASSWORD_ID)
                .accountId(accountId)
                .password(password)
                .created(Instant.now())
                .build();
        accountPasswordRepository.createPassword(accountPassword);
        log.info("Account {} ({}) created", account.getUsername(), accountId);
        return accountId;
    }
}
