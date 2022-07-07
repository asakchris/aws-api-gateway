package com.example.metadata.audit;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import com.example.metadata.constant.Headers.Constants;
import com.example.metadata.entity.User;
import io.vavr.control.Option;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomHttpTraceFilter extends OncePerRequestFilter {
  private static final int MAX_CONTENT_LENGTH = 200;
  private static final int MAX_URI_LENGTH = 200;
  private static final String APP_REQUEST_ID = "APP_REQUEST_ID";
  private static final String HEADER_REQUEST_ID = "Request-Id";
  private LocalDateTime startTime;
  private final ApplicationEventPublisher publisher;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    ContentCachingRequestWrapper requestWrapper =
        new ContentCachingRequestWrapper(request, MAX_CONTENT_LENGTH);
    ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
    startTime = LocalDateTime.now();

    // If request id header is present then use it or create new one
    final Optional<String> header = Optional.ofNullable(request.getHeader(HEADER_REQUEST_ID));
    final String requestId = header.orElse(UUID.randomUUID().toString());
    if (log.isInfoEnabled()) {
      log.info("header: {}, requestId: {}", header, requestId);
    }
    MDC.put(APP_REQUEST_ID, requestId);
    // If request id header is present then add it in the response
    header.ifPresentOrElse(s -> {}, () -> response.setHeader(HEADER_REQUEST_ID, requestId));

    try {
      filterChain.doFilter(requestWrapper, responseWrapper);
    } finally {
      try {
        afterRequest(requestId, requestWrapper, responseWrapper);
      } finally {
        MDC.remove(APP_REQUEST_ID);
      }
    }
  }

  private void afterRequest(
      String requestId,
      ContentCachingRequestWrapper requestWrapper,
      ContentCachingResponseWrapper responseWrapper)
      throws IOException {
    String method = requestWrapper.getMethod();

    // Concatenate request URI & query string and then get 1st 200 characters
    final String uri =
        StringUtils.left(
            getUri(requestWrapper.getRequestURI(), requestWrapper.getQueryString()),
            MAX_URI_LENGTH);

    if (uri.matches(".*/h2-console/.*") || uri.contains("h2-console")) {
      if (log.isInfoEnabled()) {
        log.info("Ignoring h2 console requests: {}", uri);
      }
      responseWrapper.copyBodyToResponse();
      return;
    }

    String userName =
        Optional.ofNullable(requestWrapper.getHeader(Constants.USER_NAME_VALUE))
            .orElseGet(
                () -> {
                  User user = getUserFromSecurityContext();
                  return user == null ? "Unknown" : user.getUserName();
                });

    final ContentTrace contentTrace =
        ContentTrace.builder()
            .requestId(requestId)
            .startTime(startTime)
            .method(method)
            .uri(uri)
            .username(userName)
            .requestBody(getRequestPayload(requestWrapper))
            .status(responseWrapper.getStatus())
            .responseBody(getResponsePayload(responseWrapper))
            .timeTakenInMillis(ChronoUnit.MILLIS.between(startTime, LocalDateTime.now()))
            .build();

    // Important to copy the original response body, because it is removed
    responseWrapper.copyBodyToResponse();

    // publish event
    publisher.publishEvent(contentTrace);
  }

  public User getUserFromSecurityContext() {
    return Option.of(getContext().getAuthentication())
        .map(auth -> (User) auth.getDetails())
        .getOrNull();
  }

  private String getUri(String requestUri, @Nullable String queryString) {
    return Stream.of(requestUri, queryString)
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.joining("?"));
  }

  private String getRequestPayload(HttpServletRequest request) {
    ContentCachingRequestWrapper wrapper =
        WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
    if (wrapper != null) {
      byte[] buf = wrapper.getContentAsByteArray();
      if (buf.length > 0) {
        int length = buf.length;
        return new String(buf, 0, Math.min(length, MAX_CONTENT_LENGTH), StandardCharsets.UTF_8);
      }
    }
    return null;
  }

  private String getResponsePayload(ContentCachingResponseWrapper wrappedResponse) {
    final int contentSize = wrappedResponse.getContentSize();
    if (contentSize <= 0) {
      return null;
    }
    return new String(
            wrappedResponse.getContentAsByteArray(),
            0,
            Math.min(contentSize, MAX_CONTENT_LENGTH),
            StandardCharsets.UTF_8)
        .replace(System.getProperty("line.separator"), StringUtils.EMPTY);
  }
}
