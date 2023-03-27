package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.AccountDTO;
import com.github.anhem.howto.model.Account;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.howto.controller.mapper.AccountDTOMapper.mapToAccountDTOs;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class AccountDTOMapperTest {

    @Test
    void mappedToDTO() {
        Account account = populate(Account.class);

        List<AccountDTO> accountDTOs = mapToAccountDTOs(List.of(account));

        assertThat(accountDTOs).hasSize(1);
        AccountDTO accountDTO = accountDTOs.get(0);
        assertThat(accountDTO).hasNoNullFieldsOrProperties();
        assertThat(accountDTO.getId()).isEqualTo(account.getAccountId().value());
    }

}