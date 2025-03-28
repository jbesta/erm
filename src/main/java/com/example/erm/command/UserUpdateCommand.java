package com.example.erm.command;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserUpdateCommand(
    String id, String email, String password, String name, List<String> roles) {}
