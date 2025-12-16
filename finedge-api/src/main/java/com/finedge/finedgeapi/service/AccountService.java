package com.finedge.finedgeapi.service;

import com.finedge.finedgeapi.audit.Audit;
import com.finedge.finedgeapi.entity.Account;
import com.finedge.finedgeapi.entity.User;
import com.finedge.finedgeapi.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository repo;

    public AccountService(AccountRepository repo){
        this.repo = repo;
    }

    public List<Account> getAccountByUser(User user){
        return repo.findByUser(user);
    }

    public List<Account> getAll(){
        return repo.findAll();
    }

    public Optional<Account> getById(Long id){
        return repo.findById(id);
    }

    @Audit(action = "CreateAccount", logRequest = true, logResponse = false)
    @Transactional
    public Account create(User user, String accountType , String currency){
        Account account = Account.builder()
                .accountNumber(UUID.randomUUID().toString().substring(0,10))
                .accountType(accountType)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .createdDt(LocalDateTime.now())
                .user(user)
                .build();

        return repo.save(account);
    }

    public Account getAccountByAccountNumber(String accountNumber){
        return repo.findByAccountNumber(accountNumber).orElseThrow(()->new RuntimeException("Account not found"));
    }

    public void delete(Long id){
        repo.deleteById(id);
    }

}
