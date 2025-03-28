package com.example.erm.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.erm.command.ExternalProjectCreateCommand;
import com.example.erm.command.ExternalProjectListCommand;
import com.example.erm.repository.ExternalProjectRepository;
import com.example.erm.repository.domain.ExternalProject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalProjectService {
  private final ExternalProjectRepository externalProjectRepository;

  public ExternalProject createExternalProject(ExternalProjectCreateCommand command) {
    log.debug("Creating external project. {}", command);
    ExternalProject externalProject =
        externalProjectRepository.insert(
            ExternalProject.builder().userId(command.userId()).name(command.name()).build());
    log.debug("Created external project. {}", externalProject);

    return externalProject;
  }

  public Page<ExternalProject> listExternalProjects(ExternalProjectListCommand command) {
    return externalProjectRepository.list(command);
  }
}
