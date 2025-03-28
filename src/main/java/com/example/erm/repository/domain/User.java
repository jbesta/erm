package com.example.erm.repository.domain;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Document(collection = User.COLLECTION)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldNameConstants
public class User {

  public static final String COLLECTION = "user";

  @Id private String id;

  private String email;
  private String password;
  private String name;

  @Builder.Default private List<String> roles = List.of();

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
