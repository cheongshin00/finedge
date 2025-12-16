package com.finedge.finedgeapi.service;

import com.finedge.finedgeapi.audit.Audit;
import com.finedge.finedgeapi.entity.Account;
import com.finedge.finedgeapi.entity.JobHistory;
import com.finedge.finedgeapi.entity.Transaction;
import com.finedge.finedgeapi.entity.TransactionType;
import com.finedge.finedgeapi.repository.AccountRepository;
import com.finedge.finedgeapi.repository.JobHistoryRepository;
import com.finedge.finedgeapi.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;

@Service
public class ScheduledFinanceService {


    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final JobHistoryRepository jobHistoryRepository;

    public ScheduledFinanceService(AccountRepository accountRepository, TransactionRepository transactionRepository, JobHistoryRepository jobHistoryRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.jobHistoryRepository = jobHistoryRepository;
    }


    //@Scheduled(cron = "0 10 0 1 * *")
    @Audit(action = "MonthlyInterest", logRequest = true, logResponse = false)
    @Scheduled(cron = "0/30 * * * * *")
    public void runMonthlyInterestJob(){
        JobHistory history = startJob("monthly-interest");
        try {
            applyMonthlyInterest();
            finishJobSuccess(history, "Monthly interest applied successfully.");
        } catch (Exception e) {
            finishJobFailure(history, e.toString());
        }
    }

    //@Scheduled(cron = "0 10 0 1 * *")
    @Audit(action = "MonthlyFee", logRequest = true, logResponse = false)
    @Scheduled(cron = "0/30 * * * * *")
    public void runMonthlyFeeJob() {
        JobHistory history = startJob("monthly-fee");
        try {
            applyMonthlyFee();
            finishJobSuccess(history, "Monthly fees applied successfully.");
        } catch (Exception e) {
            finishJobFailure(history, e.toString());
        }
    }

    private JobHistory startJob(String jobName){
        JobHistory h = new JobHistory();
        h.setJobName(jobName);
        h.setStartedAt(LocalDateTime.now());
        h.setSuccess(false);
        return jobHistoryRepository.save(h);
    }

    private void finishJobSuccess(JobHistory history, String details){
        history.setFinishedAt(LocalDateTime.now());
        history.setSuccess(true);
        history.setDetails(details);
        jobHistoryRepository.save(history);
    }

    private void finishJobFailure(JobHistory history, String details){
        history.setFinishedAt(LocalDateTime.now());
        history.setSuccess(false);
        history.setDetails(details);
        jobHistoryRepository.save(history);
    }

    @Transactional
    protected void applyMonthlyInterest(){
        BigDecimal monthlyRate = new BigDecimal("0.005");
        List<Account> accounts = accountRepository.findByAccountType("SAVINGS");

        for (Account acc: accounts){
            if (acc.getBalance() == null) continue;

            BigDecimal interest = acc.getBalance().multiply(monthlyRate).setScale(2, BigDecimal.ROUND_HALF_UP);

            if (interest.compareTo(BigDecimal.ZERO) <= 0) continue;

            acc.setBalance(acc.getBalance().add(interest));
            accountRepository.save(acc);

            Transaction txn = new Transaction();
            txn.setReceiverAccountNumber(acc.getAccountNumber());
            txn.setAmount(interest);
            txn.setType(TransactionType.DEPOSIT);
            txn.setDescription("Monthly Interest");
            transactionRepository.save(txn);
        }
    }

    @Transactional
    protected void applyMonthlyFee(){
        BigDecimal fee = new BigDecimal("2.00");
        BigDecimal threshold = new BigDecimal("100.00");

        List<Account> accounts = accountRepository.findByAccountType("SAVINGS");

        for (Account acc: accounts){
            if (acc.getBalance() == null) continue;

            if (acc.getBalance().compareTo(threshold) < 0) {
                if (acc.getBalance().compareTo(fee)<0) {
                    continue;
                }
                acc.setBalance(acc.getBalance().subtract(fee));
                accountRepository.save(acc);

                Transaction txn = new Transaction();
                txn.setSenderAccountNumber(acc.getAccountNumber());
                txn.setAmount(fee);
                txn.setType(TransactionType.WITHDRAW);
                txn.setDescription("Monthly Maintenance Fee");
                transactionRepository.save(txn);
            }
        }

    }


}
