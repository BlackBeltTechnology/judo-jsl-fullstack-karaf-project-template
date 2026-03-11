# Application Templates Specification

## Purpose

Provides Handlebars templates for generating all application-level sub-modules of a JUDO fullstack Karaf project. Each sub-directory contains templates for a specific concern: app (main application), model, rest, sdk, internal, frontend-react, docker, e2e, interceptors, karaf-features, karaf-offline, keycloak-theme, schema, and web-root.

## Architecture

The `judo-jsl-fullstack-karaf-project-template-application` module organizes templates by sub-module:

| Directory | Generated Module | Default |
|-----------|-----------------|---------|
| `app/` | Application assembly module | Enabled |
| `model/` | JSL model module | Enabled |
| `rest/` | REST API module | Enabled |
| `sdk/` | SDK module | Enabled |
| `internal/` | Internal API module | Enabled |
| `frontend-react/` | React frontend (with per-actor and model sub-modules) | Enabled |
| `docker/` | Docker Compose configurations (develop, postgresql-https) | Enabled |
| `e2e/` | End-to-end tests (with actor and model sub-modules) | Disabled |
| `interceptors/` | Operation call and authentication interceptors | Enabled |
| `karaf-features/` | Karaf feature descriptor (feature.xml) | Enabled |
| `karaf-offline/` | Offline Karaf distribution | Disabled |
| `keycloak-theme/` | Keycloak login theme with Docker Compose test setup | Disabled |
| `schema/` | Database schema module | Enabled |
| `web-root/` | Static web content (Bootstrap, Bootstrap Table, Popper.js) | Enabled |

Each sub-module directory follows the same fragment pattern: `pom.xml.hbs` as the main template, with `pom.xml.*.fragment.hbs` files for modular composition.

## Requirements

### Requirement: Sub-module POM generation

Each application sub-module template directory SHALL produce a valid Maven POM using the fragment pattern (main `pom.xml.hbs` with partial includes).

#### Scenario: App module POM
- **GIVEN** application generation is enabled (`shouldGenerateApplicationModule()` returns `true`)
- **WHEN** the `app/pom.xml.hbs` template is processed
- **THEN** a complete Maven POM is generated with correct parent reference, dependencies, and plugin configurations

### Requirement: Frontend per-actor generation

The `frontend-react/` templates SHALL generate a separate frontend module for each actor (transfer declaration) in the JSL model, plus a model sub-module and a parent aggregator.

#### Scenario: Multiple actors
- **GIVEN** a JSL model with actors `AdminActor` and `UserActor`
- **WHEN** frontend templates are processed
- **THEN** separate `frontend-react/actor/` modules are generated for each actor, each with its own POM

### Requirement: Docker Compose variants

The `docker/` templates SHALL generate multiple Docker Compose configurations for different environments.

#### Scenario: Development compose
- **WHEN** Docker module generation is enabled
- **THEN** `compose-develop/docker-compose.yml` is generated with development-suitable configuration

#### Scenario: PostgreSQL HTTPS compose
- **WHEN** Docker module generation is enabled
- **THEN** `compose-postgresql-https/docker-compose.yml` is generated with PostgreSQL and HTTPS configuration

### Requirement: Interceptor default implementations

The `interceptors/` templates SHALL generate default Java interceptor implementations for logging.

#### Scenario: Default interceptors
- **WHEN** interceptor module generation is enabled
- **THEN** `LogAuthenticationInterceptor.java` and `LogOperationCallInterceptor.java` default files are generated in the correct package structure

### Requirement: Karaf feature descriptor

The `karaf-features/` templates SHALL generate a `feature.xml` describing OSGi bundles and repositories for Karaf deployment.

#### Scenario: Feature with model bundles
- **GIVEN** a generated model module
- **WHEN** Karaf features template is processed
- **THEN** the `feature.xml` includes bundle entries for model artifacts, with fragment files for extra bundles and repositories

### Requirement: Keycloak theme generation

The `keycloak-theme/` templates SHALL generate a complete Keycloak login theme with FreeMarker templates, CSS, and a Docker Compose test setup — only when `shouldGenerateKeycloakTheme()` is `true`.

#### Scenario: Theme enabled
- **GIVEN** `generateKeycloakTheme` flag is `"true"`
- **WHEN** templates are processed
- **THEN** login page templates (`login.ftl`, `register.ftl`, `error.ftl`, `template.ftl`, `footer.ftl`), CSS, theme properties, assembly descriptor, Docker Compose, and test realm JSON are generated

### Requirement: Conditional generation respects flags

Each sub-module's templates SHALL only be processed when the corresponding `StoredVariableHelper.shouldGenerate*()` flag returns `true`.

#### Scenario: Disabled module not generated
- **GIVEN** `generateE2eModule` is `"false"` or not set
- **WHEN** the application template manifest is processed
- **THEN** no E2E module files are generated in the output
