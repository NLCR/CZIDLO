# CZIDLO architecture

## Overview

CZIDLO is a system for assigning, managing, and resolving URN:NBN identifiers.

The core of the platform is the `api` application. Other components extend it with administration, search, statistics, and interoperability features.

## Main components

### `api`

The `api` application is the core service of the system.

Responsibilities:

- URN:NBN reservation
- registration of digital documents
- management of digital instances
- resolution of URN:NBN and related identifiers
- public and authenticated REST API operations

Technology notes:

- Java 21
- Apache Tomcat 9
- legacy Jersey-based application
- limited Spring usage for basic endpoint authorization
- default context path: `/api`

### `web-api`

The `web-api` application is the backend for the new frontend.

Responsibilities:

- user and permission management
- registrar administration
- support for search and statistics shown in the frontend
- administrative functionality outside the public API

Technology notes:

- Java 21
- Apache Tomcat 11
- default context path: `/web-api`

`web-api` is not strictly required for a minimal API-only installation, but in practice it is strongly recommended because it provides a functional way to manage users, passwords, and administrative settings without editing the database directly.

### `czidlo-frontend`

The frontend is the main user interface for administrators and other authenticated users.

Responsibilities:

- login and authenticated access to the administrative UI
- user and permission management
- registrar-related management tasks
- search in indexed data
- statistics views
- manual data entry and editing
- links to public API outputs

Technology notes:

- Angular frontend
- deployed either as static build artifacts served by Apache HTTP Server, or via a Docker image

Frontend repository:

- `https://github.com/NLCR/czidlo-frontend`

### `oaiPmhProvider`

The `oaiPmhProvider` application is an optional module.

Responsibilities:

- OAI-PMH exposure of selected data
- interoperability with harvesting workflows where applicable

Technology notes:

- Java 21
- Apache Tomcat 9
- default context path: `/oaiPmhProvider`

## Persistence and indexing

### PostgreSQL

PostgreSQL 14 is the primary database of the system.

It stores:

- application data
- users
- passwords and salts
- permissions and registrar mappings
- document and identifier metadata

### Elasticsearch

Elasticsearch is the currently used indexing and search engine.

It supports in particular:

- search used by the web interface
- access statistics shown in the frontend
- assignment-related statistics
- indexed representations used by administrative and search features

Elasticsearch is recommended for current installations. It is not strictly required for the core API assignment and resolution workflows, but it is required for full web functionality such as search and statistics.

## Deployment model

A typical deployment is a single-server installation containing:

- Apache HTTP Server as the public entry point
- Tomcat 9 for `api` and `oaiPmhProvider`
- Tomcat 11 for `web-api`
- PostgreSQL
- Elasticsearch
- the frontend as static files or a containerized frontend deployment

Default context paths are:

- `/api`
- `/web-api`
- `/oaiPmhProvider`

The frontend is typically exposed separately, often under `/web` or on a dedicated host or subdomain depending on the environment.

## Example environment layouts

Depending on the environment, deployments may use either:

- one shared domain with Apache proxy rules and path-based routing, or
- multiple subdomains for individual applications.

Examples:

- `https://resolver.nkp.cz/...` for combined NKP deployments
- `https://api.czidlo.trinera.cloud/api/` for the core API
- `https://web-api.czidlo.trinera.cloud/...` for `web-api`
- `https://czidlo.test.trinera.cloud/` for the frontend

## Reverse proxy and public URLs

Apache HTTP Server is typically expected to:

- expose public URLs
- proxy Tomcat applications
- serve the frontend or its static assets
- provide stable public resolver URLs independent of backend implementation

A common pattern is to expose a stable resolver URL and map it internally to the core API, for example:

```text
/resolver/{URN:NBN} -> /api/v6/resolver/{URN:NBN}
```

In some environments, the resolver may also be exposed directly under:

```text
/urn:nbn:...
```
