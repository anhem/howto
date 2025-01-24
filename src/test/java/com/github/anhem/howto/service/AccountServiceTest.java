package com.github.anhem.howto.service;

import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.AccountPassword;
import com.github.anhem.howto.model.Role;
import com.github.anhem.howto.model.RoleName;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Password;
import com.github.anhem.howto.model.id.Username;
import com.github.anhem.howto.repository.AccountPasswordRepository;
import com.github.anhem.howto.repository.AccountRepository;
import com.github.anhem.howto.repository.AccountRoleRepository;
import com.github.anhem.howto.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.github.anhem.howto.model.id.AccountPasswordId.NEW_ACCOUNT_PASSWORD_ID;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    public static final Account NEW_ACCOUNT = populate(Account.class).toBuilder()
            .accountId(AccountId.NEW_ACCOUNT_ID)
            .build();
    private static final Password PASSWORD = new Password("{bcrypt}encryptedPassword");
    @Captor
    ArgumentCaptor<AccountPassword> accountPasswordArgumentCaptor;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountPasswordRepository accountPasswordRepository;
    @Mock
    private AccountRoleRepository accountRoleRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private AccountService accountService;

    @Test
    void exceptionIsThrownWHenCreatingUserAccountWithAccountIdNotZero() {
        Account accountWithIdNotZero = populate(Account.class);

        assertThatThrownBy(() -> accountService.createUserAccount(accountWithIdNotZero, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid accountId");
    }

    @Test
    void exceptionIsThrownWhenCreatingUserAccountWithReservedUsername() {
        Account accountWithReservedUsername = populate(Account.class).toBuilder()
                .accountId(AccountId.NEW_ACCOUNT_ID)
                .username(Username.UNKNOWN)
                .build();

        assertThatThrownBy(() -> accountService.createUserAccount(accountWithReservedUsername, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reserved username");
    }

    @Test
    void exceptionIsThrownWhenCreatingUserAccountAndAccountAlreadyExists() {
        when(accountRepository.accountExists(NEW_ACCOUNT.getUsername(), NEW_ACCOUNT.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> accountService.createUserAccount(NEW_ACCOUNT, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username or email already exists");
    }

    @Test
    void accountIsCreatedWhenCallingCreateAccount() {
        AccountId expectedAccountId = new AccountId(100);
        Role role = populate(Role.class);
        when(roleRepository.getRoleByName(RoleName.USER)).thenReturn(role);
        when(accountRepository.createAccount(NEW_ACCOUNT)).thenReturn(expectedAccountId);

        AccountId accountId = accountService.createUserAccount(NEW_ACCOUNT, PASSWORD);

        assertThat(accountId).isEqualTo(expectedAccountId);
        verify(accountRoleRepository, times(1)).addRoleToAccount(eq(expectedAccountId), eq(role.getRoleId()), any(Instant.class));
        verify(accountPasswordRepository, times(1)).createPassword(accountPasswordArgumentCaptor.capture());
        AccountPassword accountPassword = accountPasswordArgumentCaptor.getValue();
        assertThat(accountPassword.getAccountPasswordId()).isEqualTo(NEW_ACCOUNT_PASSWORD_ID);
        assertThat(accountPassword.getAccountId()).isEqualTo(accountId);
        assertThat(accountPassword.getPassword()).isEqualTo(PASSWORD);
        assertThat(accountPassword.getCreated()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));

    }
}