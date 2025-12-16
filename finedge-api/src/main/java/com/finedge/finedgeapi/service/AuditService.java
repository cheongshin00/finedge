package com.finedge.finedgeapi.service;

import com.finedge.finedgeapi.entity.AuditLog;
import com.finedge.finedgeapi.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) {
        this.repo = repo;
    }

    @Async("AuditTaskExecutor")
    @Transactional
    public void persist(AuditLog auditLog) {
        repo.save(auditLog);
    }


}
