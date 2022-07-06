package com.example.metadata.audit;

public interface AuditLogService {
  AuditLog save(ContentTrace contentTrace);
}
