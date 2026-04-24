# CZIDLO

CZIDLO is a system for assigning, managing, and resolving URN:NBN identifiers.

The platform consists of a core backend API, an optional OAI-PMH provider, a separate administrative backend for the new frontend, and the frontend application itself.

## Main components

- `api` – core service for URN:NBN assignment and resolution
- `web-api` – backend for the new frontend and administrative operations
- `czidlo-frontend` – administrative UI, search, statistics, and manual editing
- `oaiPmhProvider` – optional OAI-PMH module
- PostgreSQL – primary database
- Elasticsearch – search and statistics backend for the web layer

## Documentation

- [Architecture](docs/architecture.md)
- [Installation](docs/installation.md)
- [Configuration](docs/configuration.md)
- [Core workflows](docs/workflows.md)

## Related repositories and references

- Backend repository: `NLCR/CZIDLO`
- Frontend repository: `https://github.com/NLCR/czidlo-frontend`
- `web-api` OpenAPI documentation: `https://resolver.nkp.cz/web-api/api-docs/`

## Legacy README

The original project documentation is kept in [README-OLD.md](docs/README-OLD.md).

It may still be useful as a historical or supplementary reference, but some parts may be outdated or inaccurate for current installations.
