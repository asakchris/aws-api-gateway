package com.example.metadata.security;

import com.example.metadata.service.UserService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
  private final UserService userService;
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

  public SecurityConfig(
      UserService userService,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver springWebExceptionResolver) {
    this.userService = userService;
    this.restAuthenticationEntryPoint =
        new RestAuthenticationEntryPoint(springWebExceptionResolver);
  }

  // Overriding this bean from HttpSecurityConfiguration to disable default configs
  @Primary
  @Bean
  public HttpSecurity httpSecurity(
      ObjectPostProcessor<Object> objectPostProcessor,
      AuthenticationManagerBuilder authenticationManagerBuilder,
      ApplicationContext applicationContext) {
    return new HttpSecurity(
        objectPostProcessor,
        authenticationManagerBuilder,
        Map.of(ApplicationContext.class, applicationContext));
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().antMatchers("/actuator/**");
  }

  @Bean
  public SecurityFilterChain httpSecurityCustomizer(HttpSecurity http) throws Exception {
    http.authenticationManager(new ProviderManager(List.of(new UsernameAuthProvider(userService))))
        .exceptionHandling()
        .authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
        // Set this filter after exception handling filter - this way it will be able to handle any
        // Auth exceptions thrown properly from our last two filters
        .addFilterAfter(getRequestHeaderFilter(), ExceptionTranslationFilter.class)
        .authorizeRequests()
        .anyRequest()
        .authenticated();
    return http.build();
  }

  private RequestHeaderAuthenticationFilter getRequestHeaderFilter() {
    // Reusing a filter from Spring Security
    RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
    // This needs to be change once we move into token based auth
    filter.setPrincipalRequestHeader("App-User-Name");

    // Our Pre-auth provider just converts the corresponding security header to an instance of
    // UsernamePasswordAuthenticationToken. Note that the filter throws exception if header is
    // missing even before it reaches the provider. Basically, it just does a conversion logic
    filter.setAuthenticationManager(new ProviderManager(List.of(new SecurityHeaderAuthProvider())));

    // Set Authentication.details as the requestURI from our HTTP request (for logging purposes)
    filter.setAuthenticationDetailsSource(
        context -> context.getMethod() + " " + context.getRequestURI());
    return filter;
  }

  private static class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final HandlerExceptionResolver exceptionResolver;

    RestAuthenticationEntryPoint(HandlerExceptionResolver springWebExceptionResolver) {
      this.exceptionResolver = springWebExceptionResolver;
    }

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException) {
      exceptionResolver.resolveException(request, response, null, authException);
    }
  }
}
