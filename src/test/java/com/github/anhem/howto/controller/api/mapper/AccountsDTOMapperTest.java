package com.github.anhem.howto.controller.api.mapper;

import com.github.anhem.howto.controller.api.AccountsDTO;
import com.github.anhem.howto.model.Account;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.howto.controller.api.mapper.AccountsDTOMapper.mapToAccountsDTO;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class AccountsDTOMapperTest {

    @Test
    void mappedToDTO() {
        AccountsDTO accountsDTO = mapToAccountsDTO(List.of(populate(Account.class)));

        assertThat(accountsDTO).hasNoNullFieldsOrProperties();
        assertThat(accountsDTO.getAccountCount()).isEqualTo(1);
        assertThat(accountsDTO.getAccounts()).hasSize(1);

    }

}