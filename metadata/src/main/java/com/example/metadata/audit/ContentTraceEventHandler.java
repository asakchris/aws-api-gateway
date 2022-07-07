package com.example.metadata.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentTraceEventHandler {

  private final AuditLogService auditLogService;

  @Async
  @EventListener
  public void contentTraceEventListener(ContentTrace contentTrace) {
    if (log.isInfoEnabled()) {
      log.info("contentTrace event received: {}", contentTrace);
    }
    auditLogService.save(contentTrace);
  }
}
