# JUDO JSL Fullstack Karaf Project Template - Project Documentation

## Project Overview


**Repository:** BlackBeltTechnology/judo-jsl-fullstack-karaf-project-template
**License:** Eclipse Public License 2.0 (EPL-2.0) or GPL-2.0 WITH Classpath-exception-2.0
**Java Version:** 21
**Build System:** Maven 3.9.4+ with Maven Wrapper and OSGi bundle packaging

1. A **Maven-based project template generator** that produces complete fullstack applications for the JUDO platform targeting Apache Karaf
2. Uses **Handlebars templates** (`.hbs` files) driven by YAML manifests to generate Maven project structures including backend, frontend, Docker, and E2E tests
3. Provides **two Java helper classes** (`JslProjectHelper`, `StoredVariableHelper`) that supply naming transformations and conditional generation flags to templates
4. Generated projects are **modular and conditional** — 16+ boolean flags control which modules (model, SDK, REST, frontend, Docker, Karaf features, interceptors, etc.) are included
5. All modules are packaged as **OSGi bundles** for deployment in Apache Karaf runtime

## Code Instructions

1. First think through the problem, read the codebase for relevant files.
2. Before you make any major changes, check in with me and I will verify the plan.
3. Please every step of the way just give me a high level explanation of what changes you made.
4. Make every task and code change you do as simple as possible. We want to avoid making any massive or complex changes. Every change should impact as little code as possible. Everything is about simplicity.
5. Maintain a documentation file that describes how the architecture of the app works inside and out.
6. Never speculate about code you have not opened. If the user references a specific file, you MUST read the file before answering. Make sure to investigate and read relevant files BEFORE answering questions about the codebase. Never make any claims about code before investigating unless you are certain of the correct answer - give grounded and hallucination-free answers.
7. For implementation use TDD (Test-Driven Development): write or update tests first to define the expected behaviour, verify they fail, then write the minimal implementation to make them pass.
8. Use DRY (Don't Repeat Yourself): extract reusable logic into separate classes, utilities, or components. If the same pattern appears in multiple places, refactor it into a shared helper.

## Directory Structure

```
judo-jsl-fullstack-karaf-project-template/
├── pom.xml                          # Parent POM (Java 21, OSGi bundle packaging)
├── .mvn/                            # Maven wrapper config (JVM: -Xms1024m -Xmx2048m)
├── .github/workflows/               # CI/CD pipelines (build, release, version bump)
├── judo-jsl-fullstack-karaf-project-template-common/
│   └── src/main/java/.../           # JslProjectHelper.java, StoredVariableHelper.java
├── judo-jsl-fullstack-karaf-project-template-root/
│   └── src/main/resources/          # Root project templates (.hbs), fullstack-project.yaml
├── judo-jsl-fullstack-karaf-project-template-application/
│   └── src/main/resources/          # Application module templates (app, docker, e2e, frontend, etc.)
└── judo-jsl-fullstack-karaf-project-template-test/
    └── pom.xml                      # Integration tests via generator plugin executions
```

## Core Modules

### Template Infrastructure

| Module | Type | Purpose |
|--------|------|---------|
| `judo-jsl-fullstack-karaf-project-template-common/` | Java (bundle) | Helper classes for Handlebars template rendering — string transformations and context-managed generation flags |
| `judo-jsl-fullstack-karaf-project-template-root/` | Resources (bundle) | Handlebars templates for the root-level generated project: parent POM (with fragment includes), build scripts, config files |
| `judo-jsl-fullstack-karaf-project-template-application/` | Resources (bundle) | Handlebars templates for all application sub-modules: app, model, rest, sdk, internal, frontend-react, docker, e2e, interceptors, karaf-features, karaf-offline, keycloak-theme, schema, web-root |
| `judo-jsl-fullstack-karaf-project-template-test/` | Test | Integration tests that invoke `judo-jsl-generator-maven-plugin` with various parameter combinations to validate template generation |

### Key Java Classes

| Class | Role |
|-------|------|
| `JslProjectHelper` | `@TemplateHelper` extending `StaticMethodValueResolver`. Provides static methods registered as Handlebars helpers: `plainName()`, `pathName()`, `filePathName()`, `fqClass()`, `packageName()`, `capitalizeEL()`, `resolveFrontendType()`, `isReact()`, `isMultipleFrontend()`, `keycloakThemeFolder()` |
| `StoredVariableHelper` | `@TemplateHelper` + `@ContextAccessor` extending `StaticMethodValueResolver`. ThreadLocal-based context manager exposing 16 `shouldGenerate*()` flags plus `getBaseUrl()`, `getAuthenticationUrl()`, `getSpecificationVersionNumber()` |

## Technology Stack

### Core Technologies
- **Java 21** — compiler source and target
- **Handlebars 4.1.2** (`com.github.jknack:handlebars`) — template engine for `.hbs` files
- **JUDO Generator Commons** (`judo-generator-commons`) — provides `StaticMethodValueResolver`, `@TemplateHelper`, `@ContextAccessor`, `ThreadLocalContextHolder`
- **JUDO Meta JSL** (`hu.blackbelt.judo.meta.jsl.model`) — JSL metamodel classes (`ModelDeclaration`, `TransferDeclaration`)
- **Spring Expression Language 5.0.0** (`spring-expression`) — used for SpEL-based helpers
- **Lombok 1.18.34** — annotation processing (`@Log`)
- **SLF4J 2.0.16 / Logback 1.5.12** — logging

### Build & Quality
- **Maven 3.9.4+** with Maven Wrapper (`mvnw` / `mvnw.cmd`)
- **Apache Felix maven-bundle-plugin 5.1.9** — OSGi bundle packaging
- **flatten-maven-plugin 1.5.0** — CI-friendly `${revision}` versioning
- **Maven Surefire 3.5.1** — test execution with JDK 21 module opens
- **JaCoCo 0.8.12** — code coverage
- **SonarQube** (`sonar-maven-plugin 3.9.1.2184`) — code quality analysis
- **Lombok Maven Plugin 1.18.20.0** — delombok for Javadoc generation

## Build Commands

```sh
# Run tests
mvn clean test

# Full build and install
mvn clean install

# Use Maven wrapper
./mvnw clean install

# Skip sub-modules (parent only)
mvn clean install -DskipModules=true

# Run a single test class
mvn clean test -pl judo-jsl-fullstack-karaf-project-template-test -Dtest=ClassName
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `modules` | Active by default. Includes all 4 sub-modules. Disable with `-DskipModules=true` |
| `sign-artifacts` | GPG-signs artifacts using `sign-maven-plugin` |
| `release-dummy` | Deploys to local `/tmp/` directory for testing releases |
| `release-judong` | Deploys to JUDO Nexus (`nexus.judo.technology`) |
| `release-central` | Deploys to Maven Central via Sonatype OSSRH with nexus-staging |
| `generate-github-asciidoc-diagrams` | Renders AsciiDoc/PlantUML diagrams to PNG |
| `update-source-code-license` | Updates EPL-2.0 license headers in all source files |

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Parent POM defining Java 21, all dependencies, plugins, and 7 profiles |
| `.mvn/jvm.config` | JVM memory: `-Xms1024m -Xmx2048m` plus JDK 21 module opens |
| `.mvn/extensions.xml` | Maven extensions: `wagon-file`, `wagon-webdav-jackrabbit` |
| `logback-test.xml` | Test logging config (console appender, INFO level) |
| `*/src/main/resources/fullstack-project.yaml` | Template manifests listing all `.hbs` files to process |

## Development Environment

**Required:**
- Java 21 JDK
- Maven 3.9.4+ (or use `./mvnw`)
- Docker (for generated project development features)

**JVM flags for tests** (configured in surefire):
```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.time=ALL-UNNAMED
```

## Git Workflow

- **Main Branch:** `develop`
- **Release Branch:** `master` contains latest released sources
- **Versioning:** `${revision}` = `1.0.0-SNAPSHOT`; dynamic versions on develop: `major.minor.qualifier.YYYYMMDD_HHmmss_commitId_branchName`
- **Branching Model:** GitFlow with `feature/JNG-xxx`, `release/x.y`, `bugfix/JNG-xxx`, `support/JNG-xxx`, `hotfix/JNG-xxx`
- **Rule:** Every commit must reference a JIRA ticket (`JNG-xxx`)

## Important Notes

1. This project does NOT run as an application — it is consumed as a Maven dependency by the `judo-jsl-generator-maven-plugin` which processes its templates
2. Template parameters are passed via Maven plugin `<templateParameters>` configuration in the consuming project's POM — almost all are mandatory
3. The root POM templates use a **fragment pattern**: `pom.xml.hbs` includes multiple `pom.xml.*.fragment.hbs` files for modular composition
4. `StoredVariableHelper` uses `ThreadLocalContextHolder` for state — this is thread-safe but requires proper context binding/unbinding
5. Some `shouldGenerate*()` flags have interdependencies: `shouldGenerateSingleActorApplication()` throws `IllegalArgumentException` if used with `generateWebrootModule`
6. The `judo-version-updater-maven-plugin` is used for automated dependency version updates across the JUDO ecosystem
7. OSGi export package: `hu.blackbelt.judo.jsl.fullstack.project.archetype`

## Related Documentation

- [README.md](README.md) — Usage guide with architecture diagram and example POM
- [CONTRIBUTING.md](CONTRIBUTING.md) — Development setup and submission guidelines
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD pipeline documentation with flow diagrams
- [judo-jsl-generator-maven-plugin](https://github.com/BlackBeltTechnology/judo-meta-jsl) — Plugin documentation
- [judo-community CONTRIBUTING](https://github.com/BlackBeltTechnology/judo-community/blob/develop/CONTRIBUTING.adoc) — Parent project contributing guide
