package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.api.AccountDTO;
import com.github.anhem.howto.controller.api.AccountsDTO;
import com.github.anhem.howto.controller.api.CreateAccountDTO;
import com.github.anhem.howto.controller.api.MessageDTO;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Password;
import com.github.anhem.howto.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.github.anhem.howto.controller.api.mapper.AccountDTOMapper.mapToAccountDTO;
import static com.github.anhem.howto.controller.api.mapper.AccountsDTOMapper.mapToAccountsDTO;
import static com.github.anhem.howto.controller.api.mapper.CreateAccountDTOMapper.mapToAccount;

@RestController
@RequestMapping(value = "api/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public AccountsDTO getAccounts() {
        return mapToAccountsDTO(accountService.getUsers());
    }

    @GetMapping(value = "{accountId}")
    public AccountDTO getAccount(@PathVariable int accountId) {
        return mapToAccountDTO(accountService.getAccount(AccountId.of(accountId)));
    }

    @DeleteMapping(value = "{accountId}")
    public MessageDTO removeAccount(@PathVariable int accountId) {
        accountService.removeAccount(AccountId.of(accountId));
        return MessageDTO.OK;
    }

    @PostMapping
    public MessageDTO createAccount(@Valid @RequestBody CreateAccountDTO createAccountDTO) {
        return MessageDTO.fromId(accountService.createAccount(mapToAccount(createAccountDTO), Password.of(createAccountDTO.getPassword())));
    }

}
