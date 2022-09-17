package com.github.anhem.howto.controller.api.mapper;

import com.github.anhem.howto.controller.api.AccountDTO;
import com.github.anhem.howto.controller.api.AccountsDTO;
import com.github.anhem.howto.model.Account;

import java.util.List;

import static com.github.anhem.howto.controller.api.mapper.AccountDTOMapper.mapToAccountDTOs;

public class AccountsDTOMapper {

    public static AccountsDTO mapToAccountsDTO(List<Account> accounts) {
        List<AccountDTO> accountDTOs = mapToAccountDTOs(accounts);
        return AccountsDTO.builder()
                .accountCount(accountDTOs.size())
                .accounts(accountDTOs)
                .build();
    }
}
