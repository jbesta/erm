package com.example.erm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.example.erm.command.ExternalProjectListCommand;
import com.example.erm.repository.domain.ExternalProject;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExternalProjectRepository {
  private final MongoTemplate mongoTemplate;

  public ExternalProject insert(ExternalProject externalProject) {
    return mongoTemplate.insert(externalProject);
  }

  public Page<ExternalProject> list(ExternalProjectListCommand command) {
    Query query = Query.query(Criteria.where(ExternalProject.Fields.userId).is(command.userId()));

    return PageableExecutionUtils.getPage(
        mongoTemplate.find(query.with(command.page()), ExternalProject.class),
        command.page(),
        () -> mongoTemplate.count(query, ExternalProject.class));
  }
}
