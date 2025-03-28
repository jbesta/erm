package com.example.erm.api.mapper;

import org.mapstruct.Mapper;

import com.example.erm.api.model.UserCreateRequest;
import com.example.erm.api.model.UserResponse;
import com.example.erm.api.model.UserUpdateRequest;
import com.example.erm.command.UserCreateCommand;
import com.example.erm.command.UserUpdateCommand;
import com.example.erm.mapper.CommonMapperConfig;
import com.example.erm.repository.domain.User;
import com.example.erm.security.SecurityRole;

@Mapper(config = CommonMapperConfig.class, imports = SecurityRole.class)
public abstract class UserApiMapper {

  public abstract UserCreateCommand toCommand(UserCreateRequest request);

  public abstract UserUpdateCommand toCommand(String id, UserUpdateRequest request);

  public abstract UserResponse toResponse(User user);
}
