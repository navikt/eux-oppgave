# Copilot Instructions — eux-oppgave

## Build and Test

We run a local postgress and env. variables are set. meaning, a simple `mvnd clean install` should pass, if not, ask user to start a postgress instance.

## Architecture

This is a Spring Boot 4 / Kotlin microservice that wraps NAV's external Oppgave API, adding idempotency tracking, retry logic, and dual OAuth2 authentication. It runs on NAIS (GCP Kubernetes) in the `eessibasis` namespace.

### Module dependency graph

```
eux-oppgave-openapi    → (OpenAPI spec → generated Kotlin Spring interfaces + models)
eux-oppgave-model      → (domain DTOs, JPA entities, enums, exceptions)
eux-oppgave-persistence → model (JPA repository, Flyway migrations)
eux-oppgave-integration → model (HTTP client to external Oppgave API, OAuth2 config, retry)
eux-oppgave-service    → persistence, integration, model (business logic orchestration)
eux-oppgave-webapp     → service, openapi (controllers, exception handlers, Spring Boot app)
```

### Request flow

OpenAPI-generated interface (`OppgaverApi`) → `OppgaverApiImpl` controller → Service layer → either `EuxOppgaveStatusRepository` (local DB) or `OppgaveClient` (external API) → response mapped back through extension functions.

### Dual OAuth2 strategy

`DualOppgaveRestTemplate` selects authentication based on JWT context:
- **User context** (NAVident present): JWT Bearer token exchange via `client-secret-basic`
- **Service context** (no NAVident): Client Credentials with private key JWT signing

## Key Conventions

### Language & runtime

- **100% Kotlin**, Java 25 target, Spring Boot 4.0.3
- Kotlin compiler plugins: `spring` (all-open) and `jpa` (no-arg) for entity/config classes

### OpenAPI code generation

The API contract is defined in hand-written YAML specs under `eux-oppgave-openapi/src/main/resources/static/`. The `openapi-generator-maven-plugin` (v6.4.0) generates **interface-only** Kotlin Spring code during `generate-sources`. Controllers implement these generated interfaces (e.g., `OppgaverApiImpl` implements `OppgaverApi`). Never edit generated code in `target/`.

### Model mapping via extension properties

Model conversion between layers uses Kotlin **extension properties** (not mapper classes or mapping frameworks). These are defined in dedicated `*Mapper.kt` files:

```kotlin
// Extension property pattern used throughout
val Oppgave.euxOppgave get() = EuxOppgave(id = id, ...)
val EuxOppgaveOpprettelse.oppgaveOpprettelse get() = OppgaveOpprettelse(...)
```

Three model layers: OpenAPI types → domain DTOs (`model` module) → integration DTOs (`integration` module).

### Data classes

Domain models use Kotlin `data class` with default values. JPA entities use `data class` with `@Entity` and Kotlin compiler plugins for no-arg constructors.

### Naming

The domain language is **Norwegian**:
- `oppgave` = task, `ferdigstill` = complete, `tildelt` = assigned
- `enhetsnr` = unit number, `journalpost` = journal entry, `tema` = theme
- `behandlingstema`/`behandlingstype` = treatment theme/type

### Testing

- Tests live only in `eux-oppgave-webapp` (integration tests against full Spring context)
- Base class: `AbstractOppgaverApiImplTest` — sets up MockOAuth2Server, TestRestTemplate, cleans DB between tests
- Assertions: Kotest (`shouldMatchJsonResource` for JSON comparison)
- Test data: Dataset classes in `webapp/dataset/` and expected JSON in `src/test/resources/dataset/expected/`
- External API mocked via `MockWebServer` (port 8181)
- Test names use Norwegian backtick-quoted descriptions

### Database

- PostgreSQL 18, single table `eux_oppgave_status`, Flyway migrations in `eux-oppgave-persistence/src/main/resources/db/migration/`
- The table tracks task creation status for idempotency (UUID-based deduplication)

### Error handling

Custom `@RestControllerAdvice` handlers map exceptions to HTTP responses:
- `OppgaveStatusEksistererException` → 409 CONFLICT
- `OppgaveUgyldigRequestException` → 422
- `MethodArgumentNotValidException` → 400 with field details

### Logging

Uses `kotlin-logging` (`io.github.oshai.kotlinlogging.KotlinLogging.logger`). MDC context is set per request with `journalpostId` and `personident` for correlation.
