# CZIDLO installation

## Overview

The recommended installation includes the following components:

1. PostgreSQL 14
2. Elasticsearch
3. `api` deployed to Tomcat 9
4. `web-api` deployed to Tomcat 11
5. optional `oaiPmhProvider` deployed to Tomcat 9
6. frontend deployed either as static assets or as a Dockerized frontend service
7. Apache HTTP Server configured as reverse proxy and static file server

A minimal installation for core API testing can omit:

- `web-api`
- frontend
- `oaiPmhProvider`
- Elasticsearch

However, such a minimal installation has important limitations:

- user administration is impractical without `web-api` and frontend
- passwords may need to be managed directly in the database
- search and statistics will not work without Elasticsearch
- asynchronous indexing attempts may fail and be logged

## Backend installation

### Build and deployment concept

The backend applications are packaged as WAR files and deployed to Tomcat.

Expected deployments:

- `api.war` -> `/api`
- `web-api.war` -> `/web-api`
- `oaiPmhProvider.war` -> `/oaiPmhProvider`

Tomcat versions:

- Tomcat 9 for `api` and `oaiPmhProvider`
- Tomcat 11 for `web-api`

All backend applications use Java 21.

### Database initialization

The primary database must be initialized before the applications are started.

The main initialization SQL script is:

```text
persistence/src/main/resources/initDatabase_6.0.sql
```

This script initializes the schema and creates initial application data.

It also creates an initial administrator account:

- login: `admin`
- password: `admin`

This default account is intended only for initial setup and should be changed immediately after installation.

### Required infrastructure dependencies

Before starting the backend applications, ensure that the following services are available and correctly configured:

- PostgreSQL 14
- Elasticsearch, if full functionality is required
- Apache Tomcat instances with matching Java version
- Apache HTTP Server or equivalent reverse proxy

### Notes on minimal setup

For local development or limited testing, the system can be started with PostgreSQL and `api` only.

This is enough to test core assignment and resolution behavior.

In such a setup:

- `web-api` is not required
- frontend is not required
- `oaiPmhProvider` is not required
- Elasticsearch is not required

But note again that:

- some asynchronous indexing operations will fail if Elasticsearch is absent
- search and statistics are unavailable
- administrative user management is limited

## Frontend installation

### Overview

The frontend is maintained in a separate repository:

- `https://github.com/NLCR/czidlo-frontend`

It depends primarily on:

- `web-api` for administrative operations
- selected public outputs from `api` for links to public JSON/XML records

### Static build served by Apache

This is the currently used deployment style.

In this case, the frontend is built and served as static assets, and runtime configuration is typically supplied in `assets/env.json`.

Example:

```json
{
  "useStaticRuntimeConfig": false,
  "devMode": false,
  "environmentName": "local npm run build & serve",
  "environmentCode": "l-nrbs",
  "czidloApiServiceBaseUrl": "https://web-api.czidlo.trinera.cloud/api",
  "czidloPublicApiBaseUrl": "https://api.czidlo.trinera.cloud/api/v6"
}
```

### Docker image

This is the preferred direction for future deployments.

For Docker-based and other alternative deployment methods, use the frontend repository and its documentation as the primary reference.
