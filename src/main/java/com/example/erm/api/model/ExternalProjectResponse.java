package com.example.erm.api.model;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

public record ExternalProjectResponse(
    @Schema(example = "67e900a69cbc1b6a7d1d4b37") String id,
    @Schema(example = "my-project") String name,
    @Schema(example = "2025-04-01T15:03:19.379Z") Instant createdAt) {}
