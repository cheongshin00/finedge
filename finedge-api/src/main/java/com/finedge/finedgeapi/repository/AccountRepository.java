package com.finedge.finedgeapi.repository;

import com.finedge.finedgeapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
