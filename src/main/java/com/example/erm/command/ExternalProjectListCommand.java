package com.example.erm.command;

import org.springframework.data.domain.Pageable;

public record ExternalProjectListCommand(String userId, Pageable page) {}
