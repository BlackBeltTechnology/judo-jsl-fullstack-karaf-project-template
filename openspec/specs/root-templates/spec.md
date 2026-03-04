# Root Templates Specification

## Purpose

Provides Handlebars templates for generating the root-level Maven project structure of a JUDO fullstack Karaf application. This includes the parent POM (composed from fragments), build scripts, configuration files, and logging setup.

## Architecture

The `judo-jsl-fullstack-karaf-project-template-root` module contains:

- A **template manifest** (`fullstack-project.yaml`) that declares all template files for the generator plugin
- A **main POM template** (`pom.xml.hbs`) that includes multiple fragment files via Handlebars partials
- **Fragment templates** (`pom.xml.*.fragment.hbs`) for modular POM composition: project definition, properties, repositories, distribution management, dependency management, plugin management, modules, plugins, and profiles
- **Support file templates**: `judo.sh.hbs` (build/deploy script), `judo.properties.hbs`, `judo-karaf.env.hbs`, `logback-test.xml.hbs`, `generator-parameter.properties.hbs`, `README.adoc.hbs`
- **Generator overrides** directory with a `.gitignore` template

## Requirements

### Requirement: POM fragment composition

The root `pom.xml.hbs` template SHALL include all `pom.xml.*.fragment.hbs` files as Handlebars partials to compose a complete Maven parent POM.

#### Scenario: Full POM generation
- **GIVEN** a complete set of template parameters (groupId, artifactId, version, dependency versions)
- **WHEN** the root template is processed by `judo-jsl-generator-maven-plugin`
- **THEN** the output `pom.xml` includes all sections: project definition, properties, repositories, dependency management, plugin management, modules, plugins, profiles, and distribution management

### Requirement: Conditional module inclusion

The `pom.xml.extra-modules.fragment.hbs` template SHALL conditionally include `<module>` entries based on `StoredVariableHelper` generation flags.

#### Scenario: Model module enabled
- **GIVEN** `shouldGenerateModelModule()` returns `true`
- **WHEN** the modules fragment is rendered
- **THEN** the model module `<module>` entry is included in the POM

#### Scenario: E2E module disabled by default
- **GIVEN** no explicit `generateE2eModule` flag is set
- **WHEN** the modules fragment is rendered
- **THEN** the E2E module `<module>` entry is NOT included (default is `false`)

### Requirement: Property interpolation

All templates SHALL correctly interpolate template parameters (groupId, artifactId, version, dependency versions, frontend configuration) into the generated files.

#### Scenario: Version properties in generated POM
- **GIVEN** template parameters including `judoTatamiJslVersion`, `judoPlatformVersion`, etc.
- **WHEN** the properties fragment is rendered
- **THEN** the generated POM contains correct `<properties>` entries with the provided version values

### Requirement: Build script generation

`judo.sh.hbs` SHALL generate a shell script that provides build and deployment commands for the generated project.

#### Scenario: Script is executable
- **WHEN** the template is processed
- **THEN** the output `judo.sh` file is a valid shell script with build/deploy commands referencing the correct artifact coordinates

### Requirement: Template manifest completeness

`fullstack-project.yaml` SHALL declare every `.hbs` template file in the module so the generator plugin processes all of them.

#### Scenario: All templates listed
- **GIVEN** the `fullstack-project.yaml` manifest
- **WHEN** compared to the actual `.hbs` files in `src/main/resources/`
- **THEN** every `.hbs` file has a corresponding entry in the manifest
