package com.github.anhem.howto.controller.mapper;

import com.github.anhem.howto.controller.model.CreateAccountDTO;
import com.github.anhem.howto.model.Account;
import org.junit.jupiter.api.Test;

import static com.github.anhem.howto.controller.mapper.CreateAccountDTOMapper.mapToAccount;
import static com.github.anhem.howto.model.id.AccountId.NEW_ACCOUNT_ID;
import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class CreateAccountDTOMapperTest {

    @Test
    void mappedToDTO() {
        CreateAccountDTO createAccountDTO = populate(CreateAccountDTO.class);

        Account account = mapToAccount(createAccountDTO);

        assertThat(account).hasNoNullFieldsOrPropertiesExcept("lastLogin");
        assertThat(account.getLastLogin()).isNull();
        assertThat(account.getAccountId()).isEqualTo(NEW_ACCOUNT_ID);
    }

}