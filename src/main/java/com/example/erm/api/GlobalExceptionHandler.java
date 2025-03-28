package com.example.erm.api;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.erm.api.util.ProblemDetailFactory;
import com.example.erm.exception.DuplicateEmailException;
import com.example.erm.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  private final ProblemDetailFactory problemDetailFactory;

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception e) {
    log.error("Internal server error: {}", e.getMessage(), e);
    return problemDetailFactory.internalServerError();
  }

  @ExceptionHandler(AuthenticationException.class)
  public ProblemDetail handleAuthenticationException(AuthenticationException e) {
    return problemDetailFactory.unauthorized();
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail accessDenied() {
    return problemDetailFactory.accessDenied();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(MethodArgumentNotValidException e) {
    return problemDetailFactory.requestValidationError(e.getFieldErrors());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ProblemDetail handleUserNotFoundException(UserNotFoundException e) {
    return problemDetailFactory.userNotFound(e.getId());
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ProblemDetail handleDuplicateEmailException(DuplicateEmailException e) {
    return problemDetailFactory.emailExists(e.getEmail());
  }
}
