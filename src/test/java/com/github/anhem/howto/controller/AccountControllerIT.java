package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.*;
import com.github.anhem.howto.testutil.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.github.anhem.howto.testutil.TestPopulator.populate;
import static org.assertj.core.api.Assertions.assertThat;

class AccountControllerIT extends TestApplication {

    private static final String CREATE_USER_ACCOUNT_URL = "/api/accounts/users/user";
    private static final String GET_ACCOUNT_DETAILS_URL = "/api/accounts/details/%d";
    private static final String GET_ACCOUNTS_URL = "/api/accounts";
    public static final String DELETE_ACCOUNT_URL = "/api/accounts/%d";

    @Test
    void crud() {
        CreateAccountDTO createAccountDTO = populate(CreateAccountDTO.class).toBuilder()
                .email("integration@test.com")
                .build();

        ResponseEntity<MessageDTO> createResponse = postWithToken(CREATE_USER_ACCOUNT_URL, createAccountDTO, MessageDTO.class, adminJwtToken);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();

        int accountId = Integer.parseInt(createResponse.getBody().getMessage());

        ResponseEntity<AccountDetailsDTO> accountDetailsResponse = getWithToken(String.format(GET_ACCOUNT_DETAILS_URL, accountId), AccountDetailsDTO.class, adminJwtToken);
        assertThat(accountDetailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(accountDetailsResponse.getBody()).isNotNull();
        assertAccount(accountDetailsResponse.getBody().getAccount(), accountId, createAccountDTO);

        ResponseEntity<AccountsDTO> accountsResponse = getWithToken(GET_ACCOUNTS_URL, AccountsDTO.class, adminJwtToken);

        assertThat(accountsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AccountsDTO accountsDTO = accountsResponse.getBody();
        assertThat(accountsDTO).isNotNull();
        assertThat(accountsDTO.getAccountCount()).isEqualTo(accountsDTO.getAccounts().size());
        assertThat(accountsDTO.getAccounts()).contains(accountDetailsResponse.getBody().getAccount());

        ResponseEntity<MessageDTO> deleteResponse = deleteWithToken(String.format(DELETE_ACCOUNT_URL, accountId), MessageDTO.class, adminJwtToken);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isNotNull();
        assertThat(deleteResponse.getBody()).isEqualTo(MessageDTO.OK);
    }

    @Test
    void userIsUnauthorized() {
        CreateAccountDTO createAccountDTO = populate(CreateAccountDTO.class).toBuilder()
                .email("integration@test.com")
                .build();

        ResponseEntity<MessageDTO> createResponse = postWithToken(CREATE_USER_ACCOUNT_URL, createAccountDTO, MessageDTO.class, userJwtToken);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
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