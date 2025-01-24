# Howto

This project is used as an example of how to build and structure a Spring Boot application with REST endpoints and
Spring Security

## Structure

_Note! There are always exceptions to any structure, but this should give a good baseline for how to structure a
project_

### Layers

This project consists of four main layers and one additional layer for communicating with the rest of the world.

Each layer has its own responsibility and a class from a lower layer **cannot**
interact directly with other classes of the same layer or any layer above. This is to keep the overall structure clear,
consistent and to avoid circular dependencies.

1. Controller
2. Aggregator
3. Service
4. Repository
5. Client

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

### 5. Client

Classes annotated with `@Component` and uses `internal model` classes as well as their own specific classes necessary
for communication with the outside world.

Clients are used to communicate with the outside world, usually via REST calls. Objects from `internal model` classes
are passed down to the client,
the client then creates its own objects to communicate with some other API, the response is then translated back
to `internal model` and returned.


## Running locally

This project requires that you have a postgresql database up and running.

### Database setup

There is a docker compose file that can be used to start a postgresql database: 

`docker compose up -d`

### Test users

Login by posting the following to http://localhost:8080/api/auth/authenticate
```json
{
  "username": "string",
  "password": "string"
}
```

The response is a message with the token. This should be used as part of the `HTTP Authorization header` using the `Bearer authentication scheme` 
for requests that requires authentication.

If you're using the swagger gui you can set the authorization header by clicking the `Authorize` button at the top of the page

```
username: admin
password: superSecret1!
```

```
username: moderator
password: superSecret2!
```

```
username: user
password: superSecret3!
```

### Swagger

http://localhost:8080/swagger-ui/index.html

### Actuator

* http://localhost:8080/actuator/health
* http://localhost:8080/actuator/info

### Flyway baseline

To not have to run every single flyway migration each time the database is created we can create a baseline that will be used in tests and when
running locally with docker compose.

To create a new baseline:

1. Temporarily remove any flyway script that would execute for the local profile by removing all files
   in [src/main/resources/db/migration/local/](src/main/resources/db/migration/local/). We don't want these in our baseline
2. Start an empty mysql database using the docker compose file by first executing `docker compose down` to make sure any existing database is removed
   and then `docker compose up -d` to start a fresh database
3. Start the java backend with the local profile active to populate the database
4. Run `PGPASSWORD=password docker exec howto-postgres pg_dump -U howto -d howto-db > howto_db_baseline.sql` to create a new baseline
5. Replace the existing [infoval_baseline.sql](src/test/resources/db/baseline/infoval_baseline.sql) with this new baseline