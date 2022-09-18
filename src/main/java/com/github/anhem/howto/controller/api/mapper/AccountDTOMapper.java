package com.github.anhem.howto.controller.api.mapper;

import com.github.anhem.howto.controller.api.AccountDTO;
import com.github.anhem.howto.model.Account;

import java.util.List;
import java.util.stream.Collectors;

public class AccountDTOMapper {

    public static List<AccountDTO> mapToAccountDTOs(List<Account> accounts) {
        return accounts.stream()
                .map(AccountDTOMapper::mapToAccountDTO)
                .collect(Collectors.toList());
    }

    public static AccountDTO mapToAccountDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getAccountId().value())
                .username(account.getUsername().value())
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .build();
    }
}
