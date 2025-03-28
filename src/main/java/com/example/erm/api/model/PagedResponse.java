package com.example.erm.api.model;

import java.util.List;

import lombok.Builder;

public record PagedResponse<T>(List<T> items, Page page) {

  public static <T> PagedResponse<T> of(org.springframework.data.domain.Page<T> page) {
    return new PagedResponse<>(
        page.getContent(),
        new Page(
            page.getTotalPages(),
            page.getTotalElements(),
            page.getSize(),
            page.getNumber(),
            page.isFirst(),
            page.isLast()));
  }

  @Builder(toBuilder = true)
  public record Page(
      Integer totalPages,
      Long totalElements,
      Integer size,
      Integer number,
      Boolean first,
      Boolean last) {}
}
