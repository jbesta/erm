#!/usr/bin/env bash

echo ">> Building application JAR"
./gradlew clean bootJar

echo ">> Starting docker compose"
docker-compose up --build
