package com.example.erm.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DuplicateEmailException extends RuntimeException {
  private final String email;
}
