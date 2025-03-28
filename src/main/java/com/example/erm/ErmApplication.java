package com.example.erm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.erm.configuration.ErmConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ErmConfigurationProperties.class)
public class ErmApplication {

  public static void main(String[] args) {
    SpringApplication.run(ErmApplication.class, args);
  }
}
