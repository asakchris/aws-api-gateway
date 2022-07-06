package com.example.metadata.audit;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuditLogRepository extends PagingAndSortingRepository<AuditLog, String> {}
