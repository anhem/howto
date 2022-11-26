package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.AccountDetailsDTO;
import com.github.anhem.howto.controller.model.AccountsDTO;
import com.github.anhem.howto.controller.model.CreateAccountDTO;
import com.github.anhem.howto.controller.model.MessageDTO;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.Password;
import com.github.anhem.howto.model.id.RoleName;
import com.github.anhem.howto.repository.AccountRoleRepository;
import com.github.anhem.howto.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.github.anhem.howto.controller.mapper.AccountDetailsDTOMapper.mapToAccountDetailsDTO;
import static com.github.anhem.howto.controller.mapper.AccountsDTOMapper.mapToAccountsDTO;
import static com.github.anhem.howto.controller.mapper.CreateAccountDTOMapper.mapToAccount;

@RestController
@RequestMapping(value = "api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountRoleRepository accountRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountController(AccountService accountService, AccountRoleRepository accountRoleRepository, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.accountRoleRepository = accountRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public AccountsDTO getAccounts() {
        return mapToAccountsDTO(accountService.getUsers());
    }

    @GetMapping(value = "/details/{accountId}")
    public AccountDetailsDTO getAccountDetails(@PathVariable int accountId) {
        AccountId accId = new AccountId(accountId);
        Account account = accountService.getAccount(accId);
        List<RoleName> roleNames = accountRoleRepository.getRoleNames(accId);
        return mapToAccountDetailsDTO(account, roleNames);
    }

    @DeleteMapping(value = "{accountId}")
    public MessageDTO removeAccount(@PathVariable int accountId) {
        accountService.removeAccount(new AccountId(accountId));
        return MessageDTO.OK;
    }

    @PostMapping(value = "users/user")
    public MessageDTO createUserAccount(@Valid @RequestBody CreateAccountDTO createAccountDTO) {
        return MessageDTO.fromId(accountService.createUserAccount(mapToAccount(createAccountDTO),
                new Password(passwordEncoder.encode(createAccountDTO.getPassword()))));
    }

    @PostMapping(value = "users/administrator")
    public MessageDTO createAdministratorAccount(@Valid @RequestBody CreateAccountDTO createAccountDTO) {
        return MessageDTO.fromId(accountService.createAdministratorAccount(mapToAccount(createAccountDTO),
                new Password(passwordEncoder.encode(createAccountDTO.getPassword()))));
    }

}
