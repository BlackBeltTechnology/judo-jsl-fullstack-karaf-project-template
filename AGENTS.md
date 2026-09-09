# judo-jsl-fullstack-karaf-project-template — module agent doctrine

## Module purpose

`judo-jsl-fullstack-karaf-project-template` is the *project skeleton factory* of
the JSL stack: consumed as a Maven dependency by
`judo-jsl-generator-maven-plugin`, it turns a JSL model declaration
(`hu.blackbelt.judo.meta.jsl.model` — `ModelDeclaration`, `TransferDeclaration`,
the actors declared on it) plus a set of template parameters into a complete,
buildable multi-module Maven project that runs as an OSGi application on Apache
Karaf. Where the UI generators emit source files into an existing project, this
one emits the project itself — parent POM, module POMs, build scripts,
configuration, container assembly and developer tooling.

A generated project, end to end: a `model` module carrying the JSL sources and
the ESM/RDBMS artifacts derived from them; a `schema` module with the Liquibase
migration set for the application database; `sdk` (the generated Java SDK for
the model) and `rest`/`internal` service layers; `interceptors` for
business-logic hooks around generated operations, with its own test source set;
`app` as the assembled application bundle with its own unit and integration
tests; `frontend-react` and `web-root` wiring in the React applications produced
by `judo-ui-react-template` (one per actor, plus the multi-actor landing root);
`e2e` holding the Playwright suites from `judo-ui-e2e-template`; a
`keycloak-theme` module with a login theme and its assembly; `karaf-features`
and `karaf-offline` producing the feature descriptors and the offline
repository the Karaf distribution boots from; and `docker` with the Dockerfile,
Karaf `bin` overrides and Compose stacks (`compose-develop`,
`compose-postgresql-https`) that bring the app, its database and Keycloak up
together. `judo.sh` / `judo.properties` and `.sdkmanrc` land at the project root
as the developer entry point, and a `generator-overrides` directory is emitted
so a project can override generated files without forking the templates.

None of it is unconditional. `StoredVariableHelper` exposes sixteen
`shouldGenerate*()` flags, and template registry entries carry a
`conditionExpression` (e.g. `#shouldGenerateSdkModule()`), so the same template
set produces a backend-only service, a single-actor app, or the full stack
depending on what the consuming POM asked for.

**Repository:** BlackBeltTechnology/judo-jsl-fullstack-karaf-project-template ·
**Artifact:** `judo-jsl-fullstack-karaf-project-template-parent`
(version `${revision}` = `1.0.0-SNAPSHOT`) · **OSGi export package:**
`hu.blackbelt.judo.jsl.fullstack.project.archetype` ·
**License:** EPL-2.0 or GPL-2.0 WITH Classpath-exception-2.0 · **Java:** 21 ·
**Build:** Maven 3.9.4+ with wrapper.

## Reactor map

The root `pom.xml` declares its `<modules>` inside the `modules` profile, active
by default and disabled with `-DskipModules=true`. Build order is the declared
order — `common` first, because both template modules call its helpers.

<modules>
  <module>judo-jsl-fullstack-karaf-project-template-common</module>
  <module>judo-jsl-fullstack-karaf-project-template-root</module>
  <module>judo-jsl-fullstack-karaf-project-template-application</module>
  <module>judo-jsl-fullstack-karaf-project-template-test</module>
</modules>

| Module | Packaging | What it contributes |
|---|---|---|
| `judo-jsl-fullstack-karaf-project-template-common` | `bundle` | The two helper classes every template calls, under `hu.blackbelt.judo.jsl.fullstack.karaf.project.archetype`. `JslProjectHelper` (`@TemplateHelper` on `StaticMethodValueResolver`) derives names and paths — `plainName()`, `pathName()`, `filePathName()`, `fqClass()`, `packageName()`, `capitalizeEL()` — and answers frontend questions: `resolveFrontendType()`, `isReact()`, `isMultipleFrontend()`, `keycloakThemeFolder()`. `StoredVariableHelper` (`@TemplateHelper` + `@ContextAccessor`) is the generation-decision surface: a `ThreadLocalContextHolder`-backed context exposing the sixteen `shouldGenerate*()` flags plus `getBaseUrl()`, `getAuthenticationUrl()`, `getSpecificationVersionNumber()`. |
| `judo-jsl-fullstack-karaf-project-template-root` | `bundle` | Templates for the *outer* generated project: the aggregator POM assembled from `pom.xml.*.fragment.hbs` fragments, the `judo.sh` launcher (emitted with explicit `rwxrwx---` permission) and `judo.properties`, `README.adoc`, `.sdkmanrc`, `.gitignore` / `.generator-ignore`, the `mvn/` build configuration and the `generator-overrides` hook — everything a developer meets before opening a module. Its `fullstack-project.yaml` is the manifest binding each template to an output path. |
| `judo-jsl-fullstack-karaf-project-template-application` | `bundle` | Templates for every module *inside* the generated project — `model`, `schema` (plus `schema/migration`), `sdk`, `rest`, `internal`, `interceptors` (with main and test source sets), `app` (with main and test source sets), `frontend-react` (per-actor and model wiring), `web-root`, `e2e` (per-actor and model wiring), `keycloak-theme` (login theme resources, CSS, assembly descriptor), `karaf-features` (feature XML under `src/main/feature`), `karaf-offline`, and `docker` (Dockerfile, `bin` overrides, `compose-develop`, `compose-postgresql-https`). Its own `fullstack-project.yaml` carries the `conditionExpression` per entry that makes the module set configurable. |
| `judo-jsl-fullstack-karaf-project-template-test` | `bundle` | The executable check: invokes `judo-jsl-generator-maven-plugin` in several executions with different template-parameter combinations, so the conditional flag matrix is proven to generate a coherent project rather than assumed to. |

## Build commands

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

### Maven profiles

| Profile | Purpose |
|---|---|
| `modules` | Active by default. Includes all 4 sub-modules. Disable with `-DskipModules=true`. |
| `sign-artifacts` | GPG-signs artifacts using `sign-maven-plugin`. |
| `release-dummy` | Deploys to local `/tmp/` directory for testing releases. |
| `release-judong` | Deploys to JUDO Nexus (`nexus.judo.technology`). |
| `release-central` | Deploys to Maven Central via Sonatype OSSRH with nexus-staging. |
| `generate-github-asciidoc-diagrams` | Renders AsciiDoc/PlantUML diagrams to PNG. |
| `update-source-code-license` | Updates EPL-2.0 license headers in all source files. |

## Technology stack

- **Java 21** — compiler source and target
- **Handlebars 4.1.2** (`com.github.jknack:handlebars`) — template engine for the `.hbs` files
- **`judo-generator-commons`** — `StaticMethodValueResolver`, `@TemplateHelper`, `@ContextAccessor`, `ThreadLocalContextHolder`
- **`hu.blackbelt.judo.meta.jsl.model`** — the JSL metamodel (`ModelDeclaration`, `TransferDeclaration`)
- **Spring Expression Language 5.0.0** — SpEL-based helper and condition expressions
- **Lombok 1.18.34** (`@Log`) · **SLF4J 2.0.16 / Logback 1.5.12** — logging

**Build & quality**

- Maven 3.9.4+ with wrapper (`mvnw` / `mvnw.cmd`)
- Apache Felix `maven-bundle-plugin` 5.1.9 — OSGi bundle packaging
- `flatten-maven-plugin` 1.5.0 — CI-friendly `${revision}` versioning
- Maven Surefire 3.5.1 — test execution with JDK 21 module opens
- JaCoCo 0.8.12 · SonarQube (`sonar-maven-plugin` 3.9.1.2184)
- Lombok Maven Plugin 1.18.20.0 — delombok for Javadoc generation

## Architecture pointers

- Each template module owns a `src/main/resources/fullstack-project.yaml` manifest listing every `.hbs` (or `copy: true`) template, its output `pathExpression`, optional file `permission`, `actorTypeBased` flag and `conditionExpression`. A new generated file starts there.
- The POM templates use a **fragment pattern**: `pom.xml.hbs` composes `pom.xml.*.fragment.hbs` parts (project definition, properties, dependency/plugin management, extra modules, distribution management, release profiles), so a consumer-visible POM change is usually a fragment change.
- `StoredVariableHelper` holds state in `ThreadLocalContextHolder` — thread-safe, but only if the context is bound and unbound around generation.
- Flag interdependencies are real constraints: `shouldGenerateSingleActorApplication()` throws `IllegalArgumentException` when combined with `generateWebrootModule`.
- Template parameters arrive through the consuming project's `judo-jsl-generator-maven-plugin` `<templateParameters>` configuration — almost all are mandatory.
- `judo-version-updater-maven-plugin` drives automated dependency version updates across the JUDO ecosystem.
- Build infrastructure: `.mvn/jvm.config` sets `-Xms1024m -Xmx2048m` plus JDK 21 module opens; `.mvn/extensions.xml` adds `wagon-file` and `wagon-webdav-jackrabbit`; the repo-root `logback-test.xml` configures test logging (console appender, INFO).
- [README.md](README.md) — usage guide with architecture diagram and example POM.
- [CONTRIBUTING.md](CONTRIBUTING.md) — development setup and submission guidelines.
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD pipeline documentation with flow diagrams.
- [judo-jsl-generator-maven-plugin](https://github.com/BlackBeltTechnology/judo-meta-jsl) — the plugin that consumes these templates.
- [judo-community CONTRIBUTING](https://github.com/BlackBeltTechnology/judo-community/blob/develop/CONTRIBUTING.adoc) — parent project contributing guide.

## Development environment

**Required:** Java 21 JDK · Maven 3.9.4+ (or `./mvnw`) · Docker (for the
generated project's development features).

**JVM flags for tests** (configured in surefire):

```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.time=ALL-UNNAMED
```

## Git workflow

- **Main branch:** `develop` · **Release branch:** `master` (latest released sources)
- **Versioning:** `${revision}` = `1.0.0-SNAPSHOT`; develop builds use `major.minor.qualifier.YYYYMMDD_HHmmss_commitId_branchName`
- **Branching model:** GitFlow — `feature/JNG-xxx`, `release/x.y`, `bugfix/JNG-xxx`, `support/JNG-xxx`, `hotfix/JNG-xxx`
- **Rule:** every commit must reference a JIRA ticket (`JNG-xxx`)

## Scope guard — invariants an edit must not break

1. **This project never runs as an application.** It is consumed as a Maven dependency by `judo-jsl-generator-maven-plugin`, which processes its templates — reason about changes in terms of the *generated* project, not this one.
2. **Conditional generation is the contract.** Every template entry's `conditionExpression` and every `shouldGenerate*()` flag exists because some consumer builds without that module. Adding an unconditional file forces it on everyone.
3. **Never speculate about code you have not opened.** Read the referenced file before answering; ground every claim in it.
4. **Keep changes minimal and DRY.** Smallest edit that works; a pattern repeated across templates belongs in a fragment or a shared helper.
5. **Implement test-first.** Express the expected generation outcome as a failing test in the test module, then make it pass.
6. **Check in before a major change.** Explain the plan and get it verified before large or structural edits, and summarise what changed after each step.

<!-- dox-doctrine -->
## Documentation Update Protocol (WRITE discipline)

Per-directory `AGENTS.md` files form a tree. Each directory `AGENTS.md` is the
per-file record for the files in that directory. This module-root `AGENTS.md`
holds doctrine + architecture pointers only — never a per-file index.

**Keep the root lean.** This file loads into every agent turn — every byte costs
tokens on every turn. A verbose root file buries the rules the model must follow
(signal dilution) and measurably degrades adherence; a lean file keeps doctrine
salient. Default assumption: your update does NOT belong in the root — route it
by the table below.

**Route every doc update by kind:**

| Kind of update | Goes in |
|---|---|
| New file in a directory, or its per-file detail / change history | Nearest directory `AGENTS.md`. Add a `` | `<basename>` | <purpose> | `` row, path-alphabetical. |
| Data flow, protocol, architecture rationale | `docs/architecture.md` or a `docs/<topic>.md` |
| End-user / developer setup | `README.md` |
| Cross-cutting rule every agent needs every turn (rare) | this module-root `AGENTS.md` |

**Read before editing (chain walk).** Before editing a file, read the nearest
`AGENTS.md` chain root→leaf so you know the file's recorded purpose, contracts,
and change history. Do not edit blind.

**Update after editing (closeout pass).** After changing a file, update its row
in the nearest directory `AGENTS.md`: find the file's row, update its purpose in
place; if absent, add it in path-alphabetical order. New directory → scaffold
its `AGENTS.md`. One row per file. The purpose carries a one-line summary, key
exported symbols, contracts/invariants, and `See change: <id>` history.

**Row style (caveman).** Short declarative fragments. Drop articles. Subject →
verb → object, present tense. One fact per row. Prefer concrete tokens (paths,
symbols, env vars) over prose. Keep identifiers verbatim.

**Size rule — split an over-large directory `AGENTS.md` file-based.** pi
auto-injects a directory `AGENTS.md` on every turn when cwd sits at/below it, so
an over-large directory `AGENTS.md` is not supported. Split it file-based: a row
exceeding the length threshold promotes to a per-file `<File>.AGENTS.md`
sidecar carrying that file's full detail (including every `See change:`). The
sidecar is pull-only — its name is not `AGENTS.md`, so pi never auto-injects it
— yet it stays search-indexed (`agents` doc_type). The directory `AGENTS.md`
keeps a one-line summary plus a `→ see `<File>.AGENTS.md`` pointer. Rows within
the threshold stay verbatim (lossless).

## Finding docs (READ discipline)

`kb_*` tools are faster and cheaper than raw search — they return a one-line
purpose + key exports per file, not raw bytes. **This fires on the ACTION, not
the intent** — before you `grep`/`rg` for a symbol, `cat`/read a file to learn
what it does, or chase an import, the kb call goes first. It fires **even
mid-task when you already know the file**; knowing the file does not exempt you.
When your reflex is the left column, run the right column instead:

| You're about to… | Do this FIRST instead |
|---|---|
| `grep -rn "SymbolName" src/` — find where a fn / type / const lives | `kb_search --doc-type agents "SymbolName"` — tree indexes key exports per file |
| `grep -rn "feature\|topic" src/` — how does X work / where's X handled | `kb_search "feature topic"` |
| `cat` / read a file just to learn its purpose before editing | `kb agents <path>` — one-line purpose + exports + change history |
| chase imports / callers across files | `kb_neighbors <path\|heading>` |
| read one doc section in full | `kb_get <path> <section>` |

**Fall-through (explicit):** if the kb call returns nothing relevant, `rg` /
source read is allowed — then add the missing directory `AGENTS.md` row per the
WRITE discipline. kb does NOT replace grep; it goes first.
