package com.finedge.finedgeapi.controller;

import com.finedge.finedgeapi.entity.Account;
import com.finedge.finedgeapi.service.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service){
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Account> getAllAccounts(){
        return service.getAllAccounts();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Account> getMyAccounts(){
        return service.getMyAccounts();
    };

    @GetMapping("/{id}")
    public Account getById(@PathVariable Long id){
        return service.getById(id).orElse(null);
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public Account createAccount(@RequestBody Account account){
        return service.create(account);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}
