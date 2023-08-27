package com.github.anhem.howto.repository;

import com.github.anhem.howto.exception.NotFoundException;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Username;
import com.github.anhem.howto.testutil.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountRepositoryIT extends TestApplication {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void crd() {
        Account account = populate(Account.class).toBuilder()
                .accountId(AccountId.NEW_ACCOUNT_ID)
                .email("test@example.com")
                .lastLogin(null)
                .build();

        AccountId accountId = accountRepository.createAccount(account);

        Optional<Account> foundAccount = findAccount(accountId);
        assertThat(foundAccount).isPresent();
        assertAccount(foundAccount.get(), account, accountId);
        assertAccount(accountRepository.getAccount(account.getUsername()), account, accountId);
        assertAccount(accountRepository.getAccount(accountId), account, accountId);
        assertThat(accountRepository.accountExists(account.getUsername(), "")).isTrue();
        assertThat(accountRepository.accountExists(new Username(""), account.getEmail())).isTrue();

        accountRepository.removeAccount(accountId);

        assertThat(findAccount(accountId)).isNotPresent();
        assertThatThrownBy(() -> accountRepository.getAccount(account.getUsername()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(account.getUsername().toString());
        assertThatThrownBy(() -> accountRepository.getAccount(accountId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(accountId.toString());
        assertThat(accountRepository.accountExists(account.getUsername(), "")).isFalse();
        assertThat(accountRepository.accountExists(new Username(""), account.getEmail())).isFalse();
    }

    private Optional<Account> findAccount(AccountId accountId) {
        Optional<Account> accountFromAllAccounts = accountRepository.getAccounts().stream()
                .filter(a -> a.getAccountId().equals(accountId))
                .findFirst();
        Optional<Account> accountFromSetOfAccountIds = accountRepository.getAccounts(Set.of(accountId)).stream()
                .filter(a -> a.getAccountId().equals(accountId))
                .findFirst();


        assertThat(accountFromAllAccounts).isEqualTo(accountFromSetOfAccountIds);

        return accountFromAllAccounts;
    }

    private static void assertAccount(Account readAccount, Account account, AccountId accountId) {
        assertThat(readAccount).usingRecursiveComparison()
                .ignoringFields("accountId", "created", "lastUpdated")
                .isEqualTo(account);
        assertThat(readAccount.getAccountId()).isEqualTo(accountId);
        assertThat(readAccount.getCreated()).isBetween(account.getCreated().minusSeconds(1), account.getCreated().plusSeconds(1));
        assertThat(readAccount.getLastUpdated()).isBetween(account.getLastUpdated().minusSeconds(1), account.getLastUpdated().plusSeconds(1));
    }

}