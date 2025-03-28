package com.example.erm.security;

import java.util.Set;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityRole {
  public static final String ADMIN = "ADMIN";
  public static final String USER = "USER";

  public static Set<String> getRoles() {
    return Set.of(ADMIN, USER);
  }
}
