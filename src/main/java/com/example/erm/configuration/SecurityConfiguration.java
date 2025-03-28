package com.example.erm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.example.erm.api.util.ProblemDetailFactory;
import com.example.erm.repository.UserRepository;
import com.example.erm.security.CustomAuthenticationEntryPoint;
import com.example.erm.security.MongoUserDetailsService;
import com.example.erm.security.SecurityRole;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      SecurityContextRepository securityContextRepository,
      AuthenticationEntryPoint authenticationEntryPoint)
      throws Exception {
    return http.authorizeHttpRequests(
            registry ->
                registry
                    .requestMatchers(HttpMethod.OPTIONS)
                    .permitAll()
                    .requestMatchers("/api/user", "/api/user/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(c -> c.authenticationEntryPoint(authenticationEntryPoint))
        .exceptionHandling(Customizer.withDefaults())
        .sessionManagement(
            configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.NEVER))
        .securityContext(
            configurer -> configurer.securityContextRepository(securityContextRepository))
        .build();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint(
      ProblemDetailFactory problemDetailFactory, ObjectMapper objectMapper) {
    return new CustomAuthenticationEntryPoint(problemDetailFactory, objectMapper);
  }

  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  @Bean
  public UserDetailsService userDetailsService(UserRepository userRepository) {
    return new MongoUserDetailsService(userRepository);
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(
        String.format("%s > %s", SecurityRole.ADMIN, SecurityRole.USER));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
