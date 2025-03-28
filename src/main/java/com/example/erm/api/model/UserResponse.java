package com.example.erm.api.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
    @Schema(example = "67e900a69cbc1b6a7d1d4b37") String id,
    @Schema(example = "john@example.com") String email,
    @Schema(example = "John") String name,
    @ArraySchema(schema = @Schema(implementation = String.class, example = "USER"))
        List<String> roles) {}
