package com.scarletgryffin.data.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

/**
 * Spring Web Flux Security configuration.
 */
@Slf4j
@EnableWebFluxSecurity
@Configuration(proxyBeanMethods = false)
public class WebFluxSecurityConfiguration {

  @Value("${spring.webflux.base-path}")
  private String contextPath;

  /**
   * Initializes the FilterChain for the Spring Web Flux APIs.
   */
  @Bean
  public SecurityWebFilterChain apiFilterChain(final ServerHttpSecurity http) {
    http.securityMatcher(ServerWebExchangeMatchers.pathMatchers(contextPath + "/**"))
        .authorizeExchange(exchange -> exchange
            .pathMatchers(contextPath + "/admin/**").hasRole("ADMIN")
            .pathMatchers(contextPath + "/**").hasAnyRole("USER", "ADMIN")
            .anyExchange().authenticated())
        .httpBasic(Customizer.withDefaults())
        .formLogin(Customizer.withDefaults())
        .csrf(CsrfSpec::disable);
    return http.build();
  }

  /**
   * Initializes the FilterChain for the Spring Web Flux for allowed endpoints.
   */
  @Bean
  public SecurityWebFilterChain webFilterChain(final ServerHttpSecurity http) {
    http.authorizeExchange(exchange -> exchange
            .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .pathMatchers("/public/**").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            .anyExchange().authenticated())
        .formLogin(Customizer.withDefaults());
    return http.build();
  }
}
