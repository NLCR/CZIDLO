# CZIDLO configuration

## Overview

This document describes the main configuration areas of the system. It focuses on the effective runtime configuration and points to the relevant property files and frontend configuration.

## Core API configuration (`api`)

The sample properties file for the core API should be used as the baseline reference.

A typical deployment places the effective configuration in:

```text
~/.czidlo/api.properties
```

Typical configuration groups include:

### Database configuration

- JDBC URL
- database username
- database password
- persistence-related settings

### Resolver and public URL settings

- base URL settings
- resolver-facing URLs
- links used in generated outputs

### Security and authentication

- endpoint protection settings
- administrative or service-related access constraints

### Feature and application behavior settings

- registration behavior
- resolver-related settings
- instance-specific modes

### Elasticsearch integration

- cluster or node URL
- index names
- indexing configuration
- connection settings

## `web-api` configuration

The sample properties file for `web-api` should be used as the primary reference.

A typical deployment places the effective configuration in:

```text
~/.czidlo/web-api.properties
```

Typical configuration groups include:

- database access
- API integration settings
- Elasticsearch connection
- application URLs
- CORS or frontend-related access configuration where relevant
- administrative defaults and environment-dependent values

Because `web-api` powers the frontend, its URL configuration and Elasticsearch integration are particularly important.

## `oaiPmhProvider` configuration

The sample properties file for `oaiPmhProvider` should be used as the baseline reference.

A typical deployment places the effective configuration in:

```text
~/.czidlo/oaiPmhProvider.properties
```

Typical configuration groups include:

- database access
- OAI-PMH service configuration
- exposed endpoints and metadata behavior
- environment-dependent URLs

## Frontend configuration

The essential frontend values are:

- `web-api` base URL
- `api` base URL

For manually deployed frontend builds, configuration is typically provided in `assets/env.json`, for example:

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

## Authentication and transport security

CZIDLO uses its own local user database.

Passwords are stored as salted hashes.

Authentication for the public API and web-related administrative communication is based on HTTP Basic Authentication.

Because credentials are sent using Basic Auth, CZIDLO must be deployed behind HTTPS.

This is mandatory for production use.
