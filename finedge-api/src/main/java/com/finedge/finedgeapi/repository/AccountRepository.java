package com.finedge.finedgeapi.repository;

import com.finedge.finedgeapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerName(String username);
}
