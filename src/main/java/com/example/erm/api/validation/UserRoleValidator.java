package com.example.erm.api.validation;

import com.example.erm.security.SecurityRole;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserRoleValidator implements ConstraintValidator<UserRole, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return SecurityRole.getRoles().contains(value);
  }
}
