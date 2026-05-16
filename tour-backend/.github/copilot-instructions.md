# Backend Copilot Instructions

## Technical Stack
* Java 21+ (Leverage modern Java features like Records, Pattern Matching, and Switch Expressions where applicable).
* Maven 3
* Spring Boot 3 & Spring Security (OAuth2/OIDC Resource Server configuration).
* JPA / Hibernate & PostgreSQL.
* Liquibase for database migrations.

## Architecture: Hexagonal (Ports & Adapters)
* Keep the core domain business logic isolated from frameworks, databases, and external APIs.
* **Driving Adapters (Inbound):** Controllers, REST endpoints.
* **Driven Adapters (Outbound):** JPA Repositories, external service clients.
* **Ports:** Use interfaces to define boundaries between the domain and adapters.

## Backend Rules
1. **Thin Controllers:** Controllers must only handle HTTP routing, request validation, and calling the appropriate application service.
2. **Rich Business Logic:** Place all business rules and validation logic inside dedicated Domain Services.
3. **API Boundaries:** Use Data Transfer Objects (DTOs) strictly at the API boundary (Controllers). Never expose JPA entities directly to the frontend.
4. **Database Migrations:** Every database schema change must be written as a separate Liquibase changeLog (YAML or SQL). Never rely on `ddl-auto=update`.

## Strict OpenAPI & Code Generation Rules
1. **Spec Before Code:** Before generating any new endpoint or modifying an existing one, stop and ask to update the OpenAPI specification file first.
2. **Code Generation:** Use the `openapi-generator-maven-plugin` to generate backend stub interfaces and DTO models.
3. **Implementation Only:** Implement the generated interfaces in your Driving Adapters (Controllers). Never modify the generated code directly; only configure the generator plugin if changes to the output structure are needed.
4. **Validation:** Ensure JSR-383 validation annotations (e.g., `@Valid`, `@NotNull`) are fully driven by the OpenAPI constraints and generated into the models automatically.
5. **Target Directory:** Ensure all generated source files from the OpenAPI plugin land within the target directory of `/tour-backend`. Never commit generated code to version control.

## Spring Security & OAuth2 Implementation Rules
1. **Resource Server Configuration:** Always configure Spring Security as an OAuth2 Resource Server that validates JWTs (`http.oauth2ResourceServer(oauth2 -> oauth2.jwt(...))`).
2. **Modern SecurityFilterChain:** Use the modern component-based security configuration. Always define security via a `SecurityFilterChain` bean. Never use deprecated builders.
3. **Stateless Sessions:** Explicitly enforce stateless session management: `http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))`.
4. **Method Security:** Enable `@EnableMethodSecurity` on configuration classes. Use `@PreAuthorize("hasAuthority('SCOPE_...')")` or `@PreAuthorize("hasRole('ROLE_...')")` on Driving Adapters (Controllers) to restrict endpoint access based on JWT claims.
5. **Local Security Bypass:** Implement a `no-security` Spring Profile that registers a permissive `SecurityFilterChain` (`permitAll()`) for rapid local development and testing.
6. **Test Mocking:** When writing `@SpringBootTest` or `@WebMvcTest` architecture tests, use `@WithMockUser` or Spring Security's Test support to safely mock JWT authentication context without requiring an active IdP.
7. **CORS Configuration:** Do not hardcode CORS origins. Configure CORS via a dedicated bean that reads allowed origins from Spring `@ConfigurationProperties`.

## Coding Standards

### Package Structure
- All Java classes must be placed within the base package structure: `com.rvdijkz`
- Do not use generic top-level packages outside of this namespace.
- Example structure:
    - `com.rvdijkz.controller`
    - `com.rvdijkz.service`
    - `com.rvdijkz.repository`
