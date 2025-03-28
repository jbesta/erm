package com.example.erm.api.controller;

import java.security.Principal;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.erm.api.mapper.ExternalProjectApiMapper;
import com.example.erm.api.model.ExternalProjectCreateRequest;
import com.example.erm.api.model.ExternalProjectResponse;
import com.example.erm.api.model.PagedResponse;
import com.example.erm.command.ExternalProjectCreateCommand;
import com.example.erm.command.ExternalProjectListCommand;
import com.example.erm.exception.UserNotFoundException;
import com.example.erm.repository.domain.ExternalProject;
import com.example.erm.repository.domain.User;
import com.example.erm.service.ExternalProjectService;
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
@Tag(name = "ExternalProject", description = "provides operations on external projects of user")
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
public class ExternalProjectApiController {

  private final UserService userService;
  private final ExternalProjectService externalProjectService;
  private final ExternalProjectApiMapper externalProjectApiMapper;

  @PostMapping(
      value = "{id}/external-project",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Operation(summary = "adds external project to user identified by id")
  @ApiResponse(
      responseCode = "201",
      description = "external project successfully created",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExternalProjectResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Request constraint violation",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "404",
      description = "user with provided id not found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public ExternalProjectResponse createExternalProjectOfUser(
      @PathVariable("id") String userId, @Valid @RequestBody ExternalProjectCreateRequest request) {
    checkUserExists(userId);

    return doCreateExternalProject(userId, request);
  }

  @PostMapping(
      value = "me/external-project",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "adds external project to currently authenticated user")
  @ApiResponse(
      responseCode = "201",
      description = "external project successfully created",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExternalProjectResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Request constraint violation",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public ExternalProjectResponse createExternalProjectForCurrentUser(
      @Valid @RequestBody ExternalProjectCreateRequest request, Principal principal) {
    User currentUser = findCurrentUser(principal);

    return doCreateExternalProject(currentUser.getId(), request);
  }

  private ExternalProjectResponse doCreateExternalProject(
      String userId, ExternalProjectCreateRequest request) {
    ExternalProjectCreateCommand externalProjectCreateCommand =
        externalProjectApiMapper.toCommand(userId, request);
    ExternalProject externalProject =
        externalProjectService.createExternalProject(externalProjectCreateCommand);

    return externalProjectApiMapper.toResponse(externalProject);
  }

  @GetMapping(value = "{id}/external-project", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Operation(summary = "lists external projects of user identified by id")
  @ApiResponse(
      responseCode = "404",
      description = "user with provided id not found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public PagedResponse<ExternalProjectResponse> listProjects(
      @PathVariable("id") String userId, @ParameterObject Pageable page) {
    checkUserExists(userId);

    return doListProjects(userId, page);
  }

  @GetMapping(value = "me/external-project", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "lists external projects of currently authenticated user")
  public PagedResponse<ExternalProjectResponse> listProjectsOfCurrentUser(
      @ParameterObject Pageable page, Principal principal) {
    User currentUser = findCurrentUser(principal);

    return doListProjects(currentUser.getId(), page);
  }

  private PagedResponse<ExternalProjectResponse> doListProjects(String userId, Pageable page) {
    Page<ExternalProjectResponse> responsePage =
        externalProjectService
            .listExternalProjects(new ExternalProjectListCommand(userId, page))
            .map(externalProjectApiMapper::toResponse);

    return PagedResponse.of(responsePage);
  }

  private User findCurrentUser(Principal principal) {
    return userService
        .findUserByEmail(principal.getName())
        .orElseThrow(() -> new UserNotFoundException(principal.getName()));
  }

  private void checkUserExists(String userId) {
    userService.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId));
  }
}
