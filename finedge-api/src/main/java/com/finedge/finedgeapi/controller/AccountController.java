package com.finedge.finedgeapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.finedge.finedgeapi.entity.Account;
import com.finedge.finedgeapi.entity.User;
import com.finedge.finedgeapi.security.CustomUserDetails;
import com.finedge.finedgeapi.service.AccountService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAccounts(@AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        return accountService.getAccountByUser(user);
    }

    @PostMapping
    public Account createAccount(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam String accountType, @RequestParam String currency,
                                 @RequestHeader("Idempotency-Key") String idempotencyKey) throws JsonProcessingException {
        User user = userDetails.getUser();
        return accountService.create(user, accountType,currency, idempotencyKey);

    }
}
