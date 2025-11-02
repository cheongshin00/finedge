package com.finedge.finedgeapi.service;

import com.finedge.finedgeapi.entity.Account;
import com.finedge.finedgeapi.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository repo;

    public AccountService(AccountRepository repo){
        this.repo = repo;
    }

    public List<Account> getAllAccounts(){
        return repo.findAll();
    }

    public List<Account> getMyAccounts(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return repo.findByOwnerName(username);
    }

    public Optional<Account> getById(Long id){
        return repo.findById(id);
    }

    public Account create(Account account){
        return repo.save(account);
    }

    public void delete(Long id){
        repo.deleteById(id);
    }

}
