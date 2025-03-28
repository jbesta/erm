package com.example.erm.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.erm.api.GlobalExceptionHandler;
import com.example.erm.api.mapper.ExternalProjectApiMapperImpl;
import com.example.erm.api.mapper.UserApiMapperImpl;
import com.example.erm.api.util.ProblemDetailFactory;
import com.example.erm.command.ExternalProjectCreateCommand;
import com.example.erm.configuration.SecurityConfiguration;
import com.example.erm.repository.domain.ExternalProject;
import com.example.erm.repository.domain.User;
import com.example.erm.service.ExternalProjectService;
import com.example.erm.service.UserService;

@AutoConfigureMockMvc
@WebMvcTest(controllers = {ExternalProjectApiController.class})
@Import({
  UserApiMapperImpl.class,
  ExternalProjectApiMapperImpl.class,
  SecurityConfiguration.class,
  GlobalExceptionHandler.class
})
class ExternalProjectApiControllerTest {

  private static final User TEST_USER =
      User.builder().id("123").email("john@example.com").password("secret").name("John").build();

  @Autowired private MockMvc mvc;

  @MockitoBean private UserService userService;

  @MockitoBean private UserDetailsService userDetailsService;

  @MockitoBean private ExternalProjectService externalProjectService;

  @MockitoBean private ProblemDetailFactory problemDetailFactory;

  @WithMockUser(roles = {"ADMIN"})
  @Test
  void testAddExternalProjectToUser() throws Exception {
    String userId = UUID.randomUUID().toString();
    doReturn(Optional.of(TEST_USER)).when(userService).findUserById(userId);

    mvc.perform(
            post("/api/user/" + userId + "/external-project")
                .content(
                    """
                                        {
                                            "name": "my-project"
                                        }
                                        """)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();

    ArgumentCaptor<ExternalProjectCreateCommand> captor =
        ArgumentCaptor.forClass(ExternalProjectCreateCommand.class);
    verify(externalProjectService).createExternalProject(captor.capture());
    assertThat(captor.getValue()).isNotNull();
    assertThat(captor.getValue().userId()).isEqualTo(userId);
    assertThat(captor.getValue().name()).isEqualTo("my-project");
  }

  @WithMockUser(username = "me@myself.com")
  @Test
  void testAddExternalProjectToCurrentUser() throws Exception {
    doReturn(Optional.of(TEST_USER)).when(userService).findUserByEmail("me@myself.com");

    mvc.perform(
            post("/api/user/me/external-project")
                .content(
                    """
                                        {
                                            "name": "my-project"
                                        }
                                        """)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();

    ArgumentCaptor<ExternalProjectCreateCommand> captor =
        ArgumentCaptor.forClass(ExternalProjectCreateCommand.class);
    verify(externalProjectService).createExternalProject(captor.capture());
    assertThat(captor.getValue()).isNotNull();
    assertThat(captor.getValue().name()).isEqualTo("my-project");
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  void testListExternalProjectsOfUser() throws Exception {
    String userId = UUID.randomUUID().toString();
    Instant now = Instant.now();
    doReturn(Optional.of(TEST_USER)).when(userService).findUserById(userId);
    doReturn(
            new PageImpl<>(
                List.of(
                    new ExternalProject(
                        UUID.randomUUID().toString(), userId, "my-project", now, now),
                    new ExternalProject(
                        UUID.randomUUID().toString(),
                        userId,
                        "another-project",
                        now.minusSeconds(1),
                        now.minusSeconds(1)))))
        .when(externalProjectService)
        .listExternalProjects(any());

    mvc.perform(get("/api/user/" + userId + "/external-project"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items.length()").value(2))
        .andExpect(jsonPath("$.items[0].name").value("my-project"))
        .andExpect(jsonPath("$.items[1].name").value("another-project"))
        .andReturn()
        .getResponse();
  }

  @WithMockUser("me@myself.com")
  @Test
  void testListExternalProjectsOfCurrentUser() throws Exception {
    String userId = UUID.randomUUID().toString();
    Instant now = Instant.now();
    doReturn(Optional.of(TEST_USER)).when(userService).findUserByEmail("me@myself.com");
    doReturn(
            new PageImpl<>(
                List.of(
                    new ExternalProject(
                        UUID.randomUUID().toString(), userId, "first-project", now, now),
                    new ExternalProject(
                        UUID.randomUUID().toString(),
                        userId,
                        "second-project",
                        now.minusSeconds(1),
                        now.minusSeconds(1)),
                    new ExternalProject(
                        UUID.randomUUID().toString(),
                        userId,
                        "third-project",
                        now.minusSeconds(10),
                        now.minusSeconds(10)))))
        .when(externalProjectService)
        .listExternalProjects(any());

    mvc.perform(get("/api/user/me/external-project"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items.length()").value(3))
        .andExpect(jsonPath("$.items[0].name").value("first-project"))
        .andExpect(jsonPath("$.items[1].name").value("second-project"))
        .andExpect(jsonPath("$.items[2].name").value("third-project"))
        .andExpect(jsonPath("$.page").exists())
        .andExpect(jsonPath("$.page.totalElements").value(3))
        .andReturn()
        .getResponse();
  }
}
