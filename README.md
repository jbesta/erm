# ERM

## Requirements

it is required to have `docker` runtime installed

## Running application

to build and run dockerized application use 

```
./run.sh
```

application can be then accessed at

```
http://localhost:8080
```

## Authentication

http basic for simplicity

initial user to access api

```
username: admin@example.com
password: adminsecret
```

## Running tests

```
./test.sh
```

## OpenAPI spec

```
http://localhost:8080/api-docs
```

## Swagger

```
http://localhost:8080/swagger-ui/index.html
```

## Metrics

```
http://localhost:8080/actuator/prometheus
```