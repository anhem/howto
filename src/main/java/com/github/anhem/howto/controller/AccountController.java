package com.github.anhem.howto.controller;

import com.github.anhem.howto.aggregator.AccountAggregator;
import com.github.anhem.howto.controller.model.AccountDetailsDTO;
import com.github.anhem.howto.controller.model.AccountsDTO;
import com.github.anhem.howto.controller.model.CreateAccountDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Password;
import com.github.anhem.howto.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.github.anhem.howto.configuration.JwtTokenFilter.BEARER_AUTHENTICATION;
import static com.github.anhem.howto.controller.mapper.AccountsDTOMapper.mapToAccountsDTO;
import static com.github.anhem.howto.controller.mapper.CreateAccountDTOMapper.mapToAccount;
import static com.github.anhem.howto.model.id.RoleName.Constants.ADMINISTRATOR;

@RestController
@Secured(ADMINISTRATOR)
@RequestMapping(value = "api/accounts")
@SecurityRequirement(name = BEARER_AUTHENTICATION)
public class AccountController {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final AccountAggregator accountAggregator;

    @Autowired
    public AccountController(AccountService accountService, PasswordEncoder passwordEncoder, AccountAggregator accountAggregator) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.accountAggregator = accountAggregator;
    }

    @GetMapping
    public AccountsDTO getAccounts() {
        return mapToAccountsDTO(accountService.getUsers());
    }

    @GetMapping(value = "details/{accountId}")
    public AccountDetailsDTO getAccountDetails(@PathVariable int accountId) {
        return accountAggregator.getAccountDetails(new AccountId(accountId));
    }

    @DeleteMapping(value = "{accountId}")
    public MessageDTO removeAccount(@PathVariable int accountId) {
        accountService.removeAccount(new AccountId(accountId));
        return MessageDTO.OK;
    }

    @PostMapping(value = "users/user")
    public MessageDTO createUserAccount(@Valid @RequestBody CreateAccountDTO createAccountDTO) {
        return MessageDTO.fromId(accountService.createUserAccount(mapToAccount(createAccountDTO), new Password(passwordEncoder.encode(createAccountDTO.getPassword()))));
    }

    @PostMapping(value = "users/administrator")
    public MessageDTO createAdministratorAccount(@Valid @RequestBody CreateAccountDTO createAccountDTO) {
        return MessageDTO.fromId(accountService.createAdministratorAccount(mapToAccount(createAccountDTO), new Password(passwordEncoder.encode(createAccountDTO.getPassword()))));
    }

}

