package com.github.anhem.howto.controller.api.mapper;

import com.github.anhem.howto.controller.api.AccountDTO;
import com.github.anhem.howto.model.Account;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.controller.api.mapper.AccountDTOMapper.mapToAccountDTO;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class AccountDTOMapperTest {

    @Test
    void mappedToDTO() {
        Account account = populate(Account.class);

        AccountDTO accountDTO = mapToAccountDTO(account);

        assertThat(accountDTO).hasNoNullFieldsOrProperties();
        assertThat(accountDTO.getId()).isEqualTo(account.getAccountId().value());
    }

}