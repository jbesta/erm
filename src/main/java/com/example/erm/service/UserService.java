package com.example.erm.service;

import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.erm.command.UserCreateCommand;
import com.example.erm.command.UserUpdateCommand;
import com.example.erm.exception.DuplicateEmailException;
import com.example.erm.repository.UserRepository;
import com.example.erm.repository.domain.User;
import com.mongodb.client.result.DeleteResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User createUser(UserCreateCommand command) {
    try {
      log.debug("Creating user {}", command);
      User user =
          userRepository.insert(
              User.builder()
                  .email(command.email())
                  .name(command.name())
                  .password(passwordEncoder.encode(command.password()))
                  .roles(command.roles())
                  .build());
      log.debug("Created user {}", user);

      return user;
    } catch (DuplicateKeyException e) {
      // simplified assumption; for now only email has unique index
      log.debug("Unable to create user. user with email {} already exists", command.email());
      throw new DuplicateEmailException(command.email());
    }
  }

  public Optional<User> updateUser(UserUpdateCommand command) {
    try {
      log.debug("Updating user {}", command);
      Optional<User> updatedUser =
          Optional.ofNullable(
              userRepository.update(
                  command.toBuilder()
                      .password(passwordEncoder.encode(command.password()))
                      .build()));
      updatedUser.ifPresentOrElse(
          user -> log.debug("Updated user {}", user),
          () -> {
            log.debug("User to update not found. {}", command);
          });
      return updatedUser;
    } catch (DuplicateKeyException e) {
      // simplified assumption; for now only email has unique index
      log.debug("Unable to update user. user with email {} already exists", command.email());
      throw new DuplicateEmailException(command.email());
    }
  }

  public Optional<User> findUserById(String id) {
    return userRepository.findById(id);
  }

  public Optional<User> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public boolean deleteUserById(String id) {
    log.debug("Deleting user with id {}", id);
    DeleteResult deleteResult = userRepository.deleteById(id);

    boolean deleted = deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() > 0;
    log.debug("User with id {} has {} been deleted", id, deleted ? "" : "not");

    return deleted;
  }
}
