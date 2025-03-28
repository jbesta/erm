package com.example.erm.api.mapper;

import org.mapstruct.Mapper;

import com.example.erm.api.model.ExternalProjectCreateRequest;
import com.example.erm.api.model.ExternalProjectResponse;
import com.example.erm.command.ExternalProjectCreateCommand;
import com.example.erm.mapper.CommonMapperConfig;
import com.example.erm.repository.domain.ExternalProject;

@Mapper(config = CommonMapperConfig.class)
public abstract class ExternalProjectApiMapper {

  public abstract ExternalProjectCreateCommand toCommand(
      String userId, ExternalProjectCreateRequest request);

  public abstract ExternalProjectResponse toResponse(ExternalProject externalProject);
}
