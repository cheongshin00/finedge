package com.finedge.finedgeapi.controller;

import com.finedge.finedgeapi.entity.Account;
import com.finedge.finedgeapi.service.AccountService;
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
    public List<Account> getAll(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Account getById(@PathVariable Long id){
        return service.getById(id).orElse(null);
    }

    @PostMapping
    public Account create(@RequestBody Account account){
        return service.create(account);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}
