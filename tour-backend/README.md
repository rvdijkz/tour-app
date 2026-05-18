# tour-backend

Initial Spring Boot backend boilerplate for the Tour app, aligned with the project instructions:

- Java 21, Maven, Spring Boot 3
- OAuth2 Resource Server + JWT validation
- `SecurityFilterChain` based security (including a `no-security` profile)
- Hexagonal package skeleton (`domain`, `application`, `controller`, `config`)
- Liquibase migration setup
- OpenAPI-first generation via `openapi-generator-maven-plugin` into `target/`

## Project Layout

- `openapi/openapi.yaml`: API contract (source of truth)
- `src/main/java/com/rvdijkz/tour/controller/HealthController.java`: implementation of generated `SystemApi`
- `src/main/java/com/rvdijkz/tour`: app code
- `src/main/resources/db/changelog`: Liquibase migrations
- `src/test/java/com/rvdijkz/tour/controller/HealthContractIntegrationTest.java`: response-shape contract test for `/api/v1/health`

## Available endpoints

- `GET /api/v1/health`: status + timestamp
- `GET /api/v1/info`: service metadata (name + version)

## Quick start

```powershell
mvn clean test
mvn spring-boot:run -Dspring-boot.run.profiles=no-security
```

## Notes

- Generated OpenAPI code is configured to output to `target/generated-sources/openapi` and should not be committed.
- Default `application.yml` values are local placeholders; adjust datasource and issuer URI for your environment.

