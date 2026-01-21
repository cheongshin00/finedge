package com.finedge.finedgeapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.finedge.finedgeapi.entity.IdempotencyRecord;
import com.finedge.finedgeapi.repository.IdempotencyRecordRepository;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;

public class IdempotencyService {

    private final IdempotencyRecordRepository repo;

    public IdempotencyService(IdempotencyRecordRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Optional<IdempotencyRecord> findExisting(String idempotencyKey) {
        return repo.findByIdempotencyKey(idempotencyKey);
    }

    @Transactional
    public void save(
            String idempotencyKey,
            String resourceType,
            String resourceId
    ) {
        IdempotencyRecord key = new IdempotencyRecord();
        key.setIdempotencyKey(idempotencyKey);
        key.setResourceType(resourceType);
        key.setResourceId(resourceId);
        key.setCreatedAt(LocalDateTime.now());

        repo.save(key);
    }

}
