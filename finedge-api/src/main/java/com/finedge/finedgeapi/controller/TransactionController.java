package com.finedge.finedgeapi.controller;

import com.finedge.finedgeapi.entity.Transaction;
import com.finedge.finedgeapi.service.TransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public Transaction transfer (@RequestBody Map<String, Object> request) {
        String sender = (String) request.get("sender");
        String receiver = (String) request.get("receiver");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.getOrDefault("description", "Transfer");

        return transactionService.transfer(sender, receiver, amount, description);

    }


    @GetMapping("/{accountnumber}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public List<Transaction> getTransactions(@PathVariable String accountNumber){
        return transactionService.getAccountTransactions(accountNumber);
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public Transaction deposit (@RequestBody Map<String, Object> request) {
        String account = (String) request.get("account");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.get("description");

        return transactionService.deposit(account, amount, description);
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public Transaction withdraw (@RequestBody Map<String, Object> request) {
        String account = (String) request.get("account");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.get("description");

        return transactionService.withdraw(account, amount, description);
    }

}
