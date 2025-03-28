package com.example.erm.repository.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Document(collection = ExternalProject.COLLECTION)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldNameConstants
public class ExternalProject {

  public static final String COLLECTION = "external_project";

  @Id private String id;

  private String userId;
  private String name;

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
