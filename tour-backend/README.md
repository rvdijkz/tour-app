# tour-backend

Backend scaffold for the tour application.

## Requirements
- Java 21+
- Maven 3.9+

## Start locally

```powershell
mvn spring-boot:run
```

## Run tests

```powershell
mvn test
```

## Profiles
- `dev` uses H2 in-memory for local development.
- `prod` is prepared for PostgreSQL and JWT-based resource-server security.


