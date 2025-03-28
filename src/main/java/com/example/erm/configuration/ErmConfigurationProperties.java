package com.example.erm.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "erm")
public record ErmConfigurationProperties(Bootstrap bootstrap) {

  public record Bootstrap(User user) {

    public record User(String email, String password) {}
  }
}
