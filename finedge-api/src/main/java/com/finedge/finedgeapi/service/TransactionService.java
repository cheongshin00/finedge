package com.finedge.finedgeapi.service;

import com.finedge.finedgeapi.audit.Audit;
import com.finedge.finedgeapi.entity.Account;
import com.finedge.finedgeapi.entity.Transaction;
import com.finedge.finedgeapi.entity.TransactionType;
import com.finedge.finedgeapi.repository.AccountRepository;
import com.finedge.finedgeapi.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }


    @Audit(action = "TRANSFER", logRequest = true, logResponse = false)
    @Transactional
    public Transaction transfer(String senderAccNo, String receiverAccNo, BigDecimal amount, String description){
        Account sender = accountRepository.findByAccountNumber(senderAccNo)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Account receiver = accountRepository.findByAccountNumber(receiverAccNo)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction txn = new Transaction();
        txn.setSenderAccountNumber(senderAccNo);
        txn.setReceiverAccountNumber(receiverAccNo);
        txn.setAmount(amount);
        txn.setType(TransactionType.TRANSFER);
        txn.setDescription(description);

        return transactionRepository.save(txn);

    }

    public List<Transaction> getAccountTransactions(String accountNumber) {
        return transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber);
    }

    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount, String description) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->new RuntimeException("Account not found"));
//        if (account.getBalance().compareTo(amount) < 0) {
//            throw new RuntimeException("Insufficient funds");
//        }
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction txn = new Transaction();
        txn.setReceiverAccountNumber(accountNumber);
        txn.setAmount(amount);
        txn.setType(TransactionType.DEPOSIT);
        txn.setDescription(description);

        return transactionRepository.save(txn);

    }

    @Transactional
    public Transaction withdraw(String accountNumber, BigDecimal amount, String description){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->new RuntimeException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction txn = new Transaction();
        txn.setSenderAccountNumber(accountNumber);
        txn.setAmount(amount);
        txn.setType(TransactionType.DEPOSIT);
        txn.setDescription(description);

        return transactionRepository.save(txn);
    }
}
