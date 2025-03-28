package com.example.erm.repository;

import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.example.erm.command.UserUpdateCommand;
import com.example.erm.repository.domain.User;
import com.mongodb.client.result.DeleteResult;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {
  private final MongoTemplate mongoTemplate;

  public User insert(User user) {
    return mongoTemplate.insert(user);
  }

  public Optional<User> findById(String id) {
    return Optional.ofNullable(mongoTemplate.findById(id, User.class));
  }

  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable(
        mongoTemplate.findOne(
            Query.query(Criteria.where(User.Fields.email).is(email)), User.class));
  }

  public DeleteResult deleteById(String id) {
    return mongoTemplate.remove(Query.query(Criteria.where(User.Fields.id).is(id)), User.class);
  }

  public User update(UserUpdateCommand command) {
    Query query = Query.query(Criteria.where(User.Fields.id).is(command.id()));
    Update update =
        new Update()
            .set(User.Fields.email, command.email())
            .set(User.Fields.password, command.password())
            .set(User.Fields.name, command.name())
            .set(User.Fields.roles, command.roles());

    return mongoTemplate.findAndModify(query, update, User.class);
  }
}
