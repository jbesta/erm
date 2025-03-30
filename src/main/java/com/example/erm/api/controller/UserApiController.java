package com.example.erm.api.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.erm.api.mapper.UserApiMapper;
import com.example.erm.api.model.UserCreateRequest;
import com.example.erm.api.model.UserResponse;
import com.example.erm.api.model.UserUpdateRequest;
import com.example.erm.command.UserCreateCommand;
import com.example.erm.command.UserUpdateCommand;
import com.example.erm.exception.UserNotFoundException;
import com.example.erm.repository.domain.User;
import com.example.erm.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "User", description = "provides operations on user")
@ApiResponse(
    responseCode = "401",
    description = "Unauthorized",
    content =
        @Content(
            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
            schema = @Schema(implementation = ProblemDetail.class)))
@ApiResponse(
    responseCode = "403",
    description = "Forbidden",
    content =
        @Content(
            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
            schema = @Schema(implementation = ProblemDetail.class)))
public class UserApiController {

  private final UserService userService;
  private final UserApiMapper userApiMapper;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Operation(summary = "create new user")
  @ApiResponse(
      responseCode = "201",
      description = "user successfully created",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Request constraint violation",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
    UserCreateCommand userCreateCommand = userApiMapper.toCommand(request);
    User createdUser = userService.createUser(userCreateCommand);

    return userApiMapper.toResponse(createdUser);
  }

  @GetMapping(path = "me", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "get information about currently authenticated user")
  @ApiResponse(
      responseCode = "200",
      description = "details about current user",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Request constraint violation",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public UserResponse findCurrentUser(Principal principal) {
    User user = userService.findUserByEmail(principal.getName()).orElse(null);

    return userApiMapper.toResponse(user);
  }

  @PutMapping(
      value = "{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Operation(summary = "updates user identified by id")
  @ApiResponse(
      responseCode = "200",
      description = "user successfully updated",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Request constraint violation",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public UserResponse updateUser(
      @PathVariable String id, @Valid @RequestBody UserUpdateRequest request) {
    UserUpdateCommand userUpdateCommand = userApiMapper.toCommand(id, request);
    User updatedUser =
        userService.updateUser(userUpdateCommand).orElseThrow(() -> new UserNotFoundException(id));

    return userApiMapper.toResponse(updatedUser);
  }

  @PutMapping(
      value = "me",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "updates current user")
  @ApiResponse(
      responseCode = "200",
      description = "user successfully updated",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Request constraint violation",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public UserResponse updateCurrentUser(
      @Valid @RequestBody UserUpdateRequest request, Principal principal) {
    User currentUser =
        userService
            .findUserByEmail(principal.getName())
            .orElseThrow(() -> new UserNotFoundException(principal.getName()));
    UserUpdateCommand userUpdateCommand = userApiMapper.toCommand(currentUser.getId(), request);
    User updatedUser =
        userService
            .updateUser(userUpdateCommand)
            .orElseThrow(() -> new UserNotFoundException(currentUser.getId()));

    return userApiMapper.toResponse(updatedUser);
  }

  @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Operation(summary = "get information about user identified by id")
  @ApiResponse(responseCode = "200", description = "details about user identified by id")
  @ApiResponse(
      responseCode = "404",
      description = "user with provided id not found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public UserResponse findUserById(@PathVariable String id) {
    User user = userService.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));

    return userApiMapper.toResponse(user);
  }

  @DeleteMapping(path = "{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Operation(summary = "deletes user by id")
  @ApiResponse(responseCode = "204", description = "user deleted")
  @ApiResponse(
      responseCode = "404",
      description = "user with provided id not found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public void deleteUserById(@PathVariable String id) {
    boolean deleted = userService.deleteUserById(id);

    if (!deleted) {
      throw new UserNotFoundException(id);
    }
  }
}
