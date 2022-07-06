package com.example.metadata.audit;

import java.time.LocalDateTime;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

  private final AuditLogRepository repository;

  @Override
  @Transactional
  public AuditLog save(ContentTrace contentTrace) {
    if (log.isInfoEnabled()) {
      log.info("RequestId: {}", contentTrace.getRequestId());
    }
    final AuditLog auditLog =
        AuditLog.builder()
            .requestId(contentTrace.getRequestId())
            .startTime(contentTrace.getStartTime())
            .method(contentTrace.getMethod())
            .uri(contentTrace.getUri())
            .username(contentTrace.getUsername())
            .requestBody(contentTrace.getRequestBody())
            .status(contentTrace.getStatus())
            .responseBody(contentTrace.getResponseBody())
            .timeTakenInMillis(contentTrace.getTimeTakenInMillis())
            .createdDate(LocalDateTime.now())
            .build();
    return repository.save(auditLog);
  }
}
