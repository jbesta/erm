package com.example.erm.api.model;

import java.util.List;

import com.example.erm.api.validation.UserRole;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
  @Schema(description = "user email", example = "alice@example.com")
  @NotNull
  @NotBlank
  @Email
  private String email;

  @Schema(description = "user password", example = "mysecretpassword")
  @NotNull
  @NotBlank
  private String password;

  @Schema(description = "user name", example = "Alice Harper")
  private String name;

  @Builder.Default
  @ArraySchema(schema = @Schema(implementation = String.class, example = "USER"))
  private @NotEmpty List<@UserRole String> roles = List.of();
}
