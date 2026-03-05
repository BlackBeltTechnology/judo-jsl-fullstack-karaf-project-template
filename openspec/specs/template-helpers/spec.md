# Template Helpers Specification

## Purpose

Provides Handlebars template helper classes (`JslProjectHelper` and `StoredVariableHelper`) that supply naming transformations, frontend type resolution, and conditional module generation flags to the template engine during project generation.

## Architecture

The `judo-jsl-fullstack-karaf-project-template-common` module contains two `@TemplateHelper` classes extending `StaticMethodValueResolver` from `judo-generator-commons`:

- **JslProjectHelper** — Stateless static methods for string/name transformations, registered as Handlebars value resolvers
- **StoredVariableHelper** — ThreadLocal-based context accessor (`@ContextAccessor`) managing 16+ boolean generation flags and configuration values via `ThreadLocalContextHolder`

Both classes are in package `hu.blackbelt.judo.jsl.fullstack.karaf.project.archetype`.

## Requirements

### Requirement: Name sanitization

`JslProjectHelper.plainName()` SHALL convert any input string to a safe identifier containing only lowercase alphanumeric characters and underscores.

#### Scenario: Special characters removed
- **GIVEN** an input string containing dots, hyphens, or other special characters
- **WHEN** `plainName()` is called
- **THEN** all non-alphanumeric characters (except underscores) are replaced with underscores and the result is lowercased

### Requirement: Path name conversion

`JslProjectHelper.pathName()` SHALL convert fully qualified names to a flat path format using double underscore separators.

#### Scenario: FQ name with dot separators
- **GIVEN** a fully qualified name like `"com.example.MyClass"`
- **WHEN** `pathName()` is called
- **THEN** dots are replaced with `__`, camelCase is split with underscores, and the result is lowercased

#### Scenario: FQ name with namespace separators
- **GIVEN** a name containing `::`, `#`, or `/` separators
- **WHEN** `pathName()` is called
- **THEN** all separators are replaced with `__` and the result is lowercased

### Requirement: File path conversion

`JslProjectHelper.filePathName()` SHALL convert fully qualified names to filesystem paths using the platform file separator.

#### Scenario: Dot-separated name to file path
- **GIVEN** a qualified name like `"com.example.MyClass"`
- **WHEN** `filePathName()` is called
- **THEN** separators (`.`, `::`, `#`, `/`) become file separators and camelCase is split

### Requirement: Fully qualified class name generation

`JslProjectHelper.fqClass()` SHALL convert a qualified name into a PascalCase class name by capitalizing each segment.

#### Scenario: Namespace-separated name
- **GIVEN** a name with `::`, `.`, `#`, `/`, or `_` separators
- **WHEN** `fqClass()` is called
- **THEN** each segment is capitalized and joined without separators

### Requirement: Package name extraction

`JslProjectHelper.packageName()` SHALL extract the middle segments of a `::` separated qualified name, excluding the first and last segments.

#### Scenario: Three-segment name
- **GIVEN** a name like `"Model::Package::Type"`
- **WHEN** `packageName()` is called
- **THEN** the result is the capitalized middle segment(s)

#### Scenario: Two or fewer segments
- **GIVEN** a name with fewer than 3 `::` segments
- **WHEN** `packageName()` is called
- **THEN** the result is `null`

### Requirement: Frontend type resolution

`JslProjectHelper.resolveFrontendType()` SHALL parse a comma-separated frontend type string and default to `["react"]` when null.

#### Scenario: Null input defaults to React
- **GIVEN** a null frontend type parameter
- **WHEN** `resolveFrontendType()` is called
- **THEN** the result is a collection containing `"react"`

#### Scenario: Multiple frontend types
- **GIVEN** a string like `"react,angular"`
- **WHEN** `resolveFrontendType()` is called
- **THEN** the result is a collection containing both trimmed values

### Requirement: Multiple frontend detection

`JslProjectHelper.isMultipleFrontend()` SHALL return `true` when more than one frontend type is configured.

#### Scenario: Single vs multiple
- **WHEN** `isMultipleFrontend("react")` is called
- **THEN** the result is `false`
- **WHEN** `isMultipleFrontend("react,angular")` is called
- **THEN** the result is `true`

### Requirement: Conditional module generation flags

`StoredVariableHelper` SHALL expose `shouldGenerate*()` methods that read boolean flags from ThreadLocal context and return sensible defaults when flags are not set.

#### Scenario: Default generation behavior
- **GIVEN** no explicit flag is set in the ThreadLocal context
- **WHEN** `shouldGenerateModelModule()` is called
- **THEN** the result is `true` (default)
- **WHEN** `shouldGenerateE2EModule()` is called
- **THEN** the result is `false` (default — E2E is opt-in)

#### Scenario: Explicit flag override
- **GIVEN** `generateModelModule` is set to `"false"` in the context
- **WHEN** `shouldGenerateModelModule()` is called
- **THEN** the result is `false`

### Requirement: Single actor and webroot mutual exclusion

`StoredVariableHelper.shouldGenerateSingleActorApplication()` SHALL throw `IllegalArgumentException` if both `generateSingleActorApplication` and `generateWebrootModule` with `generateWebrootContent` are set.

#### Scenario: Conflicting flags
- **GIVEN** both `generateWebrootModule` and `generateWebrootContent` are set to `"true"`
- **WHEN** `shouldGenerateSingleActorApplication()` is called
- **THEN** an `IllegalArgumentException` is thrown

### Requirement: Configuration value accessors

`StoredVariableHelper` SHALL provide `getBaseUrl()`, `getAuthenticationUrl()`, and `getSpecificationVersionNumber()` to retrieve string configuration from ThreadLocal context.

#### Scenario: Retrieve base URL
- **GIVEN** `baseUrl` is set to `"http://localhost:8181"` in the context
- **WHEN** `getBaseUrl()` is called
- **THEN** the result is `"http://localhost:8181"`
