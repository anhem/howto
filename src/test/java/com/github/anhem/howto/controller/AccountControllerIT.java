package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.AccountDTO;
import com.github.anhem.howto.controller.model.AccountsDTO;
import com.github.anhem.howto.controller.model.CreateAccountDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import com.github.anhem.howto.testutil.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class AccountControllerIT extends TestApplication {

    private static final String CREATE_USER_ACCOUNT_URL = "/api/accounts/users/user";
    private static final String GET_ACCOUNT_URL = "/api/accounts/%d";
    private static final String GET_ACCOUNTS_URL = "/api/accounts";
    public static final String DELETE_ACCOUNT_URL = "/api/accounts/%d";

    @Test
    void crud() {
        CreateAccountDTO createAccountDTO = populate(CreateAccountDTO.class).toBuilder()
                .email("integration@test.com")
                .build();

        ResponseEntity<MessageDTO> createResponse = testRestTemplate.postForEntity(CREATE_USER_ACCOUNT_URL, createAccountDTO, MessageDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();

        int accountId = Integer.parseInt(createResponse.getBody().getMessage());

        ResponseEntity<AccountDTO> accountResponse = testRestTemplate.getForEntity(String.format(GET_ACCOUNT_URL, accountId), AccountDTO.class);
        assertThat(accountResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertAccount(accountResponse.getBody(), accountId, createAccountDTO);

        ResponseEntity<AccountsDTO> accountsResponse = testRestTemplate.getForEntity(GET_ACCOUNTS_URL, AccountsDTO.class);

        assertThat(accountsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AccountsDTO accountsDTO = accountsResponse.getBody();
        assertThat(accountsDTO).isNotNull();
        assertThat(accountsDTO.getAccountCount()).isEqualTo(accountsDTO.getAccounts().size());
        assertThat(accountsDTO.getAccounts()).contains(accountResponse.getBody());

        ResponseEntity<MessageDTO> deleteResponse = deleteForEntity(String.format(DELETE_ACCOUNT_URL, accountId));

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isNotNull();
        assertThat(deleteResponse.getBody()).isEqualTo(MessageDTO.OK);
    }

    private static void assertAccount(AccountDTO accountDTO, int accountId, CreateAccountDTO createAccountDTO) {
        assertThat(accountDTO).isNotNull();
        assertThat(accountDTO.getId()).isEqualTo(accountId);
        assertThat(accountDTO.getUsername()).isEqualTo(createAccountDTO.getUsername());
        assertThat(accountDTO.getEmail()).isEqualTo(createAccountDTO.getEmail());
        assertThat(accountDTO.getFirstName()).isEqualTo(createAccountDTO.getFirstName());
        assertThat(accountDTO.getLastName()).isEqualTo(createAccountDTO.getLastName());
    }

}