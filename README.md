# Howto

This project is used as an example of how to build and structure a Spring Boot application with REST endpoints and Spring Security

## Structure

_Note! There are always exceptions to any structure, but this should give a good baseline for how to structure a
project_

### Layers

This project consists of four main layers.

Each layer has its own responsibility and a class from a lower layer **cannot**
interact directly with other classes of the same layer or any layer above. This is to keep the overall structure clear,
consistent and to avoid circular dependencies.

1. Controller
2. Aggregator
3. Service
4. Repository

### 1. Controller

Classes annotated with `@RestController` and uses `DTO` classes. `internal model` classes are only used when
passing data down to other layers.

This should be a very thin layer with very few lines of code per method. Basically we expose endpoints and validate
requests.

From here we can interact with:

* Aggregator
* Service
* Repository

### 2. Aggregator

Classes annotated with `@Component` and uses both `DTO` and `internal model` classes.

This layer is not always necessary, but it is useful if:

* we want to combine (aggregate) data from more than one service
* we want to combine (aggregate) data from a service and a repository, but we do not want to add that logic inside the
  service
* we want to create a DTO from more than one service/repository

From here we can interact with:

* Service
* Repository

### 3. Service

Classes annotated with `@Service` and uses `internal model` classes.

This is where the business logic is handled. This is where we code what we want the application to do.

From here we can interact with:

* Repository

### 4. Repository

Classes annotated with `@Repository` and uses `internal model` classes.

This is how we interact with a database. Only what is necessary to interact with a database should exist here, meaning
no business logic

## Running locally

This project requires that you have a postgresql database up and running.

### Database setup

```
docker run --name howto-db -e POSTGRES_DB=howto-db -e POSTGRES_USER=howto -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:14.5
```

or run `docker-compose up -d`

### Test users

```
username: admin
password: superSecret1!
```

```
username: user
password: superSecret2!
```

### Swagger

http://localhost:8080/swagger-ui/index.html
