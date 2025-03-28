package com.example.erm.api.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class ProblemDetailFactory {

  public ProblemDetail userNotFound(String identifier) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "User " + identifier + " not found");
    problemDetail.setTitle("User not found");

    return problemDetail;
  }

  public ProblemDetail emailExists(String email) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, "Provided email " + email + " is already registered");
    problemDetail.setTitle("Email not allowed");

    return problemDetail;
  }

  public ProblemDetail requestValidationError(List<FieldError> fieldErrors) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
    Map<String, String> errors =
        fieldErrors.stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
    problemDetail.setProperty("errors", errors);

    return problemDetail;
  }

  public ProblemDetail internalServerError() {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    problemDetail.setTitle("Internal Server Error");

    return problemDetail;
  }

  public ProblemDetail accessDenied() {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
    problemDetail.setTitle("Access denied");

    return problemDetail;
  }

  public ProblemDetail unauthorized() {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Unauthorized");
    problemDetail.setTitle("Unauthorized");

    return problemDetail;
  }
}
