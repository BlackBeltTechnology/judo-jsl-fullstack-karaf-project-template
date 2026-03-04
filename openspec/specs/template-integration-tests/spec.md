# Template Integration Tests Specification

## Purpose

Validates that the template generator produces correct, complete project structures by invoking `judo-jsl-generator-maven-plugin` with various parameter combinations and verifying the output.

## Architecture

The `judo-jsl-fullstack-karaf-project-template-test` module uses Maven plugin executions (not JUnit tests) to exercise the template generation pipeline. The test POM configures multiple `judo-jsl-generator-maven-plugin` executions with different template parameter sets, targeting both root and application template types. Generated output goes to `target/classes`.

Dependencies include all three template modules (common, root, application) plus `judo-tatami-jsl` for model processing.

## Requirements

### Requirement: Root template generation

The test module SHALL execute `judo-jsl-generator-maven-plugin` with `<type>fullstack-project</type>` targeting the root template module.

#### Scenario: Root POM generation
- **GIVEN** a test configuration with standard template parameters (groupId, artifactId, version, dependency versions)
- **WHEN** the `generate-sources` phase runs
- **THEN** a valid root Maven POM and supporting files are generated in `target/classes`

### Requirement: Application template generation

The test module SHALL execute `judo-jsl-generator-maven-plugin` with a JSL model artifact to generate application sub-modules.

#### Scenario: Full application generation
- **GIVEN** a JSL model artifact and template parameters including frontend type, node version, and all required dependency versions
- **WHEN** the `generate-sources` phase runs
- **THEN** application module POMs and configuration files are generated for all enabled modules

### Requirement: Parameter variation coverage

The test module SHALL test multiple parameter combinations to validate conditional generation.

#### Scenario: Different SQL dialects
- **GIVEN** `sqlDialects` set to `"hsqldb"` or `"postgresql"`
- **WHEN** templates are generated
- **THEN** the output correctly reflects the chosen dialect in generated configurations

#### Scenario: Optional parameters
- **GIVEN** optional parameters like `generateOpenApiAnnotations`, `baseUrl`, `authenticationUrl`
- **WHEN** templates are generated with and without these parameters
- **THEN** the output correctly includes or omits related configuration

### Requirement: Template helper registration

The test executions SHALL register `JslProjectHelper` (or `JslDslProjectHelper`) as a helper class for the generator plugin.

#### Scenario: Helper availability
- **GIVEN** the helper class is declared in `<helpers>` configuration
- **WHEN** templates reference helper methods like `plainName()` or `shouldGenerateModelModule()`
- **THEN** the helpers resolve correctly and template rendering succeeds without errors
