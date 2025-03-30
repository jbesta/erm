package com.example.erm.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.erm.api.GlobalExceptionHandler;
import com.example.erm.api.mapper.UserApiMapperImpl;
import com.example.erm.api.util.ProblemDetailFactory;
import com.example.erm.command.UserCreateCommand;
import com.example.erm.command.UserUpdateCommand;
import com.example.erm.configuration.SecurityConfiguration;
import com.example.erm.repository.domain.User;
import com.example.erm.service.UserService;

@AutoConfigureMockMvc
@WebMvcTest(controllers = {UserApiController.class})
@Import({
  UserApiMapperImpl.class,
  SecurityConfiguration.class,
  GlobalExceptionHandler.class,
  ProblemDetailFactory.class
})
class UserApiControllerTest {

  private static final User TEST_USER =
      User.builder().id("123").email("john@example.com").password("secret").name("John").build();

  @Autowired private MockMvc mvc;

  @MockitoBean private UserService userService;

  @MockitoBean private UserDetailsService userDetailsService;

  @WithMockUser(roles = {"ADMIN"})
  @Test
  void testCreateUserValid() throws Exception {
    MockHttpServletResponse response =
        mvc.perform(
                post("/api/user")
                    .content(
                        """
                                                {
                                                    "email": "john@foo.com",
                                                    "password": "secret",
                                                    "name": "John",
                                                    "roles": ["USER"]
                                                }
                                                """)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn()
            .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.getContentAsString()).isNotNull();

    ArgumentCaptor<UserCreateCommand> captor = ArgumentCaptor.forClass(UserCreateCommand.class);
    verify(userService).createUser(captor.capture());
    assertThat(captor.getValue()).isNotNull();
    assertThat(captor.getValue().email()).isEqualTo("john@foo.com");
    assertThat(captor.getValue().password()).isEqualTo("secret");
    assertThat(captor.getValue().name()).isEqualTo("John");
  }

  @WithMockUser(roles = {"ADMIN"})
  @ParameterizedTest
  @MethodSource("invalidCreateRequests")
  void testCreateUserValidationErrors() throws Exception {
    mvc.perform(
            post("/api/user")
                .content(
                    """
                                                {
                                                    "email": "john@foo.com",
                                                    "password": "",
                                                    "name": "John"
                                                }
                                                """)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.detail").value("Validation failed"));
  }

  private static Stream<Arguments> invalidCreateRequests() {
    return Stream.of(
        Arguments.of(
            """
                                    {
                                        "email": "john@foo.com",
                                        "password": "",
                                        "name": "John"
                                    }
                                    """),
        Arguments.of(
            """
                                    {
                                        "email": "invalid-email",
                                        "password": "secret",
                                        "name": "John"
                                    }
                                    """),
        Arguments.of(
            """
                                    {
                                        "email": "john@foo.com",
                                        "password": "   ",
                                        "name": "John"
                                    }
                                    """),
        Arguments.of(
            """
                                            {
                                                "email": "john@foo.com",
                                                "password": "password",
                                                "name": "John",
                                                "roles": ["INVALID_ROLE"]
                                            }
                                            """));
  }

  @WithMockUser
  @Test
  void testCreateUserNotAdmin() throws Exception {
    mvc.perform(
            post("/api/user")
                .content(
                    """
                                                {
                                                    "email": "john@foo.com",
                                                    "password": "secret",
                                                    "name": "John",
                                                    "roles": ["USER"]
                                                }
                                                """)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  void testUpdateUser() throws Exception {
    String userId = "123";
    doReturn(Optional.of(TEST_USER)).when(userService).updateUser(any());
    MockHttpServletResponse response =
        mvc.perform(
                put("/api/user/" + userId)
                    .content(
                        """
                                                                    {
                                                                        "email": "alice@foo.com",
                                                                        "password": "topsecret",
                                                                        "name": "Alice",
                                                                        "roles": ["ADMIN"]
                                                                    }
                                                                    """)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn()
            .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isNotNull();

    ArgumentCaptor<UserUpdateCommand> captor = ArgumentCaptor.forClass(UserUpdateCommand.class);
    verify(userService).updateUser(captor.capture());
    assertThat(captor.getValue()).isNotNull();
    assertThat(captor.getValue().email()).isEqualTo("alice@foo.com");
    assertThat(captor.getValue().password()).isEqualTo("topsecret");
    assertThat(captor.getValue().name()).isEqualTo("Alice");
    assertThat(captor.getValue().roles()).isEqualTo(List.of("ADMIN"));
  }

  @WithMockUser
  @Test
  void testUpdateCurrentUser() throws Exception {
    doReturn(Optional.of(TEST_USER)).when(userService).findUserByEmail(any());
    doReturn(Optional.of(TEST_USER)).when(userService).updateUser(any());
    MockHttpServletResponse response =
        mvc.perform(
                put("/api/user/me")
                    .content(
                        """
                                                                                        {
                                                                                            "email": "alice@foo.com",
                                                                                            "password": "topsecret",
                                                                                            "name": "Alice",
                                                                                            "roles": ["USER"]
                                                                                        }
                                                                                        """)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn()
            .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isNotNull();

    ArgumentCaptor<UserUpdateCommand> captor = ArgumentCaptor.forClass(UserUpdateCommand.class);
    verify(userService).updateUser(captor.capture());
    assertThat(captor.getValue()).isNotNull();
    assertThat(captor.getValue().email()).isEqualTo("alice@foo.com");
    assertThat(captor.getValue().password()).isEqualTo("topsecret");
    assertThat(captor.getValue().name()).isEqualTo("Alice");
    assertThat(captor.getValue().roles()).isEqualTo(List.of("USER"));
  }

  @WithMockUser
  @Test
  void testUpdateUserNotAdmin() throws Exception {
    String userId = "123";
    mvc.perform(
            put("/api/user/" + userId)
                .content(
                    """
                                                                {
                                                                    "email": "bob@foo.com",
                                                                    "password": "secret",
                                                                    "name": "Bob",
                                                                    "roles": ["USER"]
                                                                }
                                                                """)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  void testUpdateUserNotFound() throws Exception {
    String userId = "123";
    doReturn(Optional.empty()).when(userService).findUserById(userId);

    mvc.perform(
            put("/api/user/" + userId)
                .content(
                    """
                                                                    {
                                                                        "email": "alice@foo.com",
                                                                        "password": "secret",
                                                                        "name": "Alice",
                                                                        "roles": ["USER"]
                                                                    }
                                                                    """)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
  }

  @WithMockUser
  @Test
  void findCurrentUser() throws Exception {
    doReturn(Optional.of(TEST_USER)).when(userService).findUserByEmail(any());

    mvc.perform(get("/api/user/me"))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.name").value(TEST_USER.getName()))
        .andExpect(jsonPath("$.email").value(TEST_USER.getEmail()));
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  void findUserById() throws Exception {
    String userId = "123";
    doReturn(Optional.of(TEST_USER)).when(userService).findUserById(userId);

    mvc.perform(get("/api/user/" + userId))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.name").value(TEST_USER.getName()))
        .andExpect(jsonPath("$.email").value(TEST_USER.getEmail()));
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  void findUserByIdNotFound() throws Exception {
    String userId = "123";
    doReturn(Optional.empty()).when(userService).findUserById(userId);

    mvc.perform(get("/api/user/" + userId)).andExpect(status().is(HttpStatus.NOT_FOUND.value()));
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  void deleteUserById() throws Exception {
    String userId = "123";
    doReturn(true).when(userService).deleteUserById(userId);

    mvc.perform(delete("/api/user/" + userId))
        .andExpect(status().is(HttpStatus.NO_CONTENT.value()));

    verify(userService).deleteUserById(userId);
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  void deleteUserByIdNotFound() throws Exception {
    String userId = "123";
    doReturn(false).when(userService).deleteUserById(userId);

    mvc.perform(delete("/api/user/" + userId)).andExpect(status().is(HttpStatus.NOT_FOUND.value()));
  }
}
