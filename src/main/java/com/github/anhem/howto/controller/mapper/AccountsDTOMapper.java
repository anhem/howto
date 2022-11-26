package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.AccountDTO;
import com.github.anhem.howto.controller.model.AccountsDTO;
import com.github.anhem.howto.model.Account;

import java.util.List;

public class AccountsDTOMapper {

    public static AccountsDTO mapToAccountsDTO(List<Account> accounts) {
        List<AccountDTO> accountDTOs = AccountDTOMapper.mapToAccountDTOs(accounts);
        return AccountsDTO.builder()
                .accountCount(accountDTOs.size())
                .accounts(accountDTOs)
                .build();
    }
}
