package com.github.anhem.howto.controller.api.mapper;

import com.github.anhem.howto.controller.api.CreateAccountDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.id.Id;
import com.github.anhem.howto.model.id.Username;

import java.time.Instant;

import static com.github.anhem.howto.model.id.AccountId.NEW_ACCOUNT_ID;

public class CreateAccountDTOMapper {

    public static Account mapToAccount(CreateAccountDTO createAccountDTO) {
        Instant now = Instant.now();
        return Account.builder()
                .accountId(NEW_ACCOUNT_ID)
                .username(Id.of(Username.class, createAccountDTO.getUsername()))
                .email(createAccountDTO.getEmail())
                .firstName(createAccountDTO.getFirstName())
                .lastName(createAccountDTO.getLastName())
                .created(now)
                .lastUpdated(now)
                .build();
    }
}
