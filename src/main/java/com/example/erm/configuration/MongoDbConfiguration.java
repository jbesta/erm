package com.example.erm.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import io.mongock.runner.springboot.EnableMongock;

@Configuration
@EnableMongoAuditing
@EnableMongock
public class MongoDbConfiguration {}
