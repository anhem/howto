package com.github.anhem.howto.service;

import com.github.anhem.howto.configuration.HowtoConfig;
import com.github.anhem.howto.model.Account;
import com.github.anhem.howto.model.AccountPassword;
import com.github.anhem.howto.model.RoleName;
import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.JwtToken;
import com.github.anhem.howto.model.id.Username;
import com.github.anhem.howto.repository.AccountPasswordRepository;
import com.github.anhem.howto.repository.AccountRepository;
import com.github.anhem.howto.repository.AccountRoleRepository;
import com.github.anhem.howto.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.anhem.howto.util.JwtUtil.getRoles;
import static com.github.anhem.howto.util.JwtUtil.getUsername;

@Service
public class AuthService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AccountPasswordRepository accountPasswordRepository;
    private final String jwtSecret;

    public AuthService(AccountRepository accountRepository, AccountRoleRepository accountRoleRepository, AccountPasswordRepository accountPasswordRepository, HowtoConfig howtoConfig) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.accountPasswordRepository = accountPasswordRepository;
        this.jwtSecret = howtoConfig.getJwtSecret();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.getAccount(new Username(username));
        AccountPassword accountPassword = accountPasswordRepository.getPassword(account.getAccountId());
        List<RoleName> roleNames = accountRoleRepository.getRoleNames(account.getAccountId());

        List<SimpleGrantedAuthority> simpleGrantedAuthorities = roleNames.stream()
                .map(roleName -> new SimpleGrantedAuthority(roleName.getValue()))
                .toList();

        return new User(
                account.getUsername().value(),
                accountPassword.getPassword().value(),
                simpleGrantedAuthorities);
    }

    public JwtToken generateToken(UserDetails userDetails) {
        return JwtUtil.generateToken(userDetails, jwtSecret);
    }

    public boolean validateToken(JwtToken jwtToken) {
        return JwtUtil.validateToken(jwtToken, jwtSecret);
    }

    public void setSecurityContext(WebAuthenticationDetails webAuthenticationDetails, JwtToken jwtToken) {
        Username username = getUsername(jwtToken, jwtSecret);
        List<SimpleGrantedAuthority> roles = getRoles(jwtToken, jwtSecret);
        UserDetails userDetails = new User(username.value(), "", roles);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authentication.setDetails(webAuthenticationDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public AccountId getAccountId() {
        UserDetails userDetails = getUserDetailsFromSecurityContext();
        return accountRepository.getAccount(new Username(userDetails.getUsername())).getAccountId();
    }

    private static UserDetails getUserDetailsFromSecurityContext() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
