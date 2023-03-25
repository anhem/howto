package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.AccountDetailsDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.RoleName;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.howto.controller.mapper.AccountDTOMapper.mapToAccountDTO;

public class AccountDetailsDTOMapper {

    public static AccountDetailsDTO mapToAccountDetailsDTO(Account account, List<RoleName> roleNames) {
        List<String> roles = roleNames.stream()
                .map(RoleName::getValue)
                .collect(Collectors.toList());

        return AccountDetailsDTO.builder()
                .account(mapToAccountDTO(account))
                .created(account.getCreated())
                .updated(account.getLastUpdated())
                .roles(roles)
                .build();
    }
}
