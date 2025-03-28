package com.example.erm.command;

import java.util.List;

public record UserCreateCommand(String email, String password, String name, List<String> roles) {}
