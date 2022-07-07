package com.example.metadata.audit;

import java.util.List;

public interface AuditLogService {
  AuditLog save(ContentTrace contentTrace);

  List<AuditLog> findAll();
}
