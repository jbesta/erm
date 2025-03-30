package com.example.erm.repository.migration;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.erm.configuration.ErmConfigurationProperties;
import com.example.erm.repository.domain.ExternalProject;
import com.example.erm.repository.domain.User;
import com.example.erm.security.SecurityRole;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;

@ChangeUnit(id = "db-init", order = "000")
@RequiredArgsConstructor
public class DbInit {

  private final ErmConfigurationProperties ermConfigurationProperties;
  private final PasswordEncoder passwordEncoder;

  private static final String INITIAL_USER_EMAIL = "admin@example.com";

  @BeforeExecution
  public void beforeExecution(MongoTemplate mongoTemplate) {
    mongoTemplate.createCollection(User.class);
    mongoTemplate.createCollection(ExternalProject.class);

    mongoTemplate
        .indexOps(User.class)
        .ensureIndex(
            new Index().named("email_1").on(User.Fields.email, Sort.Direction.ASC).unique());

    mongoTemplate
        .indexOps(ExternalProject.class)
        .ensureIndex(
            new Index()
                .named("userId_1_createdAt_1")
                .on(ExternalProject.Fields.userId, Sort.Direction.ASC)
                .on(ExternalProject.Fields.createdAt, Sort.Direction.ASC));

    mongoTemplate
        .indexOps(ExternalProject.class)
        .ensureIndex(
            new Index()
                .named("userId_1_name_1")
                .on(ExternalProject.Fields.userId, Sort.Direction.ASC)
                .on(ExternalProject.Fields.name, Sort.Direction.ASC));
  }

  @RollbackBeforeExecution
  public void rollbackBeforeExecution(MongoTemplate mongoTemplate) {
    mongoTemplate.dropCollection(User.class);
    mongoTemplate.dropCollection(ExternalProject.class);
  }

  @Execution
  public void execution(MongoTemplate mongoTemplate) {
    User initialUser =
        User.builder()
            .email(ermConfigurationProperties.bootstrap().user().email())
            .password(
                passwordEncoder.encode(ermConfigurationProperties.bootstrap().user().password()))
            .name("admin")
            .roles(List.of(SecurityRole.ADMIN))
            .build();
    mongoTemplate.insert(initialUser);
  }

  @RollbackExecution
  public void rollbackExecution(MongoTemplate mongoTemplate) {
    mongoTemplate.remove(
        Query.query(
            Criteria.where(User.Fields.email)
                .is(ermConfigurationProperties.bootstrap().user().email())),
        User.class);
  }
}
