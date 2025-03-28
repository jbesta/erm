package com.example.erm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;

import com.example.erm.command.ExternalProjectCreateCommand;
import com.example.erm.command.ExternalProjectListCommand;
import com.example.erm.repository.ExternalProjectRepository;
import com.example.erm.repository.domain.ExternalProject;

class ExternalProjectServiceTest {

  private ExternalProjectRepository externalProjectRepository;
  private ExternalProjectService externalProjectService;

  @BeforeEach
  void setUp() {
    externalProjectRepository = mock(ExternalProjectRepository.class);
    externalProjectService = new ExternalProjectService(externalProjectRepository);
  }

  @Test
  void testCreateExternalProject() {
    ExternalProjectCreateCommand createCommand =
        new ExternalProjectCreateCommand("abcde", "super-project");

    externalProjectService.createExternalProject(createCommand);

    ArgumentCaptor<ExternalProject> captor = ArgumentCaptor.forClass(ExternalProject.class);
    verify(externalProjectRepository).insert(captor.capture());
    assertThat(captor.getValue()).isNotNull();
    assertThat(captor.getValue().getName()).isEqualTo(createCommand.name());
    assertThat(captor.getValue().getUserId()).isEqualTo(createCommand.userId());
  }

  @Test
  void listExternalProjects() {
    ExternalProjectListCommand listCommand =
        new ExternalProjectListCommand("user-id", Pageable.ofSize(10));

    externalProjectService.listExternalProjects(listCommand);

    verify(externalProjectRepository).list(listCommand);
  }
}
