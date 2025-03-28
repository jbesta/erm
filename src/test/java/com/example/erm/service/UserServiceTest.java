package com.example.erm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.erm.command.UserCreateCommand;
import com.example.erm.command.UserUpdateCommand;
import com.example.erm.exception.DuplicateEmailException;
import com.example.erm.repository.UserRepository;
import com.example.erm.repository.domain.User;
import com.mongodb.client.result.DeleteResult;

class UserServiceTest {

  private PasswordEncoder passwordEncoder;
  private UserRepository userRepository;
  private UserService userService;

  @BeforeEach
  void setUp() {
    passwordEncoder = mock(PasswordEncoder.class);
    userRepository = mock(UserRepository.class);
    userService = new UserService(userRepository, passwordEncoder);
  }

  @Test
  void testCreateUser() {
    UserCreateCommand command =
        new UserCreateCommand("john@foo.com", "password!", "John Bar", List.of("USER"));
    String encodedPassword = "encodedPassword!";
    doReturn(encodedPassword).when(passwordEncoder).encode(any());

    userService.createUser(command);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).insert(captor.capture());
    assertThat(captor.getValue()).isNotNull();
    assertThat(captor.getValue().getEmail()).isEqualTo(command.email());
    assertThat(captor.getValue().getPassword()).isEqualTo(encodedPassword);
    assertThat(captor.getValue().getName()).isEqualTo(command.name());
  }

  @Test
  void testCreateUserDuplicateEmail() {
    doThrow(DuplicateKeyException.class).when(userRepository).insert(any());

    Executable executable =
        () ->
            userService.createUser(
                new UserCreateCommand("john@foo.com", "password!", "John Bar", List.of("USER")));
    assertThrows(DuplicateEmailException.class, executable);
  }

  @Test
  void testUpdateUser() {
    UserUpdateCommand command =
        new UserUpdateCommand("12345", "john@foo.com", "password!", "John Bar", List.of("USER"));
    String encodedPassword = "encodedPassword!";
    doReturn(encodedPassword).when(passwordEncoder).encode(any());

    userService.updateUser(command);

    ArgumentCaptor<UserUpdateCommand> captor = ArgumentCaptor.forClass(UserUpdateCommand.class);
    verify(userRepository).update(captor.capture());
    assertThat(captor.getValue()).isNotNull();
    assertThat(captor.getValue().email()).isEqualTo(command.email());
    assertThat(captor.getValue().password()).isEqualTo(encodedPassword);
    assertThat(captor.getValue().name()).isEqualTo(command.name());
    assertThat(captor.getValue().roles()).isEqualTo(command.roles());
  }

  @Test
  void testUpdateUserDuplicateEmail() {
    doThrow(DuplicateKeyException.class).when(userRepository).update(any());

    Executable executable =
        () ->
            userService.updateUser(
                new UserUpdateCommand(
                    "12345", "john@foo.com", "password!", "John Bar", List.of("USER")));
    assertThrows(DuplicateEmailException.class, executable);
  }

  @Test
  void findUserById() {
    String userId = "userId";
    Optional<User> user = userService.findUserById(userId);

    assertThat(user).isNotNull();
    verify(userRepository).findById(userId);
  }

  @Test
  void findUserByEmail() {
    String userId = "userId";
    userService.findUserByEmail(userId);

    verify(userRepository).findByEmail(userId);
  }

  @Test
  void deleteUserById() {
    String userId = "userId";
    doReturn(DeleteResult.acknowledged(1)).when(userRepository).deleteById(userId);

    userService.deleteUserById(userId);

    verify(userRepository).deleteById(userId);
  }

  @Test
  void deleteUserByIdNotFound() {
    String userId = "userId";
    doReturn(DeleteResult.acknowledged(0)).when(userRepository).deleteById(userId);

    boolean deleted = userService.deleteUserById(userId);

    assertThat(deleted).isFalse();
    verify(userRepository).deleteById(userId);
  }
}
