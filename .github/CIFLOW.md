# CI/CD Flow: Development Versions and Branch Handling

This document describes the branching strategy, versioning policy, and GitHub Actions CI/CD pipelines used by this project.

## Branching Strategy

The project follows a [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) branching model.

```mermaid
gitGraph
    commit id: "init"
    branch develop
    checkout develop
    commit id: "dev-1"
    branch feature/JNG-1
    commit id: "feat-1a"
    commit id: "feat-1b"
    checkout develop
    merge feature/JNG-1 id: "merge-feat-1"
    branch feature/JNG-2
    commit id: "feat-2a"
    checkout develop
    merge feature/JNG-2 id: "merge-feat-2"
    branch release/1.0-beta1
    commit id: "rc-1"
    branch bugfix/JNG-3
    commit id: "fix-3"
    checkout release/1.0-beta1
    merge bugfix/JNG-3 id: "merge-fix-3"
    checkout develop
    merge release/1.0-beta1 id: "back-merge"
    checkout main
    merge release/1.0-beta1 id: "release-1.0"
```

### Branch Types

| Branch | Base | Purpose |
|--------|------|---------|
| `develop` | — | Main development branch; contains latest development sources |
| `feature/JNG-xxx_summary` | `develop` | New features for the next release |
| `release/x.y-qualifier` | `develop` | Release stabilization and testing |
| `bugfix/JNG-xxx_summary` | `release/*` | Bug fixes during release testing (applied to release and newer develop) |
| `support/JNG-xxx_summary` | `release/*` | Minor changes for a previous release; merged back to release branch |
| `hotfix/JNG-xxx_summary` | `master` | Critical fixes applied to both release and master |
| `master` | — | Latest released sources |

## Version Numbers

Versioning follows semantic versioning with these rules:

| Event | Version change |
|-------|---------------|
| Start a `feature/` branch | No change |
| Start a `release/` branch | 2nd number on `develop` is incremented |
| Start a `bugfix/` branch | No change (applied to release branch) |
| Start a `support/` branch | 3rd number is incremented |
| Start a `hotfix/` branch | 4th number is incremented |

### Dynamic Version Format

On `develop` and `increment/*` branches, versions are computed dynamically:

```
major.minor.qualifier.YYYYMMDD_HHmmss_commitId_branchName
```

On `master` and `release/*` branches, the version comes directly from `pom.xml` (without `-SNAPSHOT`).

## GitHub Actions Workflows

### build.yml — Main Build Pipeline

Triggered on pushes to `develop` and pull requests targeting `develop`, `master`, `increment/*`, or `release/*`.

```mermaid
flowchart TD
    A[Push/PR event] --> B{Base branch?}
    B -->|master, release/*| C[Version from pom.xml<br/>without -SNAPSHOT]
    B -->|develop, increment/*| D[Dynamic version<br/>major.minor.qualifier.date_commitId_branch]
    C --> E[Build and deploy to Nexus]
    D --> E
    E --> F[Create git tag v&lt;version&gt;]
    F --> G{Base branch?}
    G -->|increment/*, release/*| H[Create merge-pr/&lt;version&gt; tag]
    H --> I["Trigger merge-pr-tagged.yml"]
    G -->|develop| J[Build changelog]
    J --> K[Create GitHub prerelease]
```

### merge-pr-tagged.yml — PR Merge Automation

Triggered when a `merge-pr/*` tag is pushed.

```mermaid
flowchart TD
    A[merge-pr/* tag pushed] --> B[Extract version from tag]
    B --> C{Version format?}
    C -->|major.minor.qualifier| D[Merge PR to master]
    D --> E["Trigger create-release-on-master.yml"]
    C -->|other| F[Squash PR to develop]
    F --> G["Trigger build.yml"]
    D --> H[Delete merge-pr tag]
    F --> H
```

### create-release-on-master.yml — Release Creation

Triggered on pushes to `master`.

```mermaid
flowchart TD
    A[Push to master] --> B[Get version from tag]
    B --> C[Build changelog]
    C --> D[Create GitHub release<br/>marked as latest]
```

### release.yml — Release Orchestration

Manually triggered with a version parameter (`auto` or `major.minor.qualifier`).

```mermaid
flowchart TD
    A[Manual trigger with version] --> B{Version = auto?}
    B -->|yes| C[Read version from pom.xml<br/>remove -SNAPSHOT]
    B -->|no| D[Use given version]
    C --> E[Set release version]
    D --> E
    E --> F[Set next version = qualifier + 1]
    F --> G[Create PR to master<br/>with release version]
    F --> H[Create PR to develop<br/>with next version]
    G --> I["Trigger build.yml"]
    H --> I
```

### Other Workflows

| Workflow | Purpose |
|----------|---------|
| `bump-version.yml` | Automated version management |
| `build-dependabot.yml` | Dependabot PR integration |
| `delete-old-draft-releases.yml` | Cleanup of stale draft releases |
| `jira-description-to-pr.yml` | Syncs JIRA issue descriptions to PR bodies |
| `sync-labels.yml` | Synchronizes repository labels |

## Development Rules

> **Important:** There is no commit without a ticket number. Every pull request and commit must reference a JIRA ticket (e.g., `JNG-xxx`).

Issue tracking is done via [JIRA](https://blackbelt.atlassian.net/jira/dashboards).
