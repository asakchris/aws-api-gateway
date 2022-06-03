package com.example.metadata.security;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.example.metadata.entity.User;
import com.example.metadata.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
@Slf4j
public class UsernameAuthProvider implements AuthenticationProvider {

  private final UserService userService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    UsernamePasswordAuthenticationToken token =
        (UsernamePasswordAuthenticationToken) authentication;
    String userNameFromContext = (String) token.getPrincipal();
    log.info("userNameFromContext: {}", userNameFromContext);
    final User user =
        Optional.ofNullable(userNameFromContext)
            .flatMap(userName -> userService.findUser(userName))
            .orElseThrow(
                () -> new BadCredentialsException("Invalid User - " + userNameFromContext));
    log.info("user: {}", user);
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(user.getUserName(), EMPTY);
    authenticationToken.setDetails(user);
    return authenticationToken;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
  }
}
