package com.example.metadata.controller;

import com.example.metadata.audit.AuditLog;
import com.example.metadata.audit.AuditLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuditController {
  private final AuditLogService service;

  @GetMapping(value = "/audit-logs")
  @ResponseStatus(code = HttpStatus.OK)
  public List<AuditLog> findAllLogs() {
    log.info("Enter getAllLogs");
    return service.findAll();
  }
}
