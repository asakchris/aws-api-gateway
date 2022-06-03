package com.example.metadata.error;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class SecurityExceptionHandler {
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(BadCredentialsException.class)
  public ValidationErrorResponse handleInvalidUserAccess(
      HttpServletRequest request, BadCredentialsException exception) {
    if (log.isInfoEnabled()) {
      log.info(
          "Status 403 auth={} uri={}",
          exception.getMessage(),
          request.getMethod() + " " + request.getRequestURI());
    }

    Violation violation = new Violation();
    violation.setMessage(exception.getMessage());
    ValidationErrorResponse response = new ValidationErrorResponse();
    response.getViolations().add(violation);
    return response;
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(PreAuthenticatedCredentialsNotFoundException.class)
  public ValidationErrorResponse handleMissingSecurityHeader(
      HttpServletRequest request, PreAuthenticatedCredentialsNotFoundException exception) {
    if (log.isInfoEnabled()) {
      log.info(
          "Status 403 auth={} uri={}",
          exception.getMessage(),
          request.getMethod() + " " + request.getRequestURI());
    }

    Violation violation = new Violation();
    violation.setMessage(exception.getMessage());
    ValidationErrorResponse response = new ValidationErrorResponse();
    response.getViolations().add(violation);
    return response;
  }
}
