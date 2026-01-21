package com.finedge.finedgeapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.finedge.finedgeapi.audit.Audit;
import com.finedge.finedgeapi.entity.Account;
import com.finedge.finedgeapi.entity.IdempotencyRecord;
import com.finedge.finedgeapi.entity.User;
import com.finedge.finedgeapi.monitoring.AccountMetrics;
import com.finedge.finedgeapi.repository.AccountRepository;
import com.finedge.finedgeapi.repository.IdempotencyRecordRepository;
import jakarta.transaction.Transactional;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final IdempotencyRecordRepository idempotencyRepo;
    private final AccountMetrics metrics;

    public AccountService(AccountRepository repo, IdempotencyRecordRepository idempotencyRepo, AccountMetrics metrics){
        this.repo = repo;
        this.idempotencyRepo = idempotencyRepo;
        this.metrics = metrics;
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
    public Account create(User user, String accountType , String currency, String idempotencyKey)
    throws JsonProcessingException {

        Optional<IdempotencyRecord> existingKey = idempotencyRepo.findByIdempotencyKey(idempotencyKey);

        if(existingKey.isPresent()) {
            return repo.findById(Long.valueOf(existingKey.get().getResourceId())
            ).orElseThrow(()-> new IllegalStateException("Idempotent resource not found"));
        }

        Account account = Account.builder()
                .accountNumber(UUID.randomUUID().toString().substring(0,10))
                .accountType(accountType)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .createdDt(LocalDateTime.now())
                .user(user)
                .build();

        Account savedAccount = repo.save(account);

        IdempotencyRecord key = new IdempotencyRecord();
        key.setIdempotencyKey(idempotencyKey);
        key.setResourceId(savedAccount.getId().toString());
        key.setResourceType("ACCOUNT");
        key.setCreatedAt(LocalDateTime.now());

        idempotencyRepo.save(key);

        metrics.increment();

        return savedAccount;
    }

    public Account getAccountByAccountNumber(String accountNumber){
        return repo.findByAccountNumber(accountNumber).orElseThrow(()->new RuntimeException("Account not found"));
    }

    public void delete(Long id){
        repo.deleteById(id);
    }

}
