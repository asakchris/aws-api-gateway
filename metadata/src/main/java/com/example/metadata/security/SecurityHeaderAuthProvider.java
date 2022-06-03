package com.example.metadata.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@Slf4j
public class SecurityHeaderAuthProvider implements AuthenticationProvider {
  @Override
  public UsernamePasswordAuthenticationToken authenticate(Authentication authentication)
      throws AuthenticationException {
    PreAuthenticatedAuthenticationToken token =
        (PreAuthenticatedAuthenticationToken) authentication;
    // Extract stored requestURI and username and convert to expected Authentication class
    String authDetails = token.getDetails().toString();
    String userNameFromHeader = (String) token.getPrincipal();
    log.info("usernameFromHeader={}, uri={}", userNameFromHeader, authDetails);

    UsernamePasswordAuthenticationToken convertedToken =
        new UsernamePasswordAuthenticationToken(
            userNameFromHeader, org.apache.commons.lang3.StringUtils.EMPTY);
    convertedToken.setDetails(authDetails);
    return convertedToken;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isAssignableFrom(PreAuthenticatedAuthenticationToken.class);
  }
}
