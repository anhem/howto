package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.AccountDetailsDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.RoleName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.howto.controller.mapper.AccountDetailsDTOMapper.mapToAccountDetailsDTO;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class AccountDetailsDTOMapperTest {

    @Test
    void mappedToDTO() {
        Account account = populate(Account.class);
        List<RoleName> roleNames = List.of(populate(RoleName.class));

        AccountDetailsDTO accountDetailsDTO = mapToAccountDetailsDTO(account, roleNames);

        assertThat(accountDetailsDTO).hasNoNullFieldsOrProperties();
        assertThat(accountDetailsDTO.getAccount().getId()).isEqualTo(account.getAccountId().value());
        assertThat(accountDetailsDTO.getCreated()).isEqualTo(account.getCreated());
        assertThat(accountDetailsDTO.getUpdated()).isEqualTo(account.getLastUpdated());
        assertThat(accountDetailsDTO.getRoles()).hasSize(roleNames.size());
        assertThat(accountDetailsDTO.getRoles()).contains(roleNames.get(0).getRole());
    }

}