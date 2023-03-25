package com.github.anhem.howto.aggregator;

import com.github.anhem.howto.controller.model.AccountDetailsDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.RoleName;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.repository.AccountRoleRepository;
import com.github.anhem.howto.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.anhem.howto.controller.mapper.AccountDetailsDTOMapper.mapToAccountDetailsDTO;

@Component
public class AccountAggregator {

    private final AccountService accountService;
    private final AccountRoleRepository accountRoleRepository;

    public AccountAggregator(AccountService accountService, AccountRoleRepository accountRoleRepository) {
        this.accountService = accountService;
        this.accountRoleRepository = accountRoleRepository;
    }

    public AccountDetailsDTO getAccountDetails(AccountId accountId) {
        Account account = accountService.getAccount(accountId);
        List<RoleName> roleNames = accountRoleRepository.getRoleNames(accountId);
        return mapToAccountDetailsDTO(account, roleNames);
    }
}
